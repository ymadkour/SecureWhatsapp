package com.example.securewhatsapp.app;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.ContentUris;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class ContactListActivity extends Activity implements
		OnItemClickListener {
	
	private ListView listView;
	private List<ContactBean> list = new ArrayList<ContactBean>();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);

		listView = (ListView) findViewById(R.id.list);
		listView.setOnItemClickListener(this);

		Cursor phones = getContentResolver().query(
				ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null, null,
				null, null);

		while (phones.moveToNext()) {

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
									new Long(contactID)));
			Bitmap photo = null;
			if (inputStream != null) {
				photo = BitmapFactory.decodeStream(inputStream);

			}

			for (int i = 0; i < Contacts.alContacts.size(); i++) {
				if (Contacts.alContacts.get(i).equals(phoneNumber)) {
					ContactBean objContact = new ContactBean();
					objContact.setName(name);
					objContact.setphotoURL(photo);
					list.add(objContact);
				}
			}

		}
		phones.close();

		ContanctAdapter objAdapter = new ContanctAdapter(
				ContactListActivity.this, R.layout.contact, list);
		listView.setAdapter(objAdapter);

		if (null != list && list.size() != 0) {
			Collections.sort(list, new Comparator<ContactBean>() {

				@Override
				public int compare(ContactBean lhs, ContactBean rhs) {
					return lhs.getName().compareTo(rhs.getName());
				}
			});
			AlertDialog alert = new AlertDialog.Builder(
					ContactListActivity.this).create();
			alert.setTitle("");

			alert.setMessage(list.size() + " Contact Found!!!");

			alert.setButton("OK", new DialogInterface.OnClickListener() {

				@Override
				public void onClick(DialogInterface dialog, int which) {
					dialog.dismiss();
				}
			});
			alert.show();

		} else {
			showToast("You are foreveralone!!!");
		}
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

	       System.out.println( "CLicked ..."+checkedItemPosition);}
         }

	      return false;
	}

	@Override
	public void onItemClick(AdapterView<?> listview, View v, int position,
			long id) {
		
		
	}

}
