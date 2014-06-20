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

public class Contacts  {

    public static ArrayList<String> sContacts = new ArrayList<String>();
	

	public  ArrayList<String> checkUsers( final ArrayList<String> alContacts){

                sContacts.clear();

	            HttpPost httpPost = new HttpPost("http://50.0.21.84/whatsapp/index.php?r=user/findusers");

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




	                } catch (ClientProtocolException cpe) {
	                    System.out.println("First Exception caz of HttpResponese :" + cpe);
	                    cpe.printStackTrace();
	                } catch (IOException ioe) {
	                    System.out.println("Second Exception caz of HttpResponse :" + ioe);
	                    ioe.printStackTrace();
	                }


        return sContacts;
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
