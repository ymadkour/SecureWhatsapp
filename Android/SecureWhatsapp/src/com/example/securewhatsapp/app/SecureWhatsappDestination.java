package com.example.securewhatsapp.app;

/**
 * Created by youssef on 5/10/14.
 */
public class SecureWhatsappDestination {

    private long id;
    private long conversationID;
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

    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
    }

    // Will be used by the ArrayAdapter in the ListView
    @Override
    public String toString() {
        return number;
    }

}
