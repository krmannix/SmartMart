package com.example.searchcell3;

import java.util.ArrayList;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.TextView;

public class CustomAdapter1 extends ArrayAdapter<ListViewCustom1> {

	private ArrayList<ListViewCustom1> data;
	//private int layoutResourceId;
	Context _c;
	
	
	CustomAdapter1 (ArrayList<ListViewCustom1> d, Context c, int textViewResourceId) {
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
			v = vi.inflate(R.layout.listview_row_custom1, null);			
		}
		TextView title = (TextView) v.findViewById(R.id.itemtitle);
		TextView subtitle = (TextView) v.findViewById(R.id.itemsubtitle);
		//Button delete = (Button) v.findViewById(R.id.select);
		TextView number = (TextView) v.findViewById(R.id.numbering);
		
		ListViewCustom1 item = data.get(position);
		title.setText(item.title);
		subtitle.setText(item.subtitle);
		
		holder = new ListViewCustom1Holder();
		holder.lvc = item;
		holder.deleteButton = (Button)v.findViewById(R.id.select);
		holder.deleteButton.setTag(holder.lvc);
		
		holder.name = title;
		holder.subname = subtitle;
		holder.numbering = number;
		v.setTag(holder);

		setupItem(holder, position);	
		 
		//return row;

		return v;
		
	}
	
	private void setupItem(ListViewCustom1Holder holder, int pos) {
		String smallerSubTitle = holder.lvc.description;
		String smallerTitle;
		if (holder.lvc.title.length() < 20) {
			smallerTitle = holder.lvc.title.trim();
		} else {
			smallerTitle = holder.lvc.title.substring(0, holder.lvc.title.indexOf(' ', 19)).trim() + "... ";
		}
		
		holder.name.setText(smallerTitle);
		holder.subname.setText(smallerSubTitle);
		holder.numbering.setText((pos+1) + ")");
		
	}
	
	public static class ListViewCustom1Holder {
		ListViewCustom1 lvc;
		TextView name;
		TextView subname;
		TextView numbering;
		Button deleteButton;
	}
	
	public void refresh() {
		notifyDataSetChanged();
	}
}
