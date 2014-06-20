package com.example.securewhatsapp.app;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class SecureWhatsappDatabase extends SQLiteOpenHelper {
	
	  public static final String TABLE_USERS= "user";
      public static final String TABLE_MESSAGES= "messages";
      public static final String TABLE_FRIENDS= "friends";
      public static final String TABLE_DESTINATIONS= "destinations";
	  public static final String COLUMN_ID = "_id";
	  public static final String COLUMN_NUMBER = "number";
      public static final String COLUMN_CONVERSATIONID= "conversationID";
      public static final String COLUMN_CONVERSATIONRead= "conversationRead";
      public static final String COLUMN_CONTENT= "content";
      public static final String COLUMN_STATUS = "status";
      public static final String COLUMN_PUBLICKEY = "publicKey";
    public static final String COLUMN_PRIVATEKEY = "privateKey";

	  private static final String DATABASE_NAME = "sWhatsapp.db";
	  private static final int DATABASE_VERSION = 1;

	  // Database creation sql statement
	  public static final String DATABASE_CREATE_USERS = "create table "
	      + TABLE_USERS + "(" + COLUMN_ID
	      + " integer primary key autoincrement, " + COLUMN_NUMBER
	      + " text not null, "+ COLUMN_PUBLICKEY+" text,"+COLUMN_PRIVATEKEY+" text);";

    public static final String DATABASE_CREATE_MESSAGES = "create table "
            + TABLE_MESSAGES + "(" + COLUMN_ID
            + " integer primary key autoincrement, " + COLUMN_CONTENT
            + " text not null, "+COLUMN_CONVERSATIONID + " integer, "+COLUMN_CONVERSATIONRead+" integer,"+COLUMN_NUMBER+" text not null,"+COLUMN_STATUS+" integer);" ;

    private static final String DATABASE_CREATE_DESTIONATIONS = "create table "
            + TABLE_DESTINATIONS + "(" + COLUMN_ID
            + " integer primary key autoincrement, " + COLUMN_NUMBER
            + " text not null, "+COLUMN_CONVERSATIONID + " integer , foreign key ("+COLUMN_CONVERSATIONID+")references messages(conversationID));" ;

    private static final String DATABASE_CREATE_FRIENDS = "create table "+ TABLE_FRIENDS + "(" + COLUMN_ID
            + " integer primary key autoincrement, " + COLUMN_NUMBER
            + " text not null,"+COLUMN_STATUS+ " varchar(20), "+COLUMN_PUBLICKEY+" text);";

    public SecureWhatsappDatabase(Context context) {
	    super(context, DATABASE_NAME, null, DATABASE_VERSION);
	  }

	  @Override
	  public void onCreate(SQLiteDatabase database) {
	      database.execSQL(DATABASE_CREATE_USERS);
          database.execSQL(DATABASE_CREATE_MESSAGES);
          database.execSQL(DATABASE_CREATE_DESTIONATIONS);
          database.execSQL(DATABASE_CREATE_FRIENDS);

      }

	  @Override
	  public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
	    Log.w(SecureWhatsappDatabase.class.getName(),
	        "Upgrading database from version " + oldVersion + " to "
	            + newVersion + ", which will destroy all old data");
	    db.execSQL("DROP TABLE IF EXISTS " + TABLE_USERS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_MESSAGES);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_DESTINATIONS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_FRIENDS);
	    onCreate(db);
	  }

	} 

