package com.springbootchat.chatMuti.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;

@Controller
public class ChatController {

    @GetMapping("/")
    public String home() {
        return "chat";  // Trả về trang chat.html
    }
    @GetMapping("/api/users")
    public List<String> getOnlineUsers() {
        return List.of("Client 1", "Client 2");
    }
}