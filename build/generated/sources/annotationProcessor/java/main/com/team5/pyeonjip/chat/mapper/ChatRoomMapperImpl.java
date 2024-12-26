package com.team5.pyeonjip.chat.mapper;

import com.team5.pyeonjip.chat.dto.ChatRoomDto;
import com.team5.pyeonjip.chat.entity.ChatRoom;
import com.team5.pyeonjip.user.entity.User;
import javax.annotation.processing.Generated;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2024-12-26T22:43:10+0900",
    comments = "version: 1.5.5.Final, compiler: IncrementalProcessingEnvironment from gradle-language-java-8.10.2.jar, environment: Java 21 (Oracle Corporation)"
)
@Component
public class ChatRoomMapperImpl implements ChatRoomMapper {

    @Override
    public ChatRoomDto toDTO(ChatRoom chatRoom) {
        if ( chatRoom == null ) {
            return null;
        }

        ChatRoomDto chatRoomDto = new ChatRoomDto();

        chatRoomDto.setAdminEmail( chatRoomAdminEmail( chatRoom ) );
        chatRoomDto.setUserEmail( chatRoomUserEmail( chatRoom ) );
        chatRoomDto.setUserId( chatRoomUserId( chatRoom ) );
        chatRoomDto.setAdminId( chatRoomAdminId( chatRoom ) );
        chatRoomDto.setCreatedAt( chatRoom.getCreatedAt() );
        chatRoomDto.setId( chatRoom.getId() );
        chatRoomDto.setCategory( chatRoom.getCategory() );
        chatRoomDto.setStatus( chatRoom.getStatus() );

        return chatRoomDto;
    }

    private String chatRoomAdminEmail(ChatRoom chatRoom) {
        if ( chatRoom == null ) {
            return null;
        }
        User admin = chatRoom.getAdmin();
        if ( admin == null ) {
            return null;
        }
        String email = admin.getEmail();
        if ( email == null ) {
            return null;
        }
        return email;
    }

    private String chatRoomUserEmail(ChatRoom chatRoom) {
        if ( chatRoom == null ) {
            return null;
        }
        User user = chatRoom.getUser();
        if ( user == null ) {
            return null;
        }
        String email = user.getEmail();
        if ( email == null ) {
            return null;
        }
        return email;
    }

    private Long chatRoomUserId(ChatRoom chatRoom) {
        if ( chatRoom == null ) {
            return null;
        }
        User user = chatRoom.getUser();
        if ( user == null ) {
            return null;
        }
        Long id = user.getId();
        if ( id == null ) {
            return null;
        }
        return id;
    }

    private Long chatRoomAdminId(ChatRoom chatRoom) {
        if ( chatRoom == null ) {
            return null;
        }
        User admin = chatRoom.getAdmin();
        if ( admin == null ) {
            return null;
        }
        Long id = admin.getId();
        if ( id == null ) {
            return null;
        }
        return id;
    }
}
