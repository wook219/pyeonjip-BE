package com.team5.pyeonjip.chat.controller;

import com.team5.pyeonjip.chat.dto.ChatMessageDto;
import com.team5.pyeonjip.chat.service.ChatMessageService;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

import java.security.Principal;

@Controller
@RequiredArgsConstructor
public class ChatMessageController {

    private final ChatMessageService chatMessageService;
    private final SimpMessagingTemplate messagingTemplate;

    @MessageMapping("/chat.sendMessage/{chatRoomId}")
    public void sendMessage(@DestinationVariable("chatRoomId") Long chatRoomId,
                            @Payload ChatMessageDto message,
                            @Header("simpUser") Principal principal) {
        String senderEmail = principal.getName();
        ChatMessageDto createdMessage = chatMessageService.sendMessage(chatRoomId, message.getMessage(), senderEmail);
        messagingTemplate.convertAndSend("/topic/messages/" + chatRoomId, createdMessage);
    }

    @MessageMapping("/chat.updateMessage/{chatRoomId}")
    public void updateMessage(@DestinationVariable("chatRoomId") Long chatRoomId,
                              @Payload ChatMessageDto message,
                              @Header("simpUser") Principal principal) {
        String senderEmail = principal.getName();
        ChatMessageDto updatedMessage = chatMessageService.updateMessage(message.getId(), message.getMessage(), senderEmail);
        messagingTemplate.convertAndSend("/topic/message-updates/" + chatRoomId, updatedMessage);
    }

    @MessageMapping("/chat.deleteMessage/{chatRoomId}")
    public void deleteMessage(@DestinationVariable("chatRoomId") Long chatRoomId,
                              @Payload ChatMessageDto message,
                              @Header("simpUser") Principal principal) {
        String senderEmail = principal.getName();
        Long deletedMessageId = chatMessageService.deleteMessage(message.getId(), senderEmail);
        messagingTemplate.convertAndSend("/topic/message-deletions/" + chatRoomId, deletedMessageId);
    }
}
