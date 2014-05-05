package com.example.securewhatsapp.app;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.ContentUris;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

public class ContactListActivity extends Activity implements
		OnItemClickListener {
	
	private ListView listView;
	private List<ContactBean> list = new ArrayList<ContactBean>();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);

		listView = (ListView) findViewById(R.id.list);
		listView.setOnItemClickListener(this);

		Cursor phones = getContentResolver().query(
				ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null, null,
				null, null);

		while (phones.moveToNext()) {

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
									new Long(contactID)));
			Bitmap photo = null;
			if (inputStream != null) {
				photo = BitmapFactory.decodeStream(inputStream);

			}

			for (int i = 0; i < Contacts.sContacts.size(); i++) {
				if (Contacts.sContacts.get(i).equals(phoneNumber.replaceAll("\\s+",""))) {
					ContactBean objContact = new ContactBean();
					objContact.setName(name);
					objContact.setphotoURL(photo);
					list.add(objContact);
				}
			}

		}
		phones.close();

		ContanctAdapter objAdapter = new ContanctAdapter(
				ContactListActivity.this, R.layout.contact, list);
		listView.setAdapter(objAdapter);

		if (null != list && list.size() != 0) {
			Collections.sort(list, new Comparator<ContactBean>() {

				@Override
				public int compare(ContactBean lhs, ContactBean rhs) {
					return lhs.getName().compareTo(rhs.getName());
				}
			});
			AlertDialog alert = new AlertDialog.Builder(
					ContactListActivity.this).create();
			alert.setTitle("");

			alert.setMessage(list.size() + " Contact Found!!!");

			alert.setButton("OK", new DialogInterface.OnClickListener() {

				@Override
				public void onClick(DialogInterface dialog, int which) {
					dialog.dismiss();
				}
			});
			alert.show();

		} else {
			showToast("You are foreveralone!!!");
		}

	}

	private void showToast(String msg) {
		Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
	}
	
	public void onClick(View v) {
        
       // items[position].setSelected(cBox.isChecked());
    } 

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {

		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.contacts, menu);
		return true;
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
        ArrayList<String> checkedNumbers = new ArrayList<String>();
		 CheckBox cbx = (CheckBox)listView.findViewById(R.id.checkBox1);
		 int checkedItemPosition;
         int firstPosition = listView.getFirstVisiblePosition();
         for(int i=firstPosition;i<listView.getCount();i++){
             View v1=listView.getChildAt(i);
             cbx = (CheckBox)v1.findViewById(R.id.checkBox1);
             if(cbx.isChecked()){

                 // Toast.makeText(getApplicationContext(),
                        //          "Checked position " + goods.get(i),
                         //          Toast.LENGTH_SHORT).show();
               checkedItemPosition=i;
               checkedNumbers.add(Contacts.sContacts.get(i));
               System.out.println( "CLicked ..."+checkedItemPosition);
             }
         }

            CustomJsonReader cJR = new CustomJsonReader();
            String userNumber = MainActivity.datasource.getUserDB();
            JSONObject object = cJR.writeJSON(checkedNumbers,userNumber);
            SendRequest(object);

        return false;
	}


    public void SendRequest( final JSONObject object){
        class SendPostReqAsyncTask extends AsyncTask<String, Void, String> {

            @Override
            protected String doInBackground(String... params) {


                HttpPost httpPost = new HttpPost("http://192.168.1.6/whatsapp/index.php?r=user/addfriends");

                ArrayList <BasicNameValuePair> basicValuePair;

                List<NameValuePair> nameValuePairList = new ArrayList<NameValuePair>();


                    nameValuePairList.add(new BasicNameValuePair("1", object.toString()));



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

                    System.out.println("Server message request "+ stringBuilder);
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

                // Toast.makeText(getApplicationContext(), "User created Successfully ", Toast.LENGTH_LONG).show();
               // Intent nextScreen = new Intent(getApplicationContext(), ContactListActivity.class);
                //Sending data to another Activity
              //  startActivity(nextScreen);
                System.out.println("Server request done ");
                Intent nextScreen = new Intent(getApplicationContext(), sWhatsappFriends.class);
                //Sending data to another Activity
                startActivity(nextScreen);

            }
        }

        SendPostReqAsyncTask sendPostReqAsyncTask = new SendPostReqAsyncTask();
        sendPostReqAsyncTask.execute("");
    }

	@Override
	public void onItemClick(AdapterView<?> listview, View v, int position,
			long id) {
		
		
	}



}
