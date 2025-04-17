package com.springbootchat.chatMuti.controller;

import com.springbootchat.chatMuti.handler.ChatWebSocketHandler;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RequestMapping("/api")
@RestController
public class ChatController {
    @GetMapping("/")
    public String home() {
        return "chat";  // Trả về trang chat.html
    }
    @PostMapping("/set-username")
    public String setUsername(@RequestBody Map<String, String> payload, HttpSession session) {
        String username = payload.get("username");
        session.setAttribute("username", username);
        return "Username saved to session: " + username;
    }
}