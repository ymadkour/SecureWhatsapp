package com.example.securewhatsapp.app;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;
import java.util.List;

public class SecureWhatsappDatabaseHelper {
	
	// Database fields

	  private SQLiteDatabase database;
	  private SecureWhatsappDatabase dbHelper;
	  private String[] allColumns = { SecureWhatsappDatabase.COLUMN_ID,
			  SecureWhatsappDatabase.COLUMN_NUMBER };

	  public SecureWhatsappDatabaseHelper(Context context) {
	    dbHelper = new SecureWhatsappDatabase(context);
	  }

	  public void open() throws SQLException {
	    database = dbHelper.getWritableDatabase();
	  }

	  public void close() {
	    dbHelper.close();
	  }

	  public SecureWhatsappUser createSecureWhatsappUser(String number) {
	    ContentValues values = new ContentValues();
	    values.put(SecureWhatsappDatabase.COLUMN_NUMBER, number);
	    long insertId = database.insert(SecureWhatsappDatabase.TABLE_USERS, null,
	        values);
	    Cursor cursor = database.query(SecureWhatsappDatabase.TABLE_USERS,
	        allColumns, SecureWhatsappDatabase.COLUMN_ID + " = " + insertId, null,
	        null, null, null);
	    cursor.moveToFirst();
	    SecureWhatsappUser newUser = cursorToUser(cursor);
	    cursor.close();
	    return newUser;
	  }

	  public void deletesecureWhatsappUser(SecureWhatsappUser SecureWhatsappUser) {
	    long id = SecureWhatsappUser.getId();
	    System.out.println("Comment deleted with id: " + id);
	    database.delete(SecureWhatsappDatabase.TABLE_USERS, SecureWhatsappDatabase.COLUMN_ID
	        + " = " + id, null);
	  }

    public boolean isDBEmpty(){
        Cursor mCursor = database.rawQuery("SELECT * FROM " + SecureWhatsappDatabase.TABLE_USERS, null);
        Boolean rowExists;
        String id = null;
        String number = null;
        int count = 0;
        List<SecureWhatsappUser> contactList = new ArrayList<SecureWhatsappUser>();
        if (mCursor.getCount() <= 0)
        {


            rowExists = true;

        } else
        {
            if (mCursor.moveToFirst()) {
             id = mCursor.getString(0);
             number = mCursor.getString(1);
             count = mCursor.getCount();}

            System.out.println(" "+id+" "+number+" "+count );
            rowExists = false;
        }
        mCursor.close();
        return rowExists;
    }
    public void deleteDB(){

        database.delete(SecureWhatsappDatabase.TABLE_USERS, null, null);
    }
        public String getUserDB(){
            Cursor mCursor = database.rawQuery("SELECT * FROM " +SecureWhatsappDatabase.TABLE_USERS+" LIMIT 1",null);
            String userNumber = "";

            if (mCursor.moveToFirst()) {

                userNumber = mCursor.getString(1);

            }
            mCursor.close();
            return userNumber;
        }

	  private SecureWhatsappUser cursorToUser(Cursor cursor) {
		  SecureWhatsappUser user = new SecureWhatsappUser();
		  user.setId(cursor.getLong(0));
		  user.setNumber(cursor.getString(1));
	    return user;
	  }

}
