package com.reconizer.loveteller.chat;

import java.util.ArrayList;

/**
 * Created by Damian on 31.08.2017.
 */

public class Messages {
    private String time;
    private ArrayList<Message> messages;

    public Messages(String time) {
        this.time = time;
        messages = new ArrayList<>();
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public ArrayList<Message> getMessages() {
        return messages;
    }

    public void setMessages(ArrayList<Message> messages) {
        this.messages = messages;
    }
}
