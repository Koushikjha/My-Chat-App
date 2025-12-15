package com.example.controller;

import com.example.model.ChatMessage;
import com.example.service.MessageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Collections;
import java.util.List;

@RestController
@RequestMapping("/messages")
public class MessageController {

    @Autowired
    private MessageService messageService;

    @GetMapping("/markDelivered")
    public void markDelivered(@RequestParam String username) {
        messageService.markMessagesAsDelivered(username);
    }

    @GetMapping("/markSeen")
    public void markSeen(@RequestParam String sender,
                                  @RequestParam String receiver) {
        messageService.markMessagesAsSeen(receiver, sender);
    }

    @GetMapping("/history")
    public List<ChatMessage> getChatHistory(
            @RequestParam String user1,
            @RequestParam String user2
    ) {
        return messageService.getLastMessages(user1, user2);
    }

    @GetMapping("/load")
    public List<ChatMessage> loadMessages(@RequestParam String sender,
                                      @RequestParam String receiver,
                                      @RequestParam int page,
                                      @RequestParam int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
        List<ChatMessage> messages = messageService.getChatMessages(sender, receiver, pageable);
        Collections.reverse(messages); // oldest first
        return messages;
    }

}
