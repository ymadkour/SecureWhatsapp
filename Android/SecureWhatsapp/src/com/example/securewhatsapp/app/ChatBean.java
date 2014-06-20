package com.example.securewhatsapp.app;

import android.graphics.Bitmap;
import android.widget.ArrayAdapter;

import java.util.ArrayList;

/**
 * Created by youssef on 5/8/14.
 */
public class ChatBean {

    private String name;
    private String number;
    private String convID;
    private long id;
    private int status;
    
    private ArrayList<Content> content = new ArrayList<Content>();

    public ArrayList<Content> getContent() {
        return content;
    }

    public void setContent(ArrayList<Content> content) {
        this.content = content;
    }

    public int getStatus() {
        return status;

    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String getConvID() {
        return convID;
    }

    public void setConvID(String convID) {
        this.convID = convID;
    }

    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

}

 class Content{

    String conent;
    Long status;
    public Content(String conent, long status){
        this.conent = conent;
        this.status= status;



     }
     public Long getStatus() {
         return status;
     }

     public void setStatus(Long status) {
         this.status = status;
     }

     public String getConent() {

         return conent;
     }

     public void setConent(String conent) {
         this.conent = conent;
     }
 }