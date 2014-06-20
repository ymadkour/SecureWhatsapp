package com.example.securewhatsapp.app;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.security.InvalidKeyException;
import java.security.KeyFactory;
import java.security.KeyStore;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.X509EncodedKeySpec;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;

import  android.R.*;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.StrictMode;
import android.util.Base64;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnKeyListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONObject;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;

import de.svenjacobs.loremipsum.LoremIpsum;

public class ChatActivity extends Activity {
    private Timer autoUpdate;
	private DiscussArrayAdapter adapter;
	private ListView lv;
	private LoremIpsum ipsum;
	private EditText editText1;
    private Button attachButton;
    private Button sendLocation;
	private static Random random;
    public static ArrayList<String> numbers = new ArrayList<String>();


	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_discuss);
		random = new Random();
		ipsum = new LoremIpsum();

		lv = (ListView) findViewById(R.id.listView1);
        Intent i = getIntent();
        // Receiving the Data
        final String name = i.getStringExtra("name");
        //final ArrayList<String> number = i.getStringArrayListExtra("number");
        List<String> number1 = new ArrayList<String>();
        number1 = (ArrayList<String>)getIntent().getSerializableExtra("number");
        final ArrayList<String> number = (ArrayList<String>) number1;

        for(int k = 0 ;k<number.size();k++){

            numbers.add(number.get(k));
        }



        final String convID = i.getStringExtra("convID");
        final ArrayList<String> content = i.getStringArrayListExtra("content");
        final ArrayList<String> status  = i.getStringArrayListExtra("status");

        System.out.println("name :"+name +" number: "+number+" convID "+convID);
		adapter = new DiscussArrayAdapter(getApplicationContext(), R.layout.listitem_discuss);

		lv.setAdapter(adapter);

		editText1 = (EditText) findViewById(R.id.editText1);
		editText1.setOnKeyListener(new OnKeyListener() {
			public boolean onKey(View v, int keyCode, KeyEvent event) {
				// If the event is a key-down event on the "enter" button
				if ((event.getAction() == KeyEvent.ACTION_DOWN) && (keyCode == KeyEvent.KEYCODE_ENTER)) {
					// Perform action on key press
					adapter.add(new OneComment(false, editText1.getText().toString()));
                    MainActivity.datasource.createSecureWhatsappMessage(editText1.getText().toString(), convID,number,"1");
                    try {
                        sendMessage(editText1.getText().toString(),number,convID);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    editText1.setText("");
					return true;
				}
				return false;
			}
		});

        attachButton = (Button)findViewById(R.id.attach_button);
        attachButton.setOnClickListener(
                new View.OnClickListener() {
                    public void onClick(View view) {
                        System.out.println("Uploading image");
                        Intent nextScreen = new Intent(getApplicationContext(), UploadImages.class);
                        //Sending data to another Activity
                        startActivity(nextScreen);

                    }
                }
        );

        sendLocation = (Button)findViewById(R.id.button_send_location);
        sendLocation.setOnClickListener(
                new View.OnClickListener() {
                    public void onClick(View view) {

                       getLocation();

                    }
                }
        );

		if(content != null && status != null && content.size() != 0 && status.size() != 0)
             addItems(content,status);
	}

	private void addItems(ArrayList<String> content , ArrayList<String> status) {

        for(int i =0 ; content.size()>i ; i++){
            if(status.get(i).equals("1")){
                adapter.add(new OneComment(false, content.get(i)));
            }else{
                adapter.add(new OneComment(true, content.get(i)));
            }

        }
	}

	private static int getRandomInteger(int aStart, int aEnd) {
		if (aStart > aEnd) {
			throw new IllegalArgumentException("Start cannot exceed End.");
		}
		long range = (long) aEnd - (long) aStart + 1;
		long fraction = (long) (range * random.nextDouble());
		int randomNumber = (int) (fraction + aStart);
		return randomNumber;
	}





    public void sendMessage( String message, final ArrayList<String> number, String convID) throws Exception {
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

            class SendPostReqAsyncTask extends AsyncTask<String, Void, String>{
                @Override
                protected String doInBackground(String... params) {
                   for(int i =0 ; i< number.size() ; i++){
                    HttpPost http =  new HttpPost("http://50.0.21.84/whatsapp/index.php?r=user/requestuserpublickey");
                    MainActivity.httpClient = new DefaultHttpClient();
                    String stringBuilder = "";
                    ArrayList<BasicNameValuePair> basicValuePair;
                    List<NameValuePair> nameValuePairList1 = new ArrayList<NameValuePair>();
                    MCrypt mcrypt = new MCrypt();
                        try {
                            nameValuePairList1.add(new BasicNameValuePair("number",MCrypt.bytesToHex(mcrypt.encrypt(numbers.get(i).replaceAll("\\s+", "")))));
                        } catch (Exception e) {
                            e.printStackTrace();
                        }

                        try {
                        UrlEncodedFormEntity urlEncodedFormEntity = new UrlEncodedFormEntity(nameValuePairList1);


                        http.setEntity(urlEncodedFormEntity);

                        HttpResponse httpResponse = MainActivity.httpClient.execute(http);
                        HttpEntity entity = httpResponse.getEntity();

                        InputStream inputStream = httpResponse.getEntity().getContent();

                        InputStreamReader inputStreamReader = new InputStreamReader(inputStream);

                        BufferedReader bufferedReader = new BufferedReader(inputStreamReader);



                        String bufferedStrChunk = null;

                        while((bufferedStrChunk = bufferedReader.readLine()) != null){
                            stringBuilder += bufferedStrChunk;
                        }

                        System.out.println("Server PublicKey request "+ stringBuilder.toString());


                    } catch (ClientProtocolException cpe) {
                        System.out.println("First Exception caz of HttpResponese :" + cpe);
                        cpe.printStackTrace();
                    } catch (IOException ioe) {
                        System.out.println("Second Exception caz of HttpResponse :" + ioe);
                        ioe.printStackTrace();
                    }


                   String message = params[0];
                   String convID = params[1];


                HttpPost httpPost = new HttpPost("http://50.0.21.84/whatsapp/index.php?r=user/sendmessage");



                    CustomJsonReader cjr = new CustomJsonReader();

                    JSONObject object = null;
                    byte [][] enc = null;
                        List<NameValuePair> nameValuePairList = new ArrayList<NameValuePair>();
                    try {


                            byte[]pt = Base64.decode(stringBuilder.toString(), Base64.DEFAULT);
                            KeyFactory fact = KeyFactory.getInstance("RSA");
                            X509EncodedKeySpec x509KeySpec = new X509EncodedKeySpec(pt);
                            PublicKey publicKeyS = (PublicKey)fact.generatePublic(x509KeySpec);

                            PrivateKey pl = new CustomePrivateKey().loadPrivateKey(MainActivity.datasource.getUserPR());

                         enc = Encrypt.EncryptMessage(message.getBytes(),publicKeyS , pl);
                        object = cjr.byteToJson(enc);

                            object = cjr.parsetoJSONMessage(enc,numbers ,MainActivity.datasource.getUserDB(),convID);

                            nameValuePairList.add(new BasicNameValuePair("content",object.toString()));

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



                    String bufferedStrChunk = null;
                    stringBuilder ="";
                    while((bufferedStrChunk = bufferedReader.readLine()) != null){
                        stringBuilder += bufferedStrChunk;
                    }

                    System.out.println("Server message request "+ stringBuilder);


                } catch (ClientProtocolException cpe) {
                    System.out.println("First Exception caz of HttpResponese :" + cpe);
                    cpe.printStackTrace();
                } catch (IOException ioe) {
                    System.out.println("Second Exception caz of HttpResponse :" + ioe);
                    ioe.printStackTrace();
                }

                    }
                    return null;
                }


            }

        SendPostReqAsyncTask sendPostReqAsyncTask = new SendPostReqAsyncTask();
        new SendPostReqAsyncTask().execute(message, convID);
    }

    public void getLocation()
    {
        LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        // Define the criteria how to select the locatioin provider -> use
        // default
        boolean enabled = locationManager
                .isProviderEnabled(LocationManager.GPS_PROVIDER);
        Criteria criteria = new Criteria();
        String provider = locationManager.getBestProvider(criteria, false);
        Location location = locationManager.getLastKnownLocation(provider);

        // Initialize the location fields
        if (location != null) {
            System.out.println("Provider " + provider + " has been selected.");
            int lat = (int) (location.getLatitude());
            int lng = (int) (location.getLongitude());
            System.out.println("Lat: "+lat+" lng: "+lng);
        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.chat, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        return false;
    }



}