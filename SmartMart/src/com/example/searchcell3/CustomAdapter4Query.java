package com.example.searchcell3;

import java.util.ArrayList;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.TextView;

public class CustomAdapter4Query extends ArrayAdapter<ListViewCustom1> {

		private ArrayList<ListViewCustom1> data;
		//private int layoutResourceId;
		Context _c;
		
		
		CustomAdapter4Query (ArrayList<ListViewCustom1> d, Context c, int textViewResourceId) {
			super(c, textViewResourceId, d);
			data = d;
			_c = c;
		}
		
		public int getCount() {
			return data.size();
		}
		
		public ListViewCustom1 getItem(int position) {
			return data.get(position);
		}
		
		public long getItemId(int position) {
			return position;
		}
		
		public View getView(int position, View convertView, ViewGroup parent) {
			View v = convertView;
			ListViewCustom1Holder holder = null;
			
			if (v == null)
			{
				LayoutInflater vi = (LayoutInflater) _c.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
				v = vi.inflate(R.layout.listview_row_custom2, null);			
			}
			TextView title = (TextView) v.findViewById(R.id.itemtitle);
			TextView subtitle = (TextView) v.findViewById(R.id.itemsubtitle);
			Button select = (Button) v.findViewById(R.id.select2);
			
			ListViewCustom1 item = data.get(position);
			
			
			
			if (item.getSelected()) {
				select.setText("Deselect");
			}
			
			holder = new ListViewCustom1Holder();
			holder.lvc = item;
			holder.chooseButton = select;
			holder.chooseButton.setTag(holder.lvc);
			
			holder.name = title;
			holder.subname = subtitle;
			v.setTag(holder);

			setupItem(holder);
			return v;
			
		}
		
		private void setupItem(ListViewCustom1Holder holder) {
			String smallerSubTitle;
			if (holder.lvc.description.length() <= 25) {
				smallerSubTitle = holder.lvc.description.trim();
			} else {
				smallerSubTitle = holder.lvc.description.substring(0, 25).trim() + "... ";
			}
			String smallerTitle;
			if (holder.lvc.title.length() < 19) {
				smallerTitle = holder.lvc.title.trim();
			} else {
				smallerTitle = holder.lvc.title.substring(0, 19).trim() + "... ";
			}

			
			holder.name.setText(smallerTitle);
			holder.subname.setText(smallerSubTitle);
		}
		
		public static class ListViewCustom1Holder {
			ListViewCustom1 lvc;
			TextView name;
			TextView subname;
			Button chooseButton;
		}
	}
	

