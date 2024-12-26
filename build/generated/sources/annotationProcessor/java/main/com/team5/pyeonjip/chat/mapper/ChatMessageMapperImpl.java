package com.team5.pyeonjip.chat.mapper;

import com.team5.pyeonjip.chat.dto.ChatMessageDto;
import com.team5.pyeonjip.chat.entity.ChatMessage;
import com.team5.pyeonjip.chat.entity.ChatRoom;
import javax.annotation.processing.Generated;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2024-12-26T20:37:30+0900",
    comments = "version: 1.5.5.Final, compiler: IncrementalProcessingEnvironment from gradle-language-java-8.10.2.jar, environment: Java 21 (Oracle Corporation)"
)
@Component
public class ChatMessageMapperImpl implements ChatMessageMapper {

    @Override
    public ChatMessageDto toDTO(ChatMessage chatMessage) {
        if ( chatMessage == null ) {
            return null;
        }

        ChatMessageDto.ChatMessageDtoBuilder chatMessageDto = ChatMessageDto.builder();

        chatMessageDto.chatRoomId( chatMessageChatRoomId( chatMessage ) );
        chatMessageDto.id( chatMessage.getId() );
        chatMessageDto.senderEmail( chatMessage.getSenderEmail() );
        chatMessageDto.message( chatMessage.getMessage() );

        return chatMessageDto.build();
    }

    private Long chatMessageChatRoomId(ChatMessage chatMessage) {
        if ( chatMessage == null ) {
            return null;
        }
        ChatRoom chatRoom = chatMessage.getChatRoom();
        if ( chatRoom == null ) {
            return null;
        }
        Long id = chatRoom.getId();
        if ( id == null ) {
            return null;
        }
        return id;
    }
}
