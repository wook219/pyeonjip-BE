package com.team5.pyeonjip.chat.service;

import com.team5.pyeonjip.chat.dto.ChatMessageDto;
import com.team5.pyeonjip.chat.dto.ChatRoomDto;
import com.team5.pyeonjip.chat.entity.ChatMessage;
import com.team5.pyeonjip.chat.entity.ChatRoom;
import com.team5.pyeonjip.chat.mapper.ChatMessageMapper;
import com.team5.pyeonjip.chat.mapper.ChatRoomMapper;
import com.team5.pyeonjip.chat.repository.ChatMessageRepository;
import com.team5.pyeonjip.chat.repository.ChatRoomRepository;
import com.team5.pyeonjip.global.exception.ErrorCode;
import com.team5.pyeonjip.global.exception.GlobalException;
import com.team5.pyeonjip.global.exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class ChatMessageService {

    private final ChatMessageRepository chatMessageRepository;
    private final ChatRoomRepository chatRoomRepository;
    private final ChatMessageMapper chatMessageMapper;

    public List<ChatMessageDto> getChatMessagesByChatRoomId(Long chatRoomId){
        List<ChatMessage> chatMessages = chatMessageRepository.findByChatRoomId(chatRoomId);
        List<ChatMessageDto> chatMessageDtos = new ArrayList<>();

        for (ChatMessage chatMessage : chatMessages) {
            chatMessageDtos.add(chatMessageMapper.toDTO(chatMessage));
        }

        return chatMessageDtos;
    }

    public ChatMessageDto sendMessage(Long chatRoomId, String message, String senderEmail) {
        ChatRoom chatRoom = chatRoomRepository.findById(chatRoomId)
                .orElseThrow(() -> new GlobalException(ErrorCode.CHAT_ROOM_NOT_FOUND));

        ChatMessage chatMessage = ChatMessage.builder()
                .chatRoom(chatRoom)
                .message(message)
                .senderEmail(senderEmail)
                .build();

        chatMessage = chatMessageRepository.save(chatMessage);

        return chatMessageMapper.toDTO(chatMessage);
    }

    @Transactional
    public ChatMessageDto updateMessage(Long messageId, String message, String senderEmail) {
        ChatMessage chatMessage = chatMessageRepository.findById(messageId)
                .orElseThrow(() -> {
                    log.error("Message not found with ID: {}", messageId);
                    return new GlobalException(ErrorCode.CHAT_MESSAGE_NOT_FOUND);
                });

        if (!chatMessage.getSenderEmail().equals(senderEmail)) {
            throw new GlobalException(ErrorCode.UNAUTHORIZED_MESSAGE_MODIFICATION);
        }

        chatMessage.updateMessage(message);
        chatMessageRepository.save(chatMessage);

        return chatMessageMapper.toDTO(chatMessage);
    }

    @Transactional
    public Long deleteMessage(Long messageId, String senderEmail) {
        ChatMessage chatMessage = chatMessageRepository.findById(messageId)
                .orElseThrow(() -> {
                    log.error("Message not found with ID: {}", messageId);
                    return new GlobalException(ErrorCode.CHAT_MESSAGE_NOT_FOUND);
                });

        if (!chatMessage.getSenderEmail().equals(senderEmail)) {
            throw new GlobalException(ErrorCode.UNAUTHORIZED_MESSAGE_DELETION);
        }

        chatMessageRepository.delete(chatMessage);

        return messageId;
    }
}
