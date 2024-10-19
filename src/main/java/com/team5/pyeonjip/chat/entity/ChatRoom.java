package com.team5.pyeonjip.chat.entity;

import com.team5.pyeonjip.global.entity.BaseTimeEntity;
import com.team5.pyeonjip.user.entity.User;
import jakarta.persistence.*;
import lombok.*;

import java.sql.Timestamp;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "chat_room")
public class ChatRoom extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "category", nullable = false)
    private String category;

    @Enumerated(EnumType.STRING)
    @Column(name = "status")
    private ChatRoomStatus status;

    @Column(name = "closed_at")
    private Timestamp closedAt;

//    @Column(name = "user_id", nullable = false)
//    private Long userId;
//
//    @Column(name = "admin_id", nullable = false)
//    private Long adminId;

    @ManyToOne
    @JoinColumn(name = "user_id",referencedColumnName = "id", nullable = false)
    private User user;

    @ManyToOne
    @JoinColumn(name = "admin_id", referencedColumnName = "id", nullable = true)
    private User admin;

    public void updateAdmin(User admin){
        this.admin = admin;
    }

    public void updateStatus(ChatRoomStatus status){
        this.status = status;
    }

    public void activateRoom(User admin) {
        this.admin = admin;
        this.status = ChatRoomStatus.ACTIVE;
    }

    // 채팅방 종료 상태 업데이트
    public void closeRoom() {
        this.status = ChatRoomStatus.CLOSED;
    }

}
