package com.example.securewhatsapp.app;

public class SecureWhatsappUser {
	
	private long id;
	  private String number;

	  public long getId() {
	    return id;
	  }

	  public void setId(long id) {
	    this.id = id;
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
