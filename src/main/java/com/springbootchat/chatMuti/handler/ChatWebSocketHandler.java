package com.springbootchat.chatMuti.handler;

import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.util.concurrent.CopyOnWriteArrayList;

@Component
public class ChatWebSocketHandler extends TextWebSocketHandler {

    private final CopyOnWriteArrayList <WebSocketSession> sessions = new CopyOnWriteArrayList<>();

    private static int clientCount = 1;
    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        String username = "Client " + clientCount++;
        session.getAttributes().put("username", username);
        // Thêm vào danh sách các session đang kết nối
        sessions.add(session);
        // Gửi thông báo đến tất cả client
        broadcast("✅ " + username + " đã tham gia.", username);
    }


    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
        String username = (String) session.getAttributes().get("username");
        broadcast("💬 " + username + ": " + message.getPayload(), username);
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
        sessions.remove(session);
        String username = (String) session.getAttributes().get("username");
        broadcast("❌ " + session.getAttributes().get("username") + " đã thoát.", username);
    }
    private void broadcast(String message, String username) {
        for (WebSocketSession s : sessions) {
            try {
                if (username != s.getAttributes().get("username")) {
                    s.sendMessage(new TextMessage(message));
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
