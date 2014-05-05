package com.example.securewhatsapp.app;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.util.ArrayList;
import java.util.Enumeration;
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

import android.os.Handler;
import android.os.Message;
import android.support.v7.app.ActionBarActivity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.format.Formatter;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;


public class MainActivity extends ActionBarActivity {
	public static HttpClient httpClient ;
	public static SecureWhatsappDatabaseHelper datasource;
    public static int currentInt;
   // private RefreshHandler mRedrawHandler = new RefreshHandler();

/*    class RefreshHandler extends Handler {
        @Override
        public void handleMessage(Message msg) {
            MainActivity.this.updateUI();
        }

        public void sleep(long delayMillis) {
            this.removeMessages(0);
            sendMessageDelayed(obtainMessage(0), delayMillis);
        }
    };

    private void updateUI(){
         currentInt = currentInt + 10;

            mRedrawHandler.sleep(1000);
            System.out.println("test cron job: "+currentInt);

    }*/
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
        datasource = new SecureWhatsappDatabaseHelper(this);
        datasource.open();
      //  updateUI();
        if(!datasource.isDBEmpty()){
          // datasource.deleteDB();
         //   setContentView(R.layout.chat);
            Intent nextScreen = new Intent(getApplicationContext(), Chat.class);
            //Sending data to another Activity
            startActivity(nextScreen);

           // sendMessage();

        }
        else{
            //Intent nextScreen = new Intent(getApplicationContext(), fragment.class);
            //startActivity(nextScreen);
            setContentView(R.layout.registration_page);
            sendMessage();
        }
		
		Log.d("infot1 ", "your debug text here");

	
	}

    public String getLocalIpAddress() {
        try {
            for (Enumeration<NetworkInterface> en = NetworkInterface.getNetworkInterfaces(); en.hasMoreElements();) {
                NetworkInterface intf = en.nextElement();
                for (Enumeration<InetAddress> enumIpAddr = intf.getInetAddresses(); enumIpAddr.hasMoreElements();) {
                    InetAddress inetAddress = enumIpAddr.nextElement();
                    if (!inetAddress.isLoopbackAddress()) {
                        String ip = Formatter.formatIpAddress(inetAddress.hashCode());
                        System.out.println("***** IP=" + ip);
                        return ip;
                    }
                }
            }
        } catch (Exception ex) {
           // Log.e(TAG, ex.toString());
        }
        return null;
    }


	public void sendMessage(){
		
		final EditText userName   = (EditText)findViewById(R.id.userName);
		final EditText password   = (EditText)findViewById(R.id.password); 
		final EditText country   = (EditText)findViewById(R.id.countryTextField);
		final EditText number   = (EditText)findViewById(R.id.numberTextField); 
		Button  rtn = (Button)findViewById(R.id.done);
		Log.d("infot2 ", "your debug text here");
		rtn .setOnClickListener(
			        new View.OnClickListener()
			        {
			            public void onClick(View view)
			            {
			            	 Intent nextScreen = new Intent(getApplicationContext(), Contacts.class);
				                //Sending data to another Activity
				                startActivity(nextScreen);
				                System.out.println("ahu ya karim,, khuuuuuuud!!!!");
				                int  s =1 ;
				               datasource.createSecureWhatsappUser(number.getText().toString());
                            //TODO: hashing password before sending it to the server.
			            	postData(userName.getText().toString(),password.getText().toString(),country.getText().toString(),number.getText().toString() );
			             //   Log.d("EditText value=",edit_text.getText().toString() );
                            System.out.println("Send post to the server");
			            }
			        });
		
	}
	
	public void postData(String userName, String password, String country, String number ) {
		class SendPostReqAsyncTask extends AsyncTask<String, Void, String>{

	        @Override
	        protected String doInBackground(String... params) {

	        	String userName = params[0];
	        	String password = params[1];
	        	String country = params[2];
	        	String number = params[3];
	        	
	        	System.out.println("*****userName*******"+ userName+"*****Country*******"+ country+"*****Password*******"+ password+"*****Number*******"+ number+"*******IP*****"+getLocalIpAddress());
	           

	             httpClient = new DefaultHttpClient();

	            // In a POST request, we don't pass the values in the URL.
	            //Therefore we use only the web page URL as the parameter of the HttpPost argument
	            HttpPost httpPost = new HttpPost("http://192.168.1.6/whatsapp/index.php?r=user/create");

	            // Because we are not passing values over the URL, we should have a mechanism to pass the values that can be
	            //uniquely separate by the other end.
	            //To achieve that we use BasicNameValuePair             
	            //Things we need to pass with the POST request
	            BasicNameValuePair usernameBasicNameValuePair = new BasicNameValuePair("name", userName);
	            BasicNameValuePair passwordBasicNameValuePAir = new BasicNameValuePair("password", password);
	            BasicNameValuePair countryBasicNameValuePAir = new BasicNameValuePair("country", country);
	            BasicNameValuePair numberBasicNameValuePAir = new BasicNameValuePair("number", number);
                BasicNameValuePair ipBasicNameValuePair = new BasicNameValuePair("ip",getLocalIpAddress());

	            // We add the content that we want to pass with the POST request to as name-value pairs
	            //Now we put those sending details to an ArrayList with type safe of NameValuePair
	            List<NameValuePair> nameValuePairList = new ArrayList<NameValuePair>();
	            nameValuePairList.add(usernameBasicNameValuePair);
	            nameValuePairList.add(passwordBasicNameValuePAir);
	            nameValuePairList.add(countryBasicNameValuePAir);
	            nameValuePairList.add(numberBasicNameValuePAir);
                nameValuePairList.add(ipBasicNameValuePair);

	            try {
	                // UrlEncodedFormEntity is an entity composed of a list of url-encoded pairs. 
	                //This is typically useful while sending an HTTP POST request. 
	                UrlEncodedFormEntity urlEncodedFormEntity = new UrlEncodedFormEntity(nameValuePairList);

	                // setEntity() hands the entity (here it is urlEncodedFormEntity) to the request.
	                httpPost.setEntity(urlEncodedFormEntity);

	                try {
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
	                    //System.out.println("Server Message :" + stringBuilder);
	                    return stringBuilder.toString();

	                } catch (ClientProtocolException cpe) {
	                    System.out.println("First Exception caz of HttpResponese :" + cpe);
	                    cpe.printStackTrace();
	                } catch (IOException ioe) {
	                    System.out.println("Second Exception caz of HttpResponse :" + ioe);
	                    ioe.printStackTrace();
	                }

	            } catch (UnsupportedEncodingException uee) {
	                System.out.println("An Exception given because of UrlEncodedFormEntity argument :" + uee);
	                uee.printStackTrace();
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
	    sendPostReqAsyncTask.execute(userName, password,country,number);     
	}
	

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {

		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		
		return super.onOptionsItemSelected(item);
	}

	/**
	 * A placeholder fragment containing a simple view.
	 */

	
	

}
