package com.springbootchat.chatMuti.model;

import lombok.Getter;
import lombok.Setter;

import java.util.UUID;
@Setter
@Getter
public class User {
    private String id;

    private String username;

    private boolean online;

    public User() {
        this.id = UUID.randomUUID().toString();
    }

    public User(String username) {
        this.id = UUID.randomUUID().toString();
        this.username = username;
        this.online = true;
    }
}
