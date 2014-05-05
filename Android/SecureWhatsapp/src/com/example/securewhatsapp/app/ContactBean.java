package com.example.securewhatsapp.app;

import android.graphics.Bitmap;

public class ContactBean {
	private String name;
	private boolean selected;
	private Bitmap photoURL;

	
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	
	
	public Bitmap getphotoURL() {
		return photoURL;
	}
	public void setphotoURL(Bitmap url) {
		this.photoURL = url;
	}
	public boolean isSelected() {
	    return selected;
	  }

	  public void setSelected(boolean selected) {
	    this.selected = selected;
	  }
	
	
}
