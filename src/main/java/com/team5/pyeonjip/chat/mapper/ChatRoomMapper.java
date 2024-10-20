package com.team5.pyeonjip.chat.mapper;

import com.team5.pyeonjip.chat.dto.ChatRoomDto;
import com.team5.pyeonjip.chat.entity.ChatRoom;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface ChatRoomMapper {

    // Entity -> DTO
    @Mapping(source = "admin.email", target = "adminEmail")
    @Mapping(source = "user.email", target = "userEmail")
    @Mapping(source = "user.id", target = "userId") // User 객체의 ID를 userId로 매핑
    @Mapping(source = "admin.id", target = "adminId") // User 객체의 ID를 adminId로 매핑 (관리자와 사용자 모두 User)
    @Mapping(source = "createdAt", target = "createdAt")
    ChatRoomDto toDTO(ChatRoom chatRoom);
}
