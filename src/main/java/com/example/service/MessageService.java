package com.example.service;

import com.example.model.ChatMessage;
import com.example.model.MessageStatus;
import com.example.repo.ChatMessageRepo;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

@Service
public class MessageService {
    private final ChatMessageRepo repo;

    public MessageService(ChatMessageRepo repo) {
        this.repo = repo;
    }

    public void saveMessage(String content, String sender) {
        ChatMessage chatMessage = new ChatMessage();
        chatMessage.setContent(content);
        chatMessage.setSender(sender);
        chatMessage.setReceiver("public");
        chatMessage.setCreatedAt(LocalDateTime.now());
        chatMessage.setStatus(MessageStatus.SENT);
        repo.save(chatMessage);
    }

    public void savePrivateMessage(String content, String sender, String reciever) {
        ChatMessage chatMessage=new ChatMessage();
        chatMessage.setContent(content);
        chatMessage.setSender(sender);
        chatMessage.setReceiver(reciever);
        chatMessage.setCreatedAt(LocalDateTime.now());
        chatMessage.setStatus(MessageStatus.SENT);

        repo.save(chatMessage);
    }
    public void markMessagesAsDelivered(String username) {
        List<ChatMessage> messages = repo.findByReceiverAndStatus(username, MessageStatus.SENT);
        for(ChatMessage m : messages){
            m.setStatus(MessageStatus.DELIVERED);
        }
        repo.saveAll(messages);
    }
    public void markMessagesAsSeen(String receiver, String sender) {
        List<ChatMessage> messages = repo.findBySenderAndReceiverAndStatus(sender, receiver, MessageStatus.DELIVERED);
        for(ChatMessage m : messages){
            m.setStatus(MessageStatus.SEEN);
        }
        repo.saveAll(messages);
    }
    public List<ChatMessage> getLastMessages(String user1, String user2) {
        Pageable limit = PageRequest.of(0, 15);
        List<ChatMessage> messages =
                repo.findLastMessages(user1, user2, limit);

        Collections.reverse(messages); // oldest → newest
        return messages;
    }

    public List<ChatMessage> getChatMessages(String user1, String user2, Pageable pageable) {
        return repo.findLastMessages(user1, user2, pageable);
    }


}
