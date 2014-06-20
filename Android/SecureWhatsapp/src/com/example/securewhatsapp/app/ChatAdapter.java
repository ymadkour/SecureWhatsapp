package com.example.securewhatsapp.app;

import android.app.Activity;
import android.content.Context;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by youssef on 5/8/14.
 */
public class ChatAdapter  extends ArrayAdapter<ChatBean> {

    private Activity activity;
    private List<ChatBean> items;
    private int row;
    private ChatBean objBean;
    private ArrayList<Integer> selectedStrings = new ArrayList<Integer>();
    private static long convID = 0;

    public ChatAdapter(Activity act, int row, List<ChatBean> items) {
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

        holder.unReadMessage = (TextView) view.findViewById(R.id.unreadTextView);
       // holder.readMessage = (TextView) view.findViewById(R.id.unreadTextView);

        view.setTag(holder);


        if (holder.unReadMessage != null && null != objBean.getName()
                && objBean.getName().trim().length() > 0) {

            holder.unReadMessage.setText(Html.fromHtml(objBean.getName()));

        }

        return view;
    }

    public class ViewHolder {
        public TextView readMessage;
        public TextView unReadMessage;

    }

}