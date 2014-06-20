package com.example.securewhatsapp.app;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.math.BigInteger;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.security.InvalidAlgorithmParameterException;
import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.UnrecoverableEntryException;
import java.security.cert.CertificateException;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.X509EncodedKeySpec;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Enumeration;
import java.util.Iterator;
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
import org.json.JSONObject;
import org.json.simple.JSONArray;
import org.json.simple.parser.JSONParser;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.security.KeyPairGeneratorSpec;
import android.support.v7.app.ActionBarActivity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.format.Formatter;
import android.util.Base64;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import javax.security.auth.x500.X500Principal;


public class MainActivity extends ActionBarActivity {
	public static HttpClient httpClient ;
	public static SecureWhatsappDatabaseHelper datasource;
    public static int currentInt;
    public static PublicKey publicKeyServer ;
    public static PrivateKey privateKey;
    private RefreshHandler mRedrawHandler = new RefreshHandler();
  //  public static Encrypt encryption = new Encrypt();
   class RefreshHandler extends Handler {
        @Override
        public void handleMessage(Message msg) {
            MainActivity.this.recieveMessage("");
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

    }
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
        datasource = new SecureWhatsappDatabaseHelper(this);
        datasource.open();

       // MainActivity.datasource.creteee();
       // setContentView(R.layout.activity_main);
       // updateUI();
        recieveMessage("");
//        datasource.deleteDB();

        if(!datasource.isDBEmpty()){

           /*setContentView(R.layout.registration_page);
            sendRegistrationDetails();*/


            Intent nextScreen = new Intent(getApplicationContext(), HomePage.class);
            startActivity(nextScreen);

           // sendMessage();

        }
        else{

            setContentView(R.layout.registration_page);
            sendRegistrationDetails();

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


	public void sendRegistrationDetails(){
		
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
			            	 Intent nextScreen = new Intent(getApplicationContext(), ContactListActivity.class);
				                //Sending data to another Activity
				                startActivity(nextScreen);
				                System.out.println("ahu ya karim,, khuuuuuuud!!!!");
				                int  s =1 ;



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
	        	String number = params[3].replaceAll("\\s+", "");

                try {
                   // getServerPublicKey();
                    KeyPair key = Encrypt.GeneratePubPrivateKey(1024);
                  //  KeyPair key = savePrivateKey();
                    PublicKey pubKey2 = key.getPublic();
                    privateKey = key.getPrivate();
                   byte []  pL = pubKey2.getEncoded();
                    byte [] pR = privateKey.getEncoded();
                    CustomePrivateKey s  = new CustomePrivateKey();
                    String kPrivate = s.savePrivateKey(privateKey);
                    datasource.createSecureWhatsappUser(number,kPrivate);
                    // String sd = new String(pL);
                    CustomJsonReader crJ = new CustomJsonReader();

                    MCrypt mcrypt = new MCrypt();
                    String encrypted = null;

                    BasicNameValuePair usernameBasicNameValuePair = new BasicNameValuePair("name",  MCrypt.bytesToHex( mcrypt.encrypt(userName) ));


                    BasicNameValuePair passwordBasicNameValuePAir = new BasicNameValuePair("password", MCrypt.bytesToHex( mcrypt.encrypt(password)));


                    BasicNameValuePair countryBasicNameValuePAir = new BasicNameValuePair("country", MCrypt.bytesToHex( mcrypt.encrypt(country)));


                    /*cipherTextAndEncryptedKey =  Encrypt.EncryptMessage(number.getBytes(),pubKey2,privateKey);
                    JSONObject numberJson = crJ.byteToJson(cipherTextAndEncryptedKey);
                    BasicNameValuePair numberBasicNameValuePAir = new BasicNameValuePair("number", numberJson.toString());
*/


                     encrypted =  MCrypt.bytesToHex( mcrypt.encrypt(number) );
                   // JSONObject numberJson = crJ.byteToJson();
                    BasicNameValuePair numberBasicNameValuePAir = new BasicNameValuePair("number", encrypted);

                    BasicNameValuePair ipBasicNameValuePair = new BasicNameValuePair("ip",MCrypt.bytesToHex( mcrypt.encrypt(getLocalIpAddress())));
                    BasicNameValuePair publicKeyBasicNameValuePair = new BasicNameValuePair("publicKey",Base64.encodeToString(pL, Base64.DEFAULT));


                    System.out.println("*****userName*******"+ userName+"*****Country*******"+ country+"*****Password*******"+ password+"*****Number*******"+ number+"*******IP*****"+getLocalIpAddress());


                    httpClient = new DefaultHttpClient();

                    // In a POST request, we don't pass the values in the URL.
                    //Therefore we use only the web page URL as the parameter of the HttpPost argument
                    HttpPost httpPost = new HttpPost("http://50.0.21.84/whatsapp/index.php?r=user/create");

                    // Because we are not passing values over the URL, we should have a mechanism to pass the values that can be
                    //uniquely separate by the other end.
                    //To achieve that we use BasicNameValuePair
                    //Things we need to pass with the POST request





                    // We add the content that we want to pass with the POST request to as name-value pairs
                    //Now we put those sending details to an ArrayList with type safe of NameValuePair
                    List<NameValuePair> nameValuePairList = new ArrayList<NameValuePair>();
                    nameValuePairList.add(usernameBasicNameValuePair);
                    nameValuePairList.add(passwordBasicNameValuePAir);
                    nameValuePairList.add(countryBasicNameValuePAir);
                    nameValuePairList.add(numberBasicNameValuePAir);
                    nameValuePairList.add(ipBasicNameValuePair);
                    nameValuePairList.add(publicKeyBasicNameValuePair);

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
                            System.out.println("Server Message Register:" + stringBuilder);
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

                } catch (Exception e) {
                    e.printStackTrace();
                }



	            return null;
	        }


	    }

	    SendPostReqAsyncTask sendPostReqAsyncTask = new SendPostReqAsyncTask();
        new SendPostReqAsyncTask().execute(userName, password,country,number);
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

    public void recieveMessage( String message){
        mRedrawHandler.sleep(1000);
        class SendPostReqAsyncTask extends AsyncTask<String, Void, String> {

            @Override
            protected String doInBackground(String... params) {


                HttpPost httpPost = new HttpPost("http://50.0.21.84/whatsapp/index.php?r=user/getmessages");
                MainActivity.httpClient = new DefaultHttpClient();
                ArrayList<BasicNameValuePair> basicValuePair;

                List<NameValuePair> nameValuePairList = new ArrayList<NameValuePair>();
                MCrypt mcrypt = new MCrypt();

                try {
                    nameValuePairList.add(new BasicNameValuePair("number",MCrypt.bytesToHex(mcrypt.encrypt(MainActivity.datasource.getUserDB())) ));
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

                    System.out.println("Server message recieved "+ stringBuilder);

                    JSONParser parser = new JSONParser();
                    if(stringBuilder.toString() != "[]") {
                        try {

                            Object obj = parser.parse(stringBuilder.toString());

                            org.json.simple.JSONArray jsonArray = (org.json.simple.JSONArray) obj;
                            Iterator<org.json.simple.JSONObject> iterator = jsonArray.iterator();

                            while (iterator.hasNext()) {
                            org.json.simple.JSONObject jsonObject = (org.json.simple.JSONObject) iterator.next();
                            String key = (String) jsonObject.get("cipherKEY");
                            String txt = (String) jsonObject.get("cipherTXT");
                            String digst = (String) jsonObject.get("digest");
                            String sNumber = (String) jsonObject.get("sourceNumber");
                            String convID = (String) jsonObject.get("conversationID");

                            String decrypted = new String(mcrypt.decrypt(sNumber)).replaceAll("\\s+", "");
                            System.out.println("Decrepted Number: " + decrypted);

                            byte[] k = Base64.decode(key, Base64.DEFAULT);
                            byte[] txt1 = Base64.decode(txt, Base64.DEFAULT);
                            byte[] dig = Base64.decode(digst, Base64.DEFAULT);
                            PrivateKey pR = new CustomePrivateKey().loadPrivateKey(datasource.getUserPR());
                            String decptMessage = Encrypt.DecryptMessage(txt1, MainActivity.privateKey, k);
                                ArrayList<String> numbers = new ArrayList<String>();
                                numbers.add(decrypted);
                                MainActivity.datasource.createSecureWhatsappMessage(decptMessage, new String(mcrypt.decrypt(convID)),numbers,"0");

                            }

                        } catch (Exception e) {
                            e.printStackTrace();

                        }

                    }
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

        }

        SendPostReqAsyncTask sendPostReqAsyncTask = new SendPostReqAsyncTask();
        new SendPostReqAsyncTask().execute(message);
    }


    /*private void getServerPublicKey(){


                HttpPost httpPost = new HttpPost("http://192.168.1.7/whatsapp/index.php?r=user/requestpublickey");
                MainActivity.httpClient = new DefaultHttpClient();



                try {

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

                    System.out.println("Server message recieved "+ stringBuilder);
                    byte [] pT = Base64.decode(stringBuilder.toString(),Base64.DEFAULT);
                    //byte[]decode = Base64.decode(stringBuilder, Base64.DEFAULT);
                    KeyFactory fact = KeyFactory.getInstance("RSA");
                    X509EncodedKeySpec x509KeySpec = new X509EncodedKeySpec(pT);
                    publicKeyServer = (PublicKey)fact.generatePublic(x509KeySpec);




                } catch (ClientProtocolException cpe) {
                    System.out.println("First Exception caz of HttpResponese :" + cpe);
                    cpe.printStackTrace();
                } catch (IOException ioe) {
                    System.out.println("Second Exception caz of HttpResponse :" + ioe);
                    ioe.printStackTrace();
                } catch (InvalidKeySpecException e) {
                    e.printStackTrace();
                } catch (NoSuchAlgorithmException e) {
                    e.printStackTrace();
                }


    }


*/


    public KeyPair savePrivateKey(){

        Context ctx = getBaseContext();
        Calendar notBefore = Calendar.getInstance();
        Calendar notAfter = Calendar.getInstance();
        notAfter.add(1, Calendar.YEAR);
        KeyPairGeneratorSpec spec = new KeyPairGeneratorSpec.Builder(ctx)
                .setAlias("key1")
                .setSubject(
                        new X500Principal(String.format("CN=%s, OU=%s", "key1",
                                ctx.getPackageName())))
                .setSerialNumber(BigInteger.ONE).setStartDate(notBefore.getTime())
                .setEndDate(notAfter.getTime()).build();
        KeyPairGenerator kpGenerator;
        try {
            kpGenerator = KeyPairGenerator.getInstance("RSA", "AndroidKeyStore");

            kpGenerator.initialize(spec);
            KeyPair kp = kpGenerator.generateKeyPair();

            // in another part of the app, access the keys
            KeyStore keyStore = KeyStore.getInstance("AndroidKeyStore");
            keyStore.load(null);
            KeyStore.PrivateKeyEntry keyEntry = (KeyStore.PrivateKeyEntry) keyStore.getEntry("key1", null);
            RSAPublicKey pubKey = (RSAPublicKey)keyEntry.getCertificate().getPublicKey();
            RSAPrivateKey privKey = (RSAPrivateKey) keyEntry.getPrivateKey();
            return kp;

        } catch (NoSuchAlgorithmException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (NoSuchProviderException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (KeyStoreException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (CertificateException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (InvalidAlgorithmParameterException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (UnrecoverableEntryException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return null;

    }
}
