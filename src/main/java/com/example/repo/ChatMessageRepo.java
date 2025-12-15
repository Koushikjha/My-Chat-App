package com.example.repo;

import com.example.model.ChatMessage;
import com.example.model.MessageStatus;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ChatMessageRepo extends JpaRepository<ChatMessage,Integer> {

    @Query("""
    SELECT m FROM ChatMessage m
    WHERE
      (m.sender = :u1 AND m.receiver = :u2)
       OR
      (m.sender = :u2 AND m.receiver = :u1)
    ORDER BY m.createdAt DESC
    """)
    List<ChatMessage> findLastMessages(
            @Param("u1") String user1,
            @Param("u2") String user2,
            Pageable pageable
    );

    @Query("""
    SELECT m FROM ChatMessage m
    WHERE
      (m.sender = :u1 AND m.receiver = :u2)
       OR
      (m.sender = :u2 AND m.receiver = :u1)
    ORDER BY m.createdAt DESC
    """)
    List<ChatMessage> findChatMessages(@Param("user1") String u1,
                                   @Param("user2") String u2,
                                   Pageable pageable);

    List<ChatMessage> findByReceiverAndStatus(String receiver, MessageStatus status);
    List<ChatMessage> findBySenderAndReceiverAndStatus(String sender, String receiver, MessageStatus status);
}
