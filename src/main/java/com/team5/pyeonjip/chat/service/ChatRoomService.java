package com.team5.pyeonjip.chat.service;

import com.team5.pyeonjip.chat.dto.ChatRoomDto;
import com.team5.pyeonjip.chat.entity.ChatRoom;
import com.team5.pyeonjip.chat.entity.ChatRoomStatus;
import com.team5.pyeonjip.chat.mapper.ChatRoomMapper;
import com.team5.pyeonjip.chat.repository.ChatRoomRepository;
import com.team5.pyeonjip.global.exception.ErrorCode;
import com.team5.pyeonjip.global.exception.GlobalException;
import com.team5.pyeonjip.user.entity.User;
import com.team5.pyeonjip.user.repository.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ChatRoomService {

    private final ChatRoomRepository chatRoomRepository;
    private final UserRepository userRepository;
    private final ChatRoomMapper chatRoomMapper;
    private final SimpMessagingTemplate messagingTemplate;


    public List<ChatRoomDto> getChatRooms(){
        List<ChatRoom> chatRooms = chatRoomRepository.findAll();

        List<ChatRoomDto> chatRoomDtos = new ArrayList<>();

        for (ChatRoom chatRoom : chatRooms) {
            chatRoomDtos.add(chatRoomMapper.toDTO(chatRoom));
        }
        return chatRoomDtos;
    }

    public ChatRoomDto getChatRoomById(Long roomId) {
        ChatRoom chatRoom = chatRoomRepository.findById(roomId)
                .orElseThrow(() -> new GlobalException(ErrorCode.CHAT_ROOM_NOT_FOUND));
        return chatRoomMapper.toDTO(chatRoom);
    }

    public ChatRoomDto createWaitingRoom(String category, String userEmail) {
        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new GlobalException(ErrorCode.USER_NOT_FOUND));

        ChatRoom chatRoom = ChatRoom.builder()
                .category(category)
                .status(ChatRoomStatus.WAITING)
                .user(user)
                .build();

        ChatRoom savedChatRoom = chatRoomRepository.save(chatRoom);
        return chatRoomMapper.toDTO(savedChatRoom);
    }

    @Transactional
    public ChatRoomDto activateChatRoom(Long chatRoomId, String adminEmail) {
        ChatRoom chatRoom = chatRoomRepository.findById(chatRoomId)
                .orElseThrow(() -> new GlobalException(ErrorCode.CHAT_ROOM_NOT_FOUND));

        if (chatRoom.getStatus() != ChatRoomStatus.WAITING) {
            throw new GlobalException(ErrorCode.WAITING_ROOM_ACTIVATE);
        }

        User admin = userRepository.findByEmail(adminEmail)
                .orElseThrow(() -> new GlobalException(ErrorCode.USER_NOT_FOUND));

        User user = chatRoom.getUser();

        chatRoom.updateStatus(ChatRoomStatus.ACTIVE);
        chatRoom.updateAdmin(admin);

        ChatRoom updatedChatRoom = chatRoomRepository.save(chatRoom);
        ChatRoomDto updatedRoomDto = chatRoomMapper.toDTO(updatedChatRoom);

        updatedRoomDto.setUserEmail(user.getEmail());

        // 사용자에게 채팅방 활성화 알림
        messagingTemplate.convertAndSendToUser(
                chatRoom.getUser().getEmail(),
                "/queue/chat-room-activated",
                updatedRoomDto
        );

        return updatedRoomDto;
    }


    private void notifyAdminsNewWaitingRoom(ChatRoom chatRoom) {
        messagingTemplate.convertAndSend("/topic/admin/waiting-rooms", chatRoom);
    }

    private void notifyUserRoomActivated(ChatRoomDto chatRoom) {
        if (chatRoom.getUserEmail() == null) {
            throw new GlobalException(ErrorCode.EMAIL_ALREADY_EXISTS);
        }
        messagingTemplate.convertAndSendToUser(
                chatRoom.getUserEmail(),
                "/queue/chat-room-activated",
                chatRoom
        );
    }

    public List<ChatRoomDto> getChatRoomsByUserEmail(String email){
        List<ChatRoom> chatRooms = chatRoomRepository.findByUserEmail(email);

        List<ChatRoomDto> chatRoomDtos = new ArrayList<>();
        for (ChatRoom chatRoom : chatRooms) {
            chatRoomDtos.add(chatRoomMapper.toDTO(chatRoom));
        }
        return chatRoomDtos;
    }

    public List<ChatRoomDto> getWaitingChatRooms() {
        List<ChatRoom> waitingRooms = chatRoomRepository.findByStatus(ChatRoomStatus.WAITING);
        return waitingRooms.stream()
                .map(chatRoomMapper::toDTO)
                .collect(Collectors.toList());
    }
}
