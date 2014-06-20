package com.example.securewhatsapp.app;

public class SecureWhatsappUser {
	
	  private long id;
	  private String number;
      private String publicKey;
      private String privateKey;

    public String getPublicKey() {
        return publicKey;
    }

    public void setPublicKey(String publicKey) {
        this.publicKey = publicKey;
    }

    public String getPrivateKey() {
        return privateKey;
    }

    public void setPrivateKey(String privateKey) {
        this.privateKey = privateKey;
    }

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
