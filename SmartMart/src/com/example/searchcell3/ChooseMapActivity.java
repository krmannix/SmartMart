package com.example.searchcell3;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ActivityOptions;
import android.content.Intent;
import android.content.res.AssetManager;
import android.os.Bundle;
import android.view.GestureDetector;
import android.view.GestureDetector.OnGestureListener;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ListView;
import android.widget.Toast;

/**************** 
 * @author K. Rodman Mannix
 * REU IR @ Texas State University Summer 2013
 * Partners: Hannah Bowles, Dylan Hicks
 * Mentor: Dr. Byron Gao
 * 
 * THIS ACTIVITY HAS BEEN SILENCED FOR DEMO PURPOSES
 * 
 * This Activity simply lists all the available XML files to draw a map from. Originally developed on a tablet, there 
 * may be some text overlay in the clothes. It is a relatively simple Activity and uses the AssetsManager to run through all
 * the maps and each map in a MapItems object, and use an ArrayList of MapItems in a Custom Adapter.
 *
 */

public class ChooseMapActivity extends Activity implements OnGestureListener {
	
	ListView itemlist;
	CustomAdapterForChooseMap adapter;
	GestureDetector gDetector;
	Intent goToSearch;
	
	
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.choosemaplayout);
		
		goToSearch = new Intent(this, SearchActivity.class);
		AssetManager assetManager = getAssets();
		ArrayList<MapItems> listOfItems = new ArrayList<MapItems>();
		InputStream instream;
		try {
			String[] listOfAssets = assetManager.list("maps");
			for (int i = 0; i < listOfAssets.length; i++) {
				instream = assetManager.open("maps/" + listOfAssets[i]);
				MapItems item = ChooseMapParser.readMapFile(instream);
				item.setPath("maps/" + listOfAssets[i]);
				listOfItems.add(item);
				
			}
			
		} catch (IOException e) {
			e.printStackTrace();
			System.out.println("xxWentWrong");
		}

		itemlist = (ListView) findViewById(android.R.id.list);
		
		adapter = new CustomAdapterForChooseMap(listOfItems, this, R.layout.listview_row_custom1);
		itemlist.setAdapter(adapter);
		
		// This will recognize long touches or flings
		gDetector = new GestureDetector(this, new GestureDetector.SimpleOnGestureListener());
		final GestureDetector gestureDetector = new GestureDetector(this, this);
        View.OnTouchListener gestureListener = new View.OnTouchListener() {
            public boolean onTouch(View v, MotionEvent event) {  
                return gestureDetector.onTouchEvent(event); 
            }};
        itemlist.setOnTouchListener(gestureListener);
		
		
	}


	@Override
	public boolean onDown(MotionEvent arg0) {
		// TODO Auto-generated method stub
		return false;
	}


	@SuppressLint("NewApi")
	@Override
	public boolean onFling(MotionEvent e1, MotionEvent e2, float arg2,
			float arg3) {
		// TODO Auto-generated method stub
		if (e1.getX() > e2.getX() && (Math.abs(e2.getX() - e1.getX())) > 40 && (Math.abs(e2.getY() - e1.getY())) < 60 ) {
			Bundle bndlanimation =
					ActivityOptions.makeCustomAnimation(getApplicationContext(), R.anim.ani1, R.anim.ani2).toBundle();
			startActivity(goToSearch ,bndlanimation);

	}	
	  
	return false;
	}


	@Override
	public void onLongPress(MotionEvent e) {
		// TODO Auto-generated method stub
		
		int id = itemlist.pointToPosition((int) e.getX(), (int) e.getY());
		MapItems itemDetail = (MapItems) adapter.getItem((id));
		Toast.makeText(getApplicationContext(), "Map \"" + itemDetail.getName() + "\" chosen!", Toast.LENGTH_LONG).show();
		goToSearch.putExtra("MapPath", itemDetail.getPath());
		goToSearch.putExtra("MapName", itemDetail.getName());
		
	}


	@Override
	public boolean onScroll(MotionEvent arg0, MotionEvent arg1, float arg2,
			float arg3) {
		// TODO Auto-generated method stub
		return false;
	}


	@Override
	public void onShowPress(MotionEvent arg0) {
		// TODO Auto-generated method stub
		
	}


	@Override
	public boolean onSingleTapUp(MotionEvent arg0) {
		// TODO Auto-generated method stub
		return false;
	}
	
	
}
