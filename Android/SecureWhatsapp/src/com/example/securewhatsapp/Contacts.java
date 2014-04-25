package com.example.securewhatsapp;

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
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.contacts);
		ContentResolver contResv = getContentResolver();
		Cursor cursor = contResv.query(ContactsContract.Contacts.CONTENT_URI, null, null, null, null);
		if(cursor.moveToFirst())
		{
		    ArrayList<String> alContacts = new ArrayList<String>();
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
		    checkUsers(alContacts);
		}
		
	}

	
	public void checkUsers( ArrayList<String> alContacts){
		class SendPostReqAsyncTask extends AsyncTask<String, Void, String>{

	        @Override
	        protected String doInBackground(String... params) {

	        	 System.out.println(params[0]);
	        	

	           HttpClient httpClient = new DefaultHttpClient();

	            // In a POST request, we don't pass the values in the URL.
	            //Therefore we use only the web page URL as the parameter of the HttpPost argument
	            HttpPost httpPost = new HttpPost("http://192.168.1.6/whatsapp/index.php?r=user/findusers");

	            // Because we are not passing values over the URL, we should have a mechanism to pass the values that can be
	            //uniquely separate by the other end.
	            //To achieve that we use BasicNameValuePair             
	            //Things we need to pass with the POST request
	            ArrayList <BasicNameValuePair> basicValuePair;
	           // for(int i =0 ; i<alContact)
	            BasicNameValuePair usernameBasicNameValuePair = new BasicNameValuePair("1", params[0]);
	            BasicNameValuePair passwordBasicNameValuePAir = new BasicNameValuePair("2", params[1]);
	            //BasicNameValuePair countryBasicNameValuePAir = new BasicNameValuePair("country", country);
	            ///BasicNameValuePair numberBasicNameValuePAir = new BasicNameValuePair("number", number);

	            // We add the content that we want to pass with the POST request to as name-value pairs
	            //Now we put those sending details to an ArrayList with type safe of NameValuePair
	            List<NameValuePair> nameValuePairList = new ArrayList<NameValuePair>();
	            nameValuePairList.add(usernameBasicNameValuePair);
	            nameValuePairList.add(passwordBasicNameValuePAir);
	            //nameValuePairList.add(countryBasicNameValuePAir);
	            //nameValuePairList.add(numberBasicNameValuePAir);

	            try {
	                // UrlEncodedFormEntity is an entity composed of a list of url-encoded pairs. 
	                //This is typically useful while sending an HTTP POST request. 
	                UrlEncodedFormEntity urlEncodedFormEntity = new UrlEncodedFormEntity(nameValuePairList);

	                // setEntity() hands the entity (here it is urlEncodedFormEntity) to the request.
	                   httpPost.setEntity(urlEncodedFormEntity);

	            
	                    // HttpResponse is an interface just like HttpPost.
	                    //Therefore we can't initialize them
	                    HttpResponse httpResponse = httpClient.execute(httpPost);
	                    HttpEntity entity = httpResponse.getEntity();
	                   

	                    // According to the JAVA API, InputStream constructor do nothing. 
	                    //So we can't initialize InputStream although it is not an interface
	                    InputStream inputStream = httpResponse.getEntity().getContent();

	                    InputStreamReader inputStreamReader = new InputStreamReader(inputStream);

	                    BufferedReader bufferedReader = new BufferedReader(inputStreamReader);

	                    StringBuilder stringBuilder = new StringBuilder();

	                    String bufferedStrChunk = null;

	                    while((bufferedStrChunk = bufferedReader.readLine()) != null){
	                        stringBuilder.append(bufferedStrChunk);
	                    }
	                    System.out.println("Server Message :" + stringBuilder);
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
	            
	            if(result.equals("success")){
	                Toast.makeText(getApplicationContext(), "User created Successfully ", Toast.LENGTH_LONG).show();
	                Intent nextScreen = new Intent(getApplicationContext(), Contacts.class);
	                //Sending data to another Activity
	                startActivity(nextScreen);
	                
	            }else{
	                Toast.makeText(getApplicationContext(), "Please Try again", Toast.LENGTH_LONG).show();
	            }
	            
	           
	            
 	        }           
	    }

	    SendPostReqAsyncTask sendPostReqAsyncTask = new SendPostReqAsyncTask();
	    sendPostReqAsyncTask.execute("2314","123131");     
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
