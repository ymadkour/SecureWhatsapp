package com.example.securewhatsapp.app;


import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.Toast;

/**
 * Created by youssef on 5/8/14.
 */
public class ChatListActivity extends Activity implements
        OnItemClickListener {

    private ListView listView;
    private List<ChatBean> list = new ArrayList<ChatBean>();


    protected void onRestart(){
        super.onRestart();
        this.onCreate(null);
        System.out.println("Restart");
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        list.clear();
        listView = (ListView) findViewById(R.id.list);
        listView.setOnItemClickListener(this);

       ArrayList<SecureWhatsappMessages> s =  MainActivity.datasource.getMessages();
      ArrayList<String>   convID = new ArrayList<String>();
        int j = 0;
        while (s.size() != 0 ) {
            convID.add(s.get(j).getConversationID()+"");
            ChatBean objChat = new ChatBean();
            objChat.setName(s.get(j).getNumber());
            objChat.setNumber(s.get(j).getNumber());
            objChat.setConvID(s.get(j).getconversationID() + "");
            objChat.getContent().add(new Content(s.get(j).getContent(), s.get(j).getConversationRead()));
            objChat.setStatus((int) s.get(j).getConversationRead());
            s.remove(j);
            list.add(objChat);
            int i =0;
            while (i<s.size()) {

                if (!(convID.get(convID.size()-1).equals(s.get(i).getConversationID()+""))) {
                        i++;


                } else if(convID.get(convID.size()-1).equals(s.get(i).getConversationID()+"")){
                    list.get(list.size() - 1).getContent().add(new Content(s.get(i).getContent(), s.get(i).getConversationRead()));
                    s.remove(i);
                }


            }

        }
        ChatAdapter objAdapter = new ChatAdapter(
                ChatListActivity.this, R.layout.unread_message, list);

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
        /*ArrayList<String> checkedNumbers = new ArrayList<String>();
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

        CustomJsonReader cJR = new CustomJsonReader();
        String userNumber = MainActivity.datasource.getUserDB();
        JSONObject object = cJR.writeJSON(checkedNumbers,userNumber);
        SendRequest(object);*/

        return false;
    }



    @Override
    public void onItemClick(AdapterView<?> listview, View v, int position,
                            long id) {

            System.out.println("Clicked: "+position);
        Intent nextScreen = new Intent(getApplicationContext(), ChatActivity.class);
        nextScreen.putExtra("name", list.get(position).getName());
        ArrayList<String> numb = new ArrayList<String>();
        numb.add(list.get(position).getNumber());
        nextScreen.putExtra("number",numb);
        nextScreen.putExtra("convID",list.get(position).getConvID());
        nextScreen.putExtra("content",seperateList(list.get(position).getContent(), 0));
        nextScreen.putExtra("status",seperateList(list.get(position).getContent(),1));
        //Sending data to another Activity
        startActivity(nextScreen);
    }


    public ArrayList<String> seperateList (ArrayList<Content> c, int type){
        ArrayList<String> result = new ArrayList<String>();

        for (int i = 0 ; i<c.size() ; i++){

            if(type == 0){
            result.add(c.get(i).getConent());}
            else{
                result.add(c.get(i).getStatus()+"");
            }


        }

        return result;

    }



}
