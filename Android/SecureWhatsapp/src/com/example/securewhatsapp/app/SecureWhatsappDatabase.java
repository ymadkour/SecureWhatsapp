package com.example.securewhatsapp.app;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class SecureWhatsappDatabase extends SQLiteOpenHelper {
	
	 public static final String TABLE_USERS= "user";
	  public static final String COLUMN_ID = "_id";
	  public static final String COLUMN_NUMBER = "number";

	  private static final String DATABASE_NAME = "users.db";
	  private static final int DATABASE_VERSION = 1;

	  // Database creation sql statement
	  private static final String DATABASE_CREATE = "create table "
	      + TABLE_USERS + "(" + COLUMN_ID
	      + " integer primary key autoincrement, " + COLUMN_NUMBER
	      + " text not null);";

	  public SecureWhatsappDatabase(Context context) {
	    super(context, DATABASE_NAME, null, DATABASE_VERSION);
	  }

	  @Override
	  public void onCreate(SQLiteDatabase database) {
	    database.execSQL(DATABASE_CREATE);
	  }

	  @Override
	  public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
	    Log.w(SecureWhatsappDatabase.class.getName(),
	        "Upgrading database from version " + oldVersion + " to "
	            + newVersion + ", which will destroy all old data");
	    db.execSQL("DROP TABLE IF EXISTS " + TABLE_USERS);
	    onCreate(db);
	  }

	} 

