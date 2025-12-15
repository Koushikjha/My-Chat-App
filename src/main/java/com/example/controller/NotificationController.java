package com.example.controller;


import com.example.model.ChatMessage;
import com.example.model.MessageStatus;
import com.example.service.JwtUtil;
import com.example.service.MessageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

@Controller
public class NotificationController {

    @Autowired
    private JwtUtil jwtUtil;


    private final MessageService messageService;
    private final SimpMessagingTemplate simpMessagingTemplate;

    public NotificationController(MessageService messageService,SimpMessagingTemplate simpMessagingTemplate) {
        this.messageService = messageService;
        this.simpMessagingTemplate=simpMessagingTemplate;
    }
    @MessageMapping("/sendMessage")
    @SendTo("/topic/notifications")
    public ChatMessage publicChat(ChatMessage message, SimpMessageHeaderAccessor headers){
        String cookieHeader=(String)headers
                .getSessionAttributes()
                .get("cookie");
        String username=extractUsernameFromCookie(cookieHeader);
        message.setSender(username);
        messageService.saveMessage(message.getContent(),username);
        return message;
    }
    @MessageMapping("/privateMessage")
    public void privateChat(ChatMessage message, SimpMessageHeaderAccessor headers) {

        String cookieHeader = (String) headers
                .getSessionAttributes()
                .get("cookie");

        String sender = extractUsernameFromCookie(cookieHeader);
        message.setSender(sender);

        messageService.savePrivateMessage(
                message.getContent(),
                sender,
                message.getReceiver()
        );

        simpMessagingTemplate.convertAndSend("/topic/private-" + message.getReceiver(), message);
        simpMessagingTemplate.convertAndSend("/topic/private-" + sender, message);
    }


    private String extractUsernameFromCookie(String cookieHeader) {
        if(cookieHeader==null){
            return "anonymous";
        }
        for (String cookie : cookieHeader.split(";")) {
            cookie = cookie.trim();
            if (cookie.startsWith("JWT_TOKEN=")) {
                String token = cookie.substring("JWT_TOKEN=".length());
                return jwtUtil.extractUsername(token);
            }
        }
        return "anonymous";
    }
}

