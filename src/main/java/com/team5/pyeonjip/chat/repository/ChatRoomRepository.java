package com.team5.pyeonjip.chat.repository;

import com.team5.pyeonjip.chat.entity.ChatRoom;
import com.team5.pyeonjip.chat.entity.ChatRoomStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ChatRoomRepository extends JpaRepository<ChatRoom, Long> {
    List<ChatRoom> findByUserId(Long userId);

    // 추가된 메서드
    List<ChatRoom> findByStatus(ChatRoomStatus status);

    List<ChatRoom> findByUserEmail(String email);
}
