package com.example.securewhatsapp.app;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.ImageView;
import android.widget.TextView;

public class ContanctAdapter extends ArrayAdapter<ContactBean> {

	private Activity activity;
	private List<ContactBean> items;
	private int row;
	private ContactBean objBean;
	private ArrayList<Integer> selectedStrings = new ArrayList<Integer>();
	
	public ContanctAdapter(Activity act, int row, List<ContactBean> items) {
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

		holder.tvname = (TextView) view.findViewById(R.id.contactname);
		holder.checkbox = (CheckBox) view.findViewById(R.id.checkBox1);
		holder.imageView = (ImageView) view.findViewById(R.id.contactphoto);
		view.setTag(holder);
		
		(holder.checkbox).setOnCheckedChangeListener(new OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    selectedStrings.add(position);
                }else{
                    int pos = selectedStrings.indexOf(position);
                    if(pos>0){
                    selectedStrings.remove(position);
                    }
                }

            }
        });
		
		if (holder.tvname != null && null != objBean.getName()
				&& objBean.getName().trim().length() > 0) {
			if (objBean.getphotoURL() != null) {
				holder.imageView.setImageBitmap(objBean.getphotoURL());
			}
			holder.tvname.setText(Html.fromHtml(objBean.getName()));

		}

		return view;
	}

	public class ViewHolder {
		public TextView tvname;
		public CheckBox checkbox;
		public ImageView imageView;
	}

}