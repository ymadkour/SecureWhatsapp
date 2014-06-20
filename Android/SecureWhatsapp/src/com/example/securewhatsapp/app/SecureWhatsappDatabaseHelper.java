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


	  public SecureWhatsappDatabaseHelper(Context context) {
	    dbHelper = new SecureWhatsappDatabase(context);
	  }

	  public void open() throws SQLException {
	    database = dbHelper.getWritableDatabase();
	  }

	  public void close() {
	    dbHelper.close();
	  }

	  public SecureWhatsappUser createSecureWhatsappUser(String number, String privateKey) {
	    ContentValues values = new ContentValues();
        values.put(SecureWhatsappDatabase.COLUMN_PRIVATEKEY, privateKey);
	    values.put(SecureWhatsappDatabase.COLUMN_NUMBER, number);
	    long insertId = database.insert(SecureWhatsappDatabase.TABLE_USERS, null,
	        values);
           String[] allColumns = { SecureWhatsappDatabase.COLUMN_ID,
                  SecureWhatsappDatabase.COLUMN_NUMBER, SecureWhatsappDatabase.COLUMN_PRIVATEKEY };
	    Cursor cursor = database.query(SecureWhatsappDatabase.TABLE_USERS,
	        allColumns, SecureWhatsappDatabase.COLUMN_ID + " = " + insertId, null,
	        null, null, null);
	    cursor.moveToFirst();
	    SecureWhatsappUser newUser = cursorToUser(cursor);
	    cursor.close();
	    return newUser;
	  }



    public void createSecureWhatsappFriend(String number,String status, String pL) {
        ContentValues values = new ContentValues();
        values.put(SecureWhatsappDatabase.COLUMN_NUMBER, number);
        values.put(SecureWhatsappDatabase.COLUMN_STATUS, status);
        values.put(SecureWhatsappDatabase.COLUMN_PUBLICKEY,pL);
        long insertId = database.insert(SecureWhatsappDatabase.TABLE_FRIENDS, null,
                values);
         String[] allColumns = { SecureWhatsappDatabase.COLUMN_ID,
                SecureWhatsappDatabase.COLUMN_NUMBER , SecureWhatsappDatabase.COLUMN_PUBLICKEY};
        Cursor cursor = database.query(SecureWhatsappDatabase.TABLE_FRIENDS,
                allColumns, SecureWhatsappDatabase.COLUMN_ID + " = " + insertId, null,
                null, null, null);
        //cursor.moveToFirst();
       // SecureWhatsappFriend newFriend = cursorToFriend(cursor);
        cursor.close();
      //  return newFriend;
    }

    public SecureWhatsappMessages createSecureWhatsappMessage (String content,String conversationID, ArrayList<String>numbers,String recieved) {

        for(int i =0 ; i< numbers.size() ;i++){
        ContentValues values = new ContentValues();
        values.put(SecureWhatsappDatabase.COLUMN_CONVERSATIONID,conversationID);
        values.put(SecureWhatsappDatabase.COLUMN_CONTENT, content);
        values.put(SecureWhatsappDatabase.COLUMN_CONVERSATIONRead,recieved);
        values.put(SecureWhatsappDatabase.COLUMN_STATUS,recieved);
        values.put(SecureWhatsappDatabase.COLUMN_NUMBER,numbers.get(i));
        tableExist(SecureWhatsappDatabase.TABLE_MESSAGES);
        long insertId = database.insert(SecureWhatsappDatabase.TABLE_MESSAGES, null,
                values);



       String [] allColumns1 = {SecureWhatsappDatabase.COLUMN_ID,SecureWhatsappDatabase.COLUMN_CONTENT,SecureWhatsappDatabase.COLUMN_CONVERSATIONID,SecureWhatsappDatabase.COLUMN_CONVERSATIONRead,SecureWhatsappDatabase.COLUMN_NUMBER,SecureWhatsappDatabase.COLUMN_STATUS};
        String query = "INSERT INTO "+ SecureWhatsappDatabase.TABLE_MESSAGES+" ( "+SecureWhatsappDatabase.COLUMN_CONVERSATIONID+","+SecureWhatsappDatabase.COLUMN_CONTENT+", "+SecureWhatsappDatabase.COLUMN_NUMBER+") VALUES ('"+conversationID+"', '"+content+"',62)";

       Cursor cursor = database.query(SecureWhatsappDatabase.TABLE_MESSAGES,
                allColumns1  , SecureWhatsappDatabase.COLUMN_ID + " = " + insertId, null,
                null, null, null);

       // Cursor cursor = database.rawQuery(query, null);
        countMessage();
        cursor.moveToFirst();
        SecureWhatsappMessages newMessage = cursorToMessage(cursor);
        cursor.close();
        createSecureWhatsappDestination( conversationID, numbers);
        return newMessage;}
        return null;
    }

    public void createSecureWhatsappDestination(String conversationID, ArrayList<String>numbers){

       for(int i = 0 ; i< numbers.size() ; i++){
           ContentValues values = new ContentValues();
           values.put(SecureWhatsappDatabase.COLUMN_CONVERSATIONID, conversationID);
           values.put(SecureWhatsappDatabase.COLUMN_NUMBER, numbers.get(i));
           long insertId = database.insert(SecureWhatsappDatabase.TABLE_DESTINATIONS, null,
                   values);
           Cursor cursor = database.query(SecureWhatsappDatabase.TABLE_DESTINATIONS,
                   new String[]{SecureWhatsappDatabase.COLUMN_CONVERSATIONID, SecureWhatsappDatabase.COLUMN_NUMBER}, SecureWhatsappDatabase.COLUMN_ID + " = " + insertId, null,
                   null, null, null);
           cursor.moveToFirst();
         //  SecureWhatsappDestination newDestination = cursorToDestination(cursor);
           cursor.close();

       }


    }


	  public void deletesecureWhatsappUser(SecureWhatsappUser SecureWhatsappUser) {
	    long id = SecureWhatsappUser.getId();
	    System.out.println("Comment deleted with id: " + id);
	    database.delete(SecureWhatsappDatabase.TABLE_MESSAGES, SecureWhatsappDatabase.COLUMN_ID
	        + " = " + id, null);
          database.delete(SecureWhatsappDatabase.TABLE_USERS, SecureWhatsappDatabase.COLUMN_ID
                  + " = " + id, null);

	  }

    public void deletesecureWhatsappMessage(String conv_id) {
        long id = Long.parseLong(conv_id);
        System.out.println("Comment deleted with id: " + id);
        database.delete(SecureWhatsappDatabase.TABLE_MESSAGES, SecureWhatsappDatabase.COLUMN_ID
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
        database.execSQL("DROP TABLE IF EXISTS " + SecureWhatsappDatabase.TABLE_MESSAGES);


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

    public String getUserPR(){
        Cursor mCursor = database.rawQuery("SELECT "+ SecureWhatsappDatabase.COLUMN_PRIVATEKEY+" FROM " +SecureWhatsappDatabase.TABLE_USERS+" LIMIT 1",null);
        String userNumber = "";

        if (mCursor.moveToFirst()) {

            return mCursor.getString(0);

        }
        mCursor.close();
        return userNumber;
    }


        public ArrayList<SecureWhatsappFriend>getFriendDB(){
            ArrayList<SecureWhatsappFriend> fr = new ArrayList<SecureWhatsappFriend>();
            ArrayList<String> duplicate = new ArrayList<String>();
            Cursor mCursor = database.rawQuery("SELECT "+ SecureWhatsappDatabase.COLUMN_NUMBER +" FROM "+ SecureWhatsappDatabase.TABLE_FRIENDS,null);
            if (mCursor.moveToFirst()) {

                do{
                    SecureWhatsappFriend friend = new SecureWhatsappFriend();
                    friend.setNumber(mCursor.getString(0));
                    duplicate.add(mCursor.getString(0));
                    fr.add(friend);

                }while(mCursor.moveToNext() && !(duplicate.contains(mCursor.getString(0))));

            }
            return fr;
        }


    public int countMessage(){
       try{
        Cursor mCursor = database.rawQuery("SELECT COUNT(*) FROM "+ SecureWhatsappDatabase.TABLE_MESSAGES,null);
           mCursor.moveToFirst();
           int count= mCursor.getInt(0);
           mCursor.close();
        return count;}
       catch (Exception e){
          System.out.println(e.toString());
           return 1;
       }
    }

	  private SecureWhatsappUser cursorToUser(Cursor cursor) {
		  SecureWhatsappUser user = new SecureWhatsappUser();
		  user.setId(cursor.getLong(0));
		  user.setNumber(cursor.getString(1));
	    return user;
	  }

    private SecureWhatsappFriend cursorToFriend(Cursor cursor) {
        SecureWhatsappFriend user = new SecureWhatsappFriend();
        user.setId(cursor.getLong(0));
        user.setNumber(cursor.getString(1));
        user.setStatus(cursor.getString(2));
        return user;
    }

    private SecureWhatsappMessages cursorToMessage(Cursor cursor) {
        SecureWhatsappMessages message = new SecureWhatsappMessages();
        message.setId(cursor.getLong(0));
        message.setContent(cursor.getString(1));
        message.setconversationID(cursor.getLong(2));
        message.setConversationRead(cursor.getLong(3));
      //  message.setNumber(cursor.getString(4));
        return message;
    }

    private SecureWhatsappDestination cursorToDestination(Cursor cursor) {
        SecureWhatsappDestination destination = new SecureWhatsappDestination();
        destination.setId(cursor.getLong(0));
        destination.setNumber(cursor.getString(1));
        destination.setconversationID(cursor.getLong(2));
        return destination;
    }


    public void tableExist(String name){
        Cursor c = null;
        boolean tableExists = false;
/* get cursor on it */
        try
        {
            c = database.query(name, null,
                    null, null, null, null, null);
            System.out.println("Does Exist");
        }
        catch (Exception e) {
    /* fail */
            System.out.println("Doesnt Exist");
        }


    }

    public ArrayList<SecureWhatsappMessages> getMessages(){


        ArrayList<SecureWhatsappMessages> messageses1 = new ArrayList<SecureWhatsappMessages>();
        Cursor mCursor = database.rawQuery("SELECT * FROM "+ SecureWhatsappDatabase.TABLE_MESSAGES,null);
        if (mCursor.moveToFirst()) {

            do{
                SecureWhatsappMessages message = new SecureWhatsappMessages();
                message.setId(mCursor.getLong(0));
                message.setContent(mCursor.getString(1));
                message.setconversationID(mCursor.getLong(2));
                message.setConversationRead(mCursor.getLong(3));
                message.setNumber(mCursor.getString(4));
                messageses1.add(message);

            }while(mCursor.moveToNext());

        }
        return messageses1;
    }

    public String getPublicKey (String number){

        String publicKey = "";
        Cursor mCursor = database.rawQuery("SELECT "+SecureWhatsappDatabase.COLUMN_PUBLICKEY +" FROM "+ SecureWhatsappDatabase.TABLE_FRIENDS+" WHERE "+SecureWhatsappDatabase.COLUMN_NUMBER+" ='"+number+"'",null);
        if(mCursor.moveToFirst()){
            return mCursor.getString(0);
        }
        return publicKey ;


    }

    public String getPrivateChatConvID (String number,String userNumber){
        String id = "";

        Cursor mCursor = database.rawQuery("SELECT "+SecureWhatsappDatabase.COLUMN_CONVERSATIONID +" FROM "+ SecureWhatsappDatabase.TABLE_MESSAGES+" WHERE "+SecureWhatsappDatabase.COLUMN_NUMBER+" ='"+number+"' AND "+SecureWhatsappDatabase.COLUMN_STATUS+"= '0'",null);
        if(mCursor.moveToFirst()){
            return mCursor.getString(0);
        }
         return userNumber + countMessage();

    }


    public ArrayList<SecureWhatsappMessages> getSpecMessages (String number){
        ArrayList<SecureWhatsappMessages> messageses1 = new ArrayList<SecureWhatsappMessages>();

        Cursor mCursor = database.rawQuery("SELECT * FROM "+ SecureWhatsappDatabase.TABLE_MESSAGES+" WHERE "+SecureWhatsappDatabase.COLUMN_NUMBER+" ='"+number+"' AND "+SecureWhatsappDatabase.COLUMN_STATUS+"= '0'",null);

        if (mCursor.moveToFirst()) {

            do{
                SecureWhatsappMessages message = new SecureWhatsappMessages();
                message.setId(mCursor.getLong(0));
                message.setContent(mCursor.getString(1));
                message.setconversationID(mCursor.getLong(2));
                message.setConversationRead(mCursor.getLong(3));
                message.setNumber(mCursor.getString(4));
                messageses1.add(message);

            }while(mCursor.moveToNext());

        }
        return messageses1;
    }
}
