package com.example.securewhatsapp.app;

import android.app.Activity;
import android.content.ContentUris;
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
import java.util.List;


public class PendingRequestList extends Activity implements
        AdapterView.OnItemClickListener {

    private ListView listView;
    private List<SecureWhatsappFriend> list = new ArrayList<SecureWhatsappFriend>();
    private Button groupButton;
    public  static ArrayList<String> numbersPending;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

        ArrayList<String> alContacts = new ArrayList<String>();
        listView = (ListView) findViewById(R.id.list);
        listView.setOnItemClickListener(this);
        //TODO: get from database the friends and then check the name

        ArrayList<SecureWhatsappFriend> fr = MainActivity.datasource.getFriendDB();

        Cursor phones = getContentResolver().query(
                ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null, null,
                null, null);
        loadPendingFriends();
        /*if (phones.moveToFirst()){
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
        getMenuInflater().inflate(R.menu.pending_request, menu);
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
    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

    }

    public void loadPendingFriends( ){
        class SendPostReqAsyncTask extends AsyncTask<String, Void, String> {

            @Override
            protected String doInBackground(String... params) {


                HttpPost httpPost = new HttpPost("http://50.0.21.84/whatsapp/index.php?r=user/loadpendingfriends");

                ArrayList<BasicNameValuePair> basicValuePair;

                List<NameValuePair> nameValuePairList = new ArrayList<NameValuePair>();
                //Encryption the user number

                MCrypt mcrypt  = new MCrypt();
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

                    System.out.println("Server message loadFriends "+ stringBuilder);
                    //TODO: friends script format

                    numbersPending = new CustomJsonReader().pendingRequests(stringBuilder.toString());
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
                list.clear();
                for (int i = 0 ; i < numbersPending.size(); i++){
                    SecureWhatsappFriend obj = new SecureWhatsappFriend();
                    obj.setNumber(numbersPending.get(i));
                    obj.setName("");
                    list.add(obj);
                }

                PendingRequest objAdapter = new PendingRequest(
                        PendingRequestList.this, R.layout.activity_pending_request, list);
                listView.setAdapter(objAdapter);
                System.out.println("Server request done ");

            }
        }

        SendPostReqAsyncTask sendPostReqAsyncTask = new SendPostReqAsyncTask();
        new SendPostReqAsyncTask().execute("");
    }

}
