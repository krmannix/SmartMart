package com.example.searchcell3;

import java.util.ArrayList;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;


public class CustomAdapterForChooseMap extends ArrayAdapter<MapItems>{
	private ArrayList<MapItems> data;
	// private int layoutResourceId;
	Context _c;
	
	
	CustomAdapterForChooseMap(ArrayList<MapItems> d, Context c, int textViewResourceId) {
		super(c, textViewResourceId, d);
		data = d;
		_c = c;
	}
	
	public int getCount() {
		return data.size();
	}
	
	public MapItems getItem(int position) {
		return data.get(position);
	}
	
	public long getItemId(int position) {
		return position;
	}
	
	public View getView(int position, View convertView, ViewGroup parent) {
		View v = convertView;
		MapItemsHolder holder = null;
		
		if (v == null)
		{
			LayoutInflater vi = (LayoutInflater) _c.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			v = vi.inflate(R.layout.listviewrow_choosemap, null);			
		}
		TextView storename = (TextView) v.findViewById(R.id.mc_storename);
		TextView street = (TextView) v.findViewById(R.id.mc_street);
		TextView city = (TextView) v.findViewById(R.id.mc_city);
		
		MapItems item = data.get(position);
		storename.setText(item.getName());
		street.setText(item.getStreet());
		
		holder = new MapItemsHolder();
		holder.map = item;
		
		holder.Hstorename = storename;
		holder.Hstreet = street;
		holder.Hcity = city;
		v.setTag(holder);

		setupItem(holder, position);	

		return v;
		
	}
	
	private void setupItem(MapItemsHolder holder, int pos) {
		String name = holder.map.getName();
		String street = holder.map.getStreet();
		String cityZip = holder.map.getCity() + " " + holder.map.getZip();
		
		holder.Hstorename.setText(name);
		holder.Hstreet.setText(street);
		holder.Hcity.setText(cityZip);
		
	}
	
	public static class MapItemsHolder {
		MapItems map;
		TextView Hstorename;
		TextView Hstreet;
		TextView Hcity;
	}
	
	public void refresh() {
		notifyDataSetChanged();
	}
}
