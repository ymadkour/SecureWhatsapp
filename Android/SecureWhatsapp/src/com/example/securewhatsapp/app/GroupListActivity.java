package com.example.securewhatsapp.app;

import android.app.Activity;
import android.content.ContentUris;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.provider.ContactsContract;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.CheckBox;
import android.widget.ListView;
import android.widget.Toast;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;


public class GroupListActivity extends Activity implements
        AdapterView.OnItemClickListener {

    private ListView listView;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
//
        ArrayList<String> alContacts = new ArrayList<String>();
        List<ContactBean> list = new ArrayList<ContactBean>();
        List<ContactBean> temp_list = new ArrayList<ContactBean>();
        listView = (ListView) findViewById(R.id.list);
        listView.setOnItemClickListener(this);

        Cursor phones = getContentResolver().query(
                ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null, null,
                null, null);

        if (phones.moveToFirst()){
            do {

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
                                        new Long(contactID))
                        );
                Bitmap photo = null;
                if (inputStream != null) {
                    photo = BitmapFactory.decodeStream(inputStream);

                }

                ContactBean objContact = new ContactBean();
                objContact.setName(name);
                objContact.setphotoURL(photo);
                objContact.setPhoneNumber(phoneNumber);
                temp_list.add(objContact);
                alContacts.add(phoneNumber);


            } while (phones.moveToNext());

        }
        ArrayList<String> sContacts  = new Contacts().checkUsers(alContacts);


        for(int j =0 ; j < alContacts.size(); j++) {
            for (int i = 0; i < sContacts.size(); i++) {
                if ((sContacts.get(i).replaceAll("\\s+","")).equals(alContacts.get(j).replaceAll("\\s+",""))) {
                    list.add(temp_list.get(j));
                }

            }
        }
        phones.close();

        ContactAdapter objAdapter = new ContactAdapter(
                GroupListActivity.this, R.layout.contact, list);
        listView.setAdapter(objAdapter);



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

        Intent nextScreen = new Intent(getApplicationContext(), ChatActivity.class);
        nextScreen.putExtra("name", "Group");
        nextScreen.putExtra("number",checkedNumbers);
        String number = MainActivity.datasource.getUserDB();
        String convID = "";
        //String convID = MainActivity.datasource.getPrivateChatConvID(list.get(id).getNumber(),number);
        nextScreen.putExtra("convID",convID);
        ArrayList<SecureWhatsappMessages> m = MainActivity.datasource.getMessages();
        ArrayList <String> content = new ArrayList<String>();
        ArrayList <String> status = new ArrayList<String>();

        for(int i =0 ; i<m.size() ;i++){

            if((m.get(i).getconversationID()+"").equals(convID)){
                content.add(m.get(i).getContent());
                status.add(m.get(i).getConversationRead()+"");
            }
        }

        nextScreen.putExtra("content",content);
        nextScreen.putExtra("status",status);

        //Sending data to another Activity
        startActivity(nextScreen);



        return false;
    }




    @Override
    public void onItemClick(AdapterView<?> listview, View v, int position,
                            long id) {


    }



}

