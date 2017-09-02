package com.reconizer.loveteller.chat;

/**
 * Created by Damian on 15.08.2017.
 */

public class Message {
    private int owner;
    private String message;

    public Message(int owner, String message) {
        this.owner = owner;
        this.message = message;
    }

    public int getOwner() {
        return owner;
    }

    public void setOwner(int owner) {
        this.owner = owner;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Message() {
    }

    @Override
    public String toString() {
        return this.message;
    }
}
