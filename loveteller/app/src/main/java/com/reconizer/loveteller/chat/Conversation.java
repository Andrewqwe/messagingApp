package com.reconizer.loveteller.chat;

import java.util.ArrayList;

/**
 * Created by Damian on 15.08.2017.
 */

public class Conversation {
    private ArrayList<String> usersID;
    private String messagesID;

    public Conversation(ArrayList<String> usersUID, String messages) {
        this.usersID = usersUID;
        this.messagesID = messages;
    }

    public ArrayList<String> getUsersID() {
        return usersID;
    }

    public void setUsersID(ArrayList<String> usersID) {
        this.usersID = usersID;
    }


    public String getMessagesID() {
        return messagesID;
    }

    public void setMessagesID(String messagesID) {
        this.messagesID = messagesID;
    }
}