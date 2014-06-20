package com.example.securewhatsapp.app;

import android.app.Activity;
import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.provider.ContactsContract;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.text.Html;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

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


public class PendingRequest extends ArrayAdapter<SecureWhatsappFriend> {

    private Activity activity;
    private List<SecureWhatsappFriend> items;
    private int row;
    private SecureWhatsappFriend objBean;
    private ArrayList<Integer> selectedStrings = new ArrayList<Integer>();

    public PendingRequest(Activity act, int row, List<SecureWhatsappFriend> items) {
        super(act, row, items);

        this.activity = act;
        this.row = row;
        this.items = items;

    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        View view = convertView;
        final ViewHolder holder;
        if (view == null) {
            LayoutInflater inflater = (LayoutInflater) activity
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view = inflater.inflate(row, null);

            holder = new ViewHolder();
            view.setTag(holder);
        } else {
            holder = (ViewHolder) view.getTag();
        }

        if ((items == null) || ((position + 1) > items.size()))
            return view;

        objBean = items.get(position);

        holder.name = (TextView) view.findViewById(R.id.name);
        holder.accept_button = (Button) view.findViewById(R.id.accept_button);
        holder.accept_button.setOnClickListener(
                new View.OnClickListener() {
                    public void onClick(View view) {
                        System.out.println("accepting pending");
                        sendRequestAnswer("accept",PendingRequestList.numbersPending.get(position));

                    }
                }
        );
        holder.reject_button = (Button) view.findViewById(R.id.reject_button);
        holder.reject_button.setOnClickListener(
                new View.OnClickListener() {
                    public void onClick(View view) {
                        System.out.println("rejecting pending");
                        sendRequestAnswer("reject",PendingRequestList.numbersPending.get(position));

                    }
                }
        );

        // holder.readMessage = (TextView) view.findViewById(R.id.unreadTextView);

        view.setTag(holder);


        if (holder.name != null && null != objBean.getNumber()
                && objBean.getNumber().trim().length() > 0) {

            holder.name.setText(Html.fromHtml(objBean.getNumber()));

        }

        return view;
    }






    public class ViewHolder {
        public TextView name;
        public Button accept_button;
        public Button reject_button;


    }

    public void sendRequestAnswer(String answer, String destination_number ){
        class SendPostReqAsyncTask extends AsyncTask<String, Void, String> {

            @Override
            protected String doInBackground(String... params) {

                HttpPost httpPost;
                 if(params[0].equals("accept")){
                 httpPost = new HttpPost("http://50.0.21.84/whatsapp/index.php?r=user/acceptfriend");
                 }
                else{
                     httpPost = new HttpPost("http://50.0.21.84/whatsapp/index.php?r=user/rejectfriend");
                 }

                ArrayList<BasicNameValuePair> basicValuePair;

                List<NameValuePair> nameValuePairList = new ArrayList<NameValuePair>();
                //Encryption the user number

                MCrypt mcrypt = new MCrypt();

                try {
                    nameValuePairList.add(new BasicNameValuePair("sourceNumber", MCrypt.bytesToHex(mcrypt.encrypt(MainActivity.datasource.getUserDB()))));
                    nameValuePairList.add(new BasicNameValuePair("destinationNumber", MCrypt.bytesToHex(mcrypt.encrypt(params[1]))));
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


            }
        }

        SendPostReqAsyncTask sendPostReqAsyncTask = new SendPostReqAsyncTask();
        new SendPostReqAsyncTask().execute(answer,destination_number);
    }






}