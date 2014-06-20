package com.example.securewhatsapp.app;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.ContentUris;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.provider.ContactsContract;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.message.BasicNameValuePair;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;


public class FriendsListActivity  extends Activity implements
        AdapterView.OnItemClickListener {

    private ListView listView;
    private List<SecureWhatsappFriend> list = new ArrayList<SecureWhatsappFriend>();
    private Button groupButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.friends_list);
        ArrayList<String> alContacts = new ArrayList<String>();
        listView = (ListView) findViewById(R.id.list);
        listView.setOnItemClickListener(this);
        //TODO: get from database the friends and then check the name
        groupButton = (Button) findViewById(R.id.button_create_group);
        groupButton.setOnClickListener(
                new View.OnClickListener() {
                    public void onClick(View view) {
                        System.out.println("Creating group chat");
                        Intent nextScreen = new Intent(getApplicationContext(), GroupListActivity.class);
                        //Sending data to another Activity
                        startActivity(nextScreen);

                    }
                }
        );

        loadFriends();
       /* Cursor phones = getContentResolver().query(
                ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null, null,
                null, null);

        if (phones.moveToFirst()){
            do {

                String name = phones
                        .getString(phones
                                .getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME));

                String phoneNumber = phones
                        .getString(phones
                                .getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));

                String contactID = phones.getString(phones
                        .getColumnIndex(ContactsContract.Contacts._ID));
                InputStream inputStream = ContactsContract.Contacts
                        .openContactPhotoInputStream(getContentResolver(),
                                ContentUris.withAppendedId(
                                        ContactsContract.Contacts.CONTENT_URI,
                                        new Long(contactID))
                        );
                Bitmap photo = null;
                if (inputStream != null) {
                    photo = BitmapFactory.decodeStream(inputStream);

                }

                SecureWhatsappFriend obj = new SecureWhatsappFriend();
                obj.setNumber(phoneNumber);
                obj.setName(name);
                list.add(obj);

                *//*for(int i = 0; i<fr.size() ; i++){
                    if(fr.get(i).getNumber().replaceAll("\\s+","").equals(phoneNumber.replaceAll("\\s+",""))){
                        SecureWhatsappFriend obj = new SecureWhatsappFriend();
                        obj.setNumber(phoneNumber);
                        obj.setName(name);
                        list.add(obj);
                    }
                }*//*


            } while (phones.moveToNext());

        }

        phones.close();*/







    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.friends_list, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int id, long l) {

        System.out.println(list.get(id).getName()+" "+list.get(id).getNumber());

        Intent nextScreen = new Intent(getApplicationContext(), ChatActivity.class);
        nextScreen.putExtra("name", list.get(id).getName());
        ArrayList<String> s = new ArrayList<String>();
        s.add(list.get(id).getNumber());
        nextScreen.putExtra("number",s);
        String number = MainActivity.datasource.getUserDB();
        String convID = MainActivity.datasource.getPrivateChatConvID(list.get(id).getNumber(),number);
        nextScreen.putExtra("convID",convID);
        ArrayList<SecureWhatsappMessages> m = MainActivity.datasource.getMessages();
        ArrayList <String> content = new ArrayList<String>();
        ArrayList <String> status = new ArrayList<String>();

        for(int i =0 ; i<m.size() ;i++){

            if((m.get(i).getconversationID()+"").equals(convID)){
                content.add(m.get(i).getContent());
                status.add(m.get(i).getConversationRead()+"");
            }
        }

        nextScreen.putExtra("content",content);
        nextScreen.putExtra("status",status);

        //Sending data to another Activity
        startActivity(nextScreen);
    }


    public void loadFriends( ){
        class SendPostReqAsyncTask extends AsyncTask<String, Void, String> {

            @Override
            protected String doInBackground(String... params) {


                HttpPost httpPost = new HttpPost("http://50.0.21.84/whatsapp/index.php?r=user/loadfriends");

                ArrayList<BasicNameValuePair> basicValuePair;

                List<NameValuePair> nameValuePairList = new ArrayList<NameValuePair>();
                //Encryption the user number

                MCrypt mcrypt = new MCrypt();

                try {
                    nameValuePairList.add(new BasicNameValuePair("number", MCrypt.bytesToHex(mcrypt.encrypt(MainActivity.datasource.getUserDB()))));
                } catch (Exception e) {
                    e.printStackTrace();
                }


                try {

                    UrlEncodedFormEntity urlEncodedFormEntity = new UrlEncodedFormEntity(nameValuePairList);


                    httpPost.setEntity(urlEncodedFormEntity);

                    HttpResponse httpResponse = MainActivity.httpClient.execute(httpPost);
                    HttpEntity entity = httpResponse.getEntity();

                    InputStream inputStream = httpResponse.getEntity().getContent();

                    InputStreamReader inputStreamReader = new InputStreamReader(inputStream);

                    BufferedReader bufferedReader = new BufferedReader(inputStreamReader);

                    String stringBuilder = "";

                    String bufferedStrChunk = null;

                    while((bufferedStrChunk = bufferedReader.readLine()) != null){
                        stringBuilder += bufferedStrChunk;
                    }
                     System.out.println("Serever loadFriends: "+stringBuilder.toString());
                     ArrayList<Number> friendsNumberPL = new CustomJsonReader().readLoadFriends(stringBuilder.toString());



                    for(int i =0 ; i<friendsNumberPL.size() ; i++){

                        MainActivity.datasource.createSecureWhatsappFriend(friendsNumberPL.get(i).getNumber(),"accept",new String (friendsNumberPL.get(i).getPublicKey()));

                    }

                    System.out.println("Server message loadFriends "+ stringBuilder);
                    //TODO: friends script format

                    return stringBuilder.toString();

                } catch (ClientProtocolException cpe) {
                    System.out.println("First Exception caz of HttpResponese :" + cpe);
                    cpe.printStackTrace();
                } catch (IOException ioe) {
                    System.out.println("Second Exception caz of HttpResponse :" + ioe);
                    ioe.printStackTrace();
                }


                return null;
            }
            @Override
            protected void onPostExecute(String result) {
                super.onPostExecute(result);

                ArrayList<SecureWhatsappFriend> fr = MainActivity.datasource.getFriendDB();
                Cursor phones = getContentResolver().query(
                        ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null, null,
                        null, null);

                if (phones.moveToFirst()){
                    do {

                        String name = phones
                                .getString(phones
                                        .getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME));

                        String phoneNumber = phones
                                .getString(phones
                                        .getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));

                        String contactID = phones.getString(phones
                                .getColumnIndex(ContactsContract.Contacts._ID));
                        InputStream inputStream = ContactsContract.Contacts
                                .openContactPhotoInputStream(getContentResolver(),
                                        ContentUris.withAppendedId(
                                                ContactsContract.Contacts.CONTENT_URI,
                                                new Long(contactID))
                                );
                        Bitmap photo = null;
                        if (inputStream != null) {
                            photo = BitmapFactory.decodeStream(inputStream);

                        }


                        for(int i = 0; i<fr.size() ; i++){
                            if(fr.get(i).getNumber().replaceAll("\\s+","").equals(phoneNumber.replaceAll("\\s+",""))){
                                SecureWhatsappFriend obj1 = new SecureWhatsappFriend();
                                obj1.setNumber(phoneNumber);
                                obj1.setName(name);
                                obj1.setPublicKey(fr.get(i).getPublicKey());
                                list.add(obj1);
                            }
                        }


                    } while (phones.moveToNext());

                }

                phones.close();


                FriendAdapter objAdapter = new FriendAdapter(
                        FriendsListActivity.this, R.layout.activity_friends_list, list);

                listView.setAdapter(objAdapter);

            }
        }

        SendPostReqAsyncTask sendPostReqAsyncTask = new SendPostReqAsyncTask();
        new SendPostReqAsyncTask().execute("");
    }



}
