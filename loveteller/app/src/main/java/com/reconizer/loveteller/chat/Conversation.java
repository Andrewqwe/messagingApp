package com.reconizer.loveteller.chat;

import java.util.ArrayList;

/**
 * Created by Damian on 15.08.2017.
 */

class Conversation {
    private ArrayList<String> usersUID;
    private Message lastMessage;
    private ArrayList<Message> messages;

    public Conversation(ArrayList<String> usersUID, Message lastMessage, ArrayList<Message> messages) {
        this.usersUID = usersUID;
        this.lastMessage = lastMessage;
        this.messages = messages;
    }

    public ArrayList<String> getUsersUID() {
        return usersUID;
    }

    public void setUsersUID(ArrayList<String> usersUID) {
        this.usersUID = usersUID;
    }

    public Message getLastMessage() {
        return lastMessage;
    }

    public void setLastMessage(Message lastMessage) {
        this.lastMessage = lastMessage;
    }

    public ArrayList<Message> getMessages() {
        return messages;
    }

    public void setMessages(ArrayList<Message> messages) {
        this.messages = messages;
    }
}