package com.example.securewhatsapp.app;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.ActionBar;
import android.support.v4.app.Fragment;
import android.content.ContentResolver;
import android.content.Intent;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;
import android.os.Build;
import android.provider.ContactsContract;

public class Contacts extends ActionBarActivity {
	public static ArrayList<String> alContacts = new ArrayList<String>();
    public static ArrayList<String> sContacts = new ArrayList<String>();
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
        Contacts.sContacts.clear();
        Contacts.alContacts.clear();
	//	setContentView(R.layout.contact);
		ContentResolver contResv = getContentResolver();
		Cursor cursor = contResv.query(ContactsContract.Contacts.CONTENT_URI, null, null, null, null);
		if(cursor.moveToFirst())
		{
		    
		    do
		    {
		        String id = cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts._ID));

		        if(Integer.parseInt(cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts.HAS_PHONE_NUMBER))) > 0)
		        {
		            Cursor pCur = contResv.query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI,null,ContactsContract.CommonDataKinds.Phone.CONTACT_ID +" = ?",new String[]{ id }, null);
		            while (pCur.moveToNext()) 
		            {
		                String contactNumber = pCur.getString(pCur.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
		                alContacts.add(contactNumber);
		                break;
		            }
		            pCur.close();
		        }

		    } while (cursor.moveToNext()) ;

            //Sending data to another Activity

		     checkUsers(alContacts);

		}
		
	}

	
	public void checkUsers( final ArrayList<String> alContacts){
		class SendPostReqAsyncTask extends AsyncTask<String, Void, String>{

	        @Override
	        protected String doInBackground(String... params) {

	        
	            HttpPost httpPost = new HttpPost("http://192.168.1.6/whatsapp/index.php?r=user/findusers");

	            ArrayList <BasicNameValuePair> basicValuePair;
	           
	            List<NameValuePair> nameValuePairList = new ArrayList<NameValuePair>();
	            
	            for (int i =0 ; i < alContacts.size() ; i++){
	            	nameValuePairList.add(new BasicNameValuePair("" +(i+1), alContacts.get(i).replaceAll("\\s+","")));
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

                      //  JSONArray jsonObj = new JSONArray(stringBuilder);
                      //  System.out.println("Server Message1 :" + jsonObj.toString());
                         CustomJsonReader cJR= new CustomJsonReader();
                         sContacts =  cJR.reader(stringBuilder);



	                   // System.out.println("Server Message :" + stringBuilder);
                        /*stringBuilder = stringBuilder.replaceAll("\"","").replaceAll("\\[","").replaceAll("\\]","");
                        String[] serverMessage = stringBuilder.split(",");
                        for(int i =0 ; serverMessage.length>i ; i++){
                                sContacts.add(serverMessage[i]);
                        }*/


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
	                Intent nextScreen = new Intent(getApplicationContext(), ContactListActivity.class);
	                //Sending data to another Activity
	                startActivity(nextScreen);
	                
	           
	            
	           
	            
 	        }           
	    }

	    SendPostReqAsyncTask sendPostReqAsyncTask = new SendPostReqAsyncTask();
	    sendPostReqAsyncTask.execute("");     
	}
	
	
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {

		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.contacts, menu);
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

	/**
	 * A placeholder fragment containing a simple view.
	 */
	public static class PlaceholderFragment extends Fragment {

		public PlaceholderFragment() {
		}

		@Override
		public View onCreateView(LayoutInflater inflater, ViewGroup container,
				Bundle savedInstanceState) {
			View rootView = inflater.inflate(R.layout.fragment_contacts,
					container, false);
			return rootView;
		}
	}

}
