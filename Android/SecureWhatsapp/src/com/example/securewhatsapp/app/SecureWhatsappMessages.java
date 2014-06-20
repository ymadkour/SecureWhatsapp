package com.example.securewhatsapp.app;

import java.util.ArrayList;

/**
 * Created by youssef on 5/10/14.
 */
public class SecureWhatsappMessages {

    private long id;
    private String content;
    private long conversationID;
    private long conversationRead;

    public long getConversationID() {
        return conversationID;
    }

    public void setConversationID(long conversationID) {
        this.conversationID = conversationID;
    }

    public String getNumber() {

        return number;
    }

    public void setNumber(String number) {
        this.number = number;
    }

    private String number;


    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public long getconversationID() {
        return conversationID;
    }

    public void setconversationID(long id) {
        this.conversationID = id;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public long getConversationRead() { return conversationRead;}

    public void setConversationRead(long conversationRead) {  this.conversationRead = conversationRead; }

    // Will be used by the ArrayAdapter in the ListView
    @Override
    public String toString() {
        return content;
    }

}
