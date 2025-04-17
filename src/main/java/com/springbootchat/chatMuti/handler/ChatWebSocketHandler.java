package com.springbootchat.chatMuti.handler;

import org.springframework.stereotype.Component;
import org.springframework.web.socket.*;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.io.IOException;
import java.util.*;
import java.util.concurrent.*;
import java.util.stream.Collectors;

@Component
public class ChatWebSocketHandler extends TextWebSocketHandler {

    private final Map<String, WebSocketSession> userSessions = new ConcurrentHashMap<>();
    private final Map<String, String> sessionIdToUserId = new ConcurrentHashMap<>();
    private final Map<String, String> userIdToUsername = new ConcurrentHashMap<>();

    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
        String payload = message.getPayload();

        if (payload.startsWith("/username ")) {
            String username = payload.substring(10).trim();
            String userId = UUID.randomUUID().toString();

            session.getAttributes().put("userid", userId);
            session.getAttributes().put("username", username);

            userSessions.put(userId, session);
            sessionIdToUserId.put(session.getId(), userId);
            userIdToUsername.put(userId, username);

            sendMessage(session, "id:" + userId);
            broadcast("🔔 " + username + " đã tham gia", userId);
            sendUserListToAll();
        } else if (payload.startsWith("/to ")) {
            String[] parts = payload.split(" ", 3);
            if (parts.length < 3) return;

            String toUserId = parts[1];
            String msg = parts[2];

            String fromUserId = (String) session.getAttributes().get("userid");
            String fromUsername = (String) session.getAttributes().get("username");

            WebSocketSession receiver = userSessions.get(toUserId);

            if (receiver != null && receiver.isOpen()) {
                sendMessage(receiver, "💌 Từ " + fromUsername + ": " + msg);
                sendMessage(session, "📤 Đến " + userIdToUsername.get(toUserId) + ": " + msg);
            } else {
                sendMessage(session, "⚠️ Người dùng không tồn tại hoặc đã offline");
            }
        } else {
            String userId = (String) session.getAttributes().get("userid");
            String username = (String) session.getAttributes().get("username");
            broadcast("💬 " + username + ": " + payload, userId);
        }
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) {
        String userId = sessionIdToUserId.remove(session.getId());
        if (userId != null) {
            userSessions.remove(userId);
            String username = userIdToUsername.remove(userId);
            broadcast("❌ " + username + " đã thoát", userId);
            sendUserListToAll();
        }
    }

    private void broadcast(String message, String excludeUserId) {
        userSessions.forEach((id, session) -> {
            if (!id.equals(excludeUserId)) sendMessage(session, message);
        });
    }

    private void sendUserListToAll() {
        // Gửi danh sách dạng: userId|username,userId2|username2,...
        String userList = userSessions.keySet().stream()
                .map(id -> id + "|" + userIdToUsername.get(id))
                .collect(Collectors.joining(","));
        for (WebSocketSession session : userSessions.values()) {
            sendMessage(session, "/users " + userList);
        }
    }

    private void sendMessage(WebSocketSession session, String msg) {
        try {
            session.sendMessage(new TextMessage(msg));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
