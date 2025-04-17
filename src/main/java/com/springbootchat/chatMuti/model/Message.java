package com.springbootchat.chatMuti.model;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.UUID;
public class Message {

    private String id;

    private String sender;

    private String receiver;

    private String content;

    private LocalDateTime timestamp;

    public Message() {
        this.id = UUID.randomUUID().toString();
        this.timestamp = LocalDateTime.now();
    }

    public Message(String sender, String receiver, String content) {
        this.id = UUID.randomUUID().toString();
        this.sender = sender;
        this.receiver = receiver;
        this.content = content;
        this.timestamp = LocalDateTime.now();
    }
}
