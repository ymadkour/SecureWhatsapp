package com.example.securewhatsapp.app;

import android.util.Base64;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.simple.parser.JSONParser;


import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

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

                MCrypt mcrypt = new MCrypt();
                String decrypt = new String( mcrypt.decrypt( jsonArray.get(i).toString() ) );
                message.add(decrypt.replaceAll("\\s+", ""));
             //   JSONObject jsonObject = jsonArray.getJSONObject(i);
               //s Log.i(CustomJsonReader.class.getName(), jsonObject.getString("text"));

            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return message;
    }


    public ArrayList <String> pendingRequests (String serverMessage){
        ArrayList <String> requests = new ArrayList<String>();
        JSONParser parser = new JSONParser();
        try {

            org.json.simple.JSONArray objs = (org.json.simple.JSONArray) parser.parse(serverMessage);
            Iterator<org.json.simple.JSONObject> iterator = objs.iterator();
            while (iterator.hasNext()) {

                org.json.simple.JSONObject obj = (org.json.simple.JSONObject) iterator.next();

                MCrypt mcrypt = new MCrypt();
                String number = (String)  obj.get("number");


                requests.add(new String(mcrypt.decrypt(number)).replaceAll("\\s+", ""));

            }


        }
        catch (Exception e){

        }

        return requests ;

    }


    public ArrayList<Number> readLoadFriends (String serverMessage){
        ArrayList<Number> loadedFriends = new ArrayList<Number>();
        JSONParser parser = new JSONParser();

            try {

                org.json.simple.JSONArray objs = (org.json.simple.JSONArray) parser.parse(serverMessage);

                Iterator<org.json.simple.JSONObject> iterator = objs.iterator();

                while (iterator.hasNext()) {

                    org.json.simple.JSONObject obj = (org.json.simple.JSONObject) iterator.next();
                    Number n = new Number();
                    MCrypt mcrypt = new MCrypt();
                    String number = (String)  obj.get("number");
                    String publicKey = (String) obj.get("publicKEY");

                    n.setNumber(new String(mcrypt.decrypt(number)).replaceAll("\\s+", ""));
                    n.setPublicKey(Base64.decode(publicKey.getBytes(),Base64.DEFAULT));
                    loadedFriends.add(n);

                }


            } catch (Exception e) {
                e.printStackTrace();

            }
        return loadedFriends;

    }


    public JSONObject writeJSON( ArrayList<String> numbersRequests, String number)  {
        JSONObject object = new JSONObject();
        MCrypt mcrypt = new MCrypt();

        try {


            JSONArray requests = new JSONArray();
            for(int i = 0 ; i< numbersRequests.size() ; i++){
                requests.put(MCrypt.bytesToHex(mcrypt.encrypt(numbersRequests.get(i))));
            }
            object.put("requests",requests);
            object.put("userNumber", MCrypt.bytesToHex(mcrypt.encrypt(number)));
        } catch (JSONException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
        System.out.println(object);
        return object;
    }

    public JSONObject writeJSONMessage( ArrayList<String> numbersRequests, String number,String content, String conversationID) {
        JSONObject object = new JSONObject();
        try {


            JSONArray requests = new JSONArray();
            for(int i = 0 ; i< numbersRequests.size() ; i++){
                requests.put(numbersRequests.get(i));
            }
            object.put("destinations",requests);
            object.put("userNumber", number);
            object.put("content",content);
            object.put("conversationID",conversationID);


        } catch (JSONException e) {
            e.printStackTrace();
        }
        System.out.println(object);
        return object;
    }

    public ArrayList<String> removeDuplicate (ArrayList <String> n){

       Set<String> set = new HashSet<String>(n);
       int numDuplicates = n.size() - set.size();
       return new ArrayList<String>(set);

    }

    public JSONObject parsetoJSONMessage( byte [][] cipherTextAndEncryptedKey,ArrayList<String> numbersRequests, String number, String conversationID) {
        JSONObject object = new JSONObject();
        try {

            numbersRequests = removeDuplicate(numbersRequests);
            MCrypt mcrypt = new MCrypt();
            JSONArray requests = new JSONArray();
            for(int i = 0 ; i< numbersRequests.size() ; i++){
                String encrypted = null;
                try {
                    encrypted = MCrypt.bytesToHex(mcrypt.encrypt(numbersRequests.get(i).replaceAll("\\s+", "")));
                } catch (Exception e) {
                    e.printStackTrace();
                }
                requests.put(encrypted);
            }

            object.put("destinations",requests);
            String encrypted = null;
            encrypted = MCrypt.bytesToHex(mcrypt.encrypt(number));
            object.put("userNumber", encrypted);
            encrypted = MCrypt.bytesToHex(mcrypt.encrypt(conversationID));
            object.put("conversationID",encrypted);



            byte[] cipherKey = cipherTextAndEncryptedKey[0];
            byte[] cipherText = cipherTextAndEncryptedKey[1];
            byte[] encryptedMessageDigest = cipherTextAndEncryptedKey[2];

            System.out.println("before key cipher json "+new String(cipherKey));

            object.put("cipherKEY", Base64.encodeToString(cipherKey,Base64.DEFAULT));
            object.put("cipherTXT",  Base64.encodeToString(cipherText,Base64.DEFAULT));
            object.put("digest", Base64.encodeToString(encryptedMessageDigest,Base64.DEFAULT));

        } catch (JSONException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
        System.out.println(object);
        return object;
    }


    public JSONObject byteToJson (byte [][] cipherTextAndEncryptedKey){

        JSONObject object = new JSONObject();

        try{

        byte[] cipherKey = cipherTextAndEncryptedKey[0];
        byte[] cipherText = cipherTextAndEncryptedKey[1];
        byte[] encryptedMessageDigest = cipherTextAndEncryptedKey[2];

            System.out.println("before key cipher json "+new String(cipherKey));
            String s = new String(cipherKey);
            MCrypt mcrypt = new MCrypt();
            System.out.println("josososo  "+ MCrypt.bytesToHex(s.getBytes()));
            String file = Base64.encodeToString(cipherKey, Base64.DEFAULT);
            System.out.println("after key cipher json "+file.toString());

            byte [] data1 = Base64.decode(file.toString(), Base64.DEFAULT);
        // System.out.println(s.getBytes().length);
       //     System.out.println("decrypted message  "+Encrypt.DecryptMessage(cipherText,cipherKey));
         //   System.out.println("return equal "+equal(cipherKey,data1));

        object.put("cipherKEY", MCrypt.bytesToHex((cipherKey)));
        object.put("cipherTXT", MCrypt.bytesToHex(cipherText));
        object.put("digest",MCrypt.bytesToHex(encryptedMessageDigest));

        }
        catch (Exception e){
            e.printStackTrace();
        }

        return object;
    }

    private boolean equal(byte []a, byte []b){


        for(int i =0 ; i<a.length ; i++){
            if(a[i] != b[i]){
                return false;
            }
        }
        return true;

    }

}


class Number{
    String number;

    public byte[] getPublicKey() {
        return publicKey;
    }

    byte [] publicKey;

    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
    }



    public void setPublicKey(byte[] publicKey) {
        this.publicKey = publicKey;
    }
}