package com.example.securewhatsapp.app;

import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by youssef on 5/5/14.
 */
public class CustomJsonReader {


    public ArrayList<String> reader (String serverMessage){
        ArrayList<String> message = new ArrayList<String>();

        try {
            JSONArray jsonArray = new JSONArray(serverMessage);
            Log.i(CustomJsonReader.class.getName(),

                    "Number of entries " + jsonArray.length());
            for (int i = 0; i < jsonArray.length(); i++) {


                message.add(jsonArray.get(i).toString());
             //   JSONObject jsonObject = jsonArray.getJSONObject(i);
               //s Log.i(CustomJsonReader.class.getName(), jsonObject.getString("text"));

            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return message;
    }


    public JSONObject writeJSON( ArrayList<String> numbersRequests, String number) {
        JSONObject object = new JSONObject();
        try {


            JSONArray requests = new JSONArray();
            for(int i = 0 ; i< numbersRequests.size() ; i++){
                requests.put(numbersRequests.get(i));
            }
            object.put("requests",requests);
            object.put("userNumber", number);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        System.out.println(object);
        return object;
    }


}
