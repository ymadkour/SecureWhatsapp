package com.example.securewhatsapp.app;

import android.app.Activity;
import android.app.DownloadManager;
import android.content.ContentUris;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.provider.ContactsContract;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by youssef on 5/8/14.
 */
public class FriendAdapter  extends ArrayAdapter<SecureWhatsappFriend> {

    private Activity activity;
    private List<SecureWhatsappFriend> items;
    private int row;
    private SecureWhatsappFriend objBean;
    private ArrayList<Integer> selectedStrings = new ArrayList<Integer>();

    public FriendAdapter(Activity act, int row, List<SecureWhatsappFriend> items) {
        super(act, row, items);

        this.activity = act;
        this.row = row;
        this.items = items;

    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        View view = convertView;
        ViewHolder holder;
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

        holder.name = (TextView) view.findViewById(R.id.friendname);
        // holder.readMessage = (TextView) view.findViewById(R.id.unreadTextView);

        view.setTag(holder);


        if (holder.name != null && null != objBean.getNumber()
                && objBean.getNumber().trim().length() > 0) {

            holder.name.setText(Html.fromHtml(objBean.getName()));

        }

        return view;
    }



    public class ViewHolder {
        public TextView name;


    }




}