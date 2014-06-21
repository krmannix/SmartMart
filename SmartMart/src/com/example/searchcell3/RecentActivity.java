package com.example.searchcell3;

import java.util.ArrayList;

import android.app.ListActivity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

public class RecentActivity extends ListActivity {

	// Declare buttons
	Button back;
	CustomAdapter4Query adapter;
	ListViewCustom1 Recent;
	RecentDBHelper dbH;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
	
		super.onCreate(savedInstanceState);
		setContentView(R.layout.recentlyfoundlayout);
		final RecentDBHelper dbH = new RecentDBHelper(this);
		
		final ArrayList<ListViewCustom1> values = new ArrayList<ListViewCustom1>(dbH.getAllItems());
			if (values.size() > 10) {
			for (int i = (values.size() - 1); i >= 10; i--) {
				ListViewCustom1 itemToRemove = values.get(i);
				dbH.deleteItem(itemToRemove);
				values.remove(i);				
			}
		} 
		
		
		
		
	/*	To delete everything
	 * 
	 * 
	 * 
	 * for (int i = 0; i < values.size(); i++) {
			ListViewCustom1 itemToRemove = values.get(i);
			dbH.deleteItem(itemToCerealRemove);
			values.remove(i);
			
		} */
		
		// Define buttons
		back = (Button) findViewById(R.id.backbuttonrecent);
	
		adapter = new CustomAdapter4Query(values, this, R.layout.listview_row_custom2);
		setListAdapter(adapter); 
			
			// These methods define button behavior on click
		back.setOnClickListener(new OnClickListener() {
				public void onClick(View v) {
					Intent backToMain = new Intent(v.getContext(), SearchActivity.class);
					startActivity(backToMain);
				}
				
			}); 
			
			
			
		}
		
		public void chooseItemOnClickHandler(View v) {
			// http://looksok.wordpress.com/2012/11/03/android-custom-listview-tutorial/
			ListViewCustom1 itemToSend = (ListViewCustom1 ) v.getTag();
			ArrayList<ListViewCustom1> item = new ArrayList<ListViewCustom1>();
			item.add(itemToSend);
			Intent backToSearch = new Intent(v.getContext(), SearchActivity.class);
			// We're sending it as "fromquery" as to not mess up the search activity
			backToSearch.putParcelableArrayListExtra("fromquery", item);
			startActivity(backToSearch);


	} 
	}

