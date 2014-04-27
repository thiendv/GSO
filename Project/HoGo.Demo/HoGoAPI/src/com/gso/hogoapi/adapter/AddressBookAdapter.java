package com.gso.hogoapi.adapter;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.gso.hogoapi.R;
import com.gso.hogoapi.model.AddressBookItem;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.TextView;

public class AddressBookAdapter extends BaseAdapter {

	private List<AddressBookItem> list;
	private Context context;

	public AddressBookAdapter(Context c, List<AddressBookItem> list) {
		// TODO Auto-generated const-ructor stub
		this.list = list;
		context = c;
	}

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return list.size();
	}

	@Override
	public Object getItem(int arg0) {
		// TODO Auto-generated method stub
		return this.list.get(arg0);
	}

	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		// TODO Auto-generated method stub

		final AddressBookItem item = list.get(position);
		Holder holder = null;
		if (convertView == null) {
			Log.d("getView", "new");
			convertView = LayoutInflater.from(context)
					.inflate(R.layout.address_item, parent, false);
			holder = new Holder();
			holder.tvName = (TextView) convertView.findViewById(R.id.tvName);
			holder.cbxTag = (CheckBox) convertView.findViewById(R.id.cbxTag);
			holder.cbxTag.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

				@Override
				public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
					// TODO Auto-generated method stub
					item.setSelected(isChecked);
				}
			});
			convertView.setTag(holder);

		} else {
			holder = (Holder) convertView.getTag();
		}

		holder.email = item.getEmail();
		holder.cbxTag.setChecked(item.isSelected());
		holder.tvName.setText(item.getFirstName() + item.getMiddleName() + item.getLastName());

		holder.cbxTag.setTag(item);
		convertView.setTag(holder);
		return convertView;

	}

	public class Holder {
		private TextView tvName;
		private CheckBox cbxTag;
		private String email;
	}

	public List<AddressBookItem> getData() {
		// TODO Auto-generated method stub
		return list;
	}
}
