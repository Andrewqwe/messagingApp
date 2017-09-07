package com.reconizer.loveteller.chat;

import java.util.ArrayList;
import java.util.Date;

/**
 * Created by Damian on 31.08.2017.
 */

public class Messages {
    private Date date;
    private ArrayList<Message> messages;

    public Messages(Date time) {
        this.date = time;
        messages = new ArrayList<>();
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public ArrayList<Message> getMessages() {
        return messages;
    }

    public void setMessages(ArrayList<Message> messages) {
        this.messages = messages;
    }
}
