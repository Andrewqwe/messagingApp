package com.reconizer.loveteller.chat;

import java.util.ArrayList;

/**
 * Created by Damian on 15.08.2017.
 */

public class Conversation {
    private ArrayList<String> usersUID;
    private ArrayList<Message> messages;

    public Conversation(ArrayList<String> usersUID, ArrayList<Message> messages) {
        this.usersUID = usersUID;
        this.messages = messages;
    }

    public ArrayList<String> getUsersUID() {
        return usersUID;
    }

    public void setUsersUID(ArrayList<String> usersUID) {
        this.usersUID = usersUID;
    }


    public ArrayList<Message> getMessages() {
        return messages;
    }

    public void setMessages(ArrayList<Message> messages) {
        this.messages = messages;
    }
}