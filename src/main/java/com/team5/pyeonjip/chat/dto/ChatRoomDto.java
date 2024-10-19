package com.team5.pyeonjip.chat.dto;

import com.team5.pyeonjip.chat.entity.ChatRoomStatus;
import lombok.*;

import java.sql.Time;
import java.sql.Timestamp;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ChatRoomDto {
    private Long id;
    private String category;
    private ChatRoomStatus status;
    private Long userId;
    private String userEmail;
    private Long adminId;
    private String adminEmail;
    private Timestamp createdAt;
}
