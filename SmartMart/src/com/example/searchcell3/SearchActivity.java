package com.example.searchcell3;


import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.Socket;
import java.util.ArrayList;
import android.annotation.SuppressLint;
import android.app.ActivityOptions;
import android.app.AlertDialog;
import android.app.ListActivity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Resources;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Html;
import android.view.GestureDetector;
import android.view.GestureDetector.OnGestureListener;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

/**************** 
 * @author K. Rodman Mannix
 * REU IR @ Texas State University Summer 2013
 * Partners: Hannah Bowles, Dylan Hicks
 * Mentor: Dr. Byron Gao
 * 
 * This is the first Activity upon loading the application. A user can enter a keyword
 * to search for in the EditText and hit the Button labelled "Search" to send a query
 * to the server. A list of returned objects will appear in the ListView (this is done
 * through XML parsing - the server returns an XML files with all the results). A user
 * can either swipe to the left to go to the ChooseMapActivity.class or to the right to
 * the MapListActivity.class
 *
 */ 



public class SearchActivity extends ListActivity implements OnGestureListener {
	
	
	// Define the Buttons seen in this View
	Button search;
	Button del;
	EditText searchbar;
	ListView itemlist;
	ArrayList<ListViewCustom1> qResults;
	DatabaseHelperV2 dbV2;
	Intent goToMap;
	Intent goToChooseMap;
	
	// Variables for Async
	public static String lookupServerAddress = "";
	public static int lookupServerPort = 6001;
		
		
	Button back;
	Button select;
	ArrayList<ListViewCustom1> details;
	CustomAdapter4Query adapter;
	ListViewCustom1 Detail;
	ConnectivityManager connMgr;
	NetworkInfo networkInfo;
	Context context;
	Resources res;
	ProgressBar loading;
	TextView noresults;
	View view;
	ArrayList<AlertDialog> allAlerts;
	AlertDialog alertDetails;
	AlertDialog.Builder buildDetail;
	View footerView;
	String prevSearch;
	int nextPage;
	boolean moreItems;
	
	// For Gesture Detection
	GestureDetector gDetector;
	AlertDialog alert;
	int itemCheck;
	
	public void onCreate(Bundle savedInstanceState) {
		
		// Associate this Activity with the searchactivitylayout Layout
		super.onCreate(savedInstanceState);
		setContentView(R.layout.searchactivitylayout);
				
		// Assign buttons to their respective XML-defined buttons
		search = (Button) findViewById(R.id.searchbutton);
		searchbar = (EditText) findViewById(R.id.searchedittext);
		loading = (ProgressBar) findViewById (R.id.marker_progress_search);
		itemlist = getListView();
		context = this;
		itemCheck = 2;
		
		// This page will give the opportunity to see more of the results
		footerView = getLayoutInflater().inflate(R.layout.search_footer_layout, itemlist, false);
		//itemlist.addFooterView(footerView);
		// This is the first page we will ask for if the user wants to see more results
		nextPage = 2;
		// This will let us know if the server has more items to return
		moreItems = false;
		
		// Create arrayList for all the possible information Alerts
		allAlerts = new ArrayList<AlertDialog>();
		buildDetail = new AlertDialog.Builder(this);
		
		// Create intent for when user wants to go to map page
		goToMap = new Intent(this, MapListActivity.class);
		goToChooseMap = new Intent(this, ChooseMapActivity.class);
		String checkForExtra = getIntent().getStringExtra("MapPath");
		
		if (checkForExtra != null) {
			 goToMap.putExtra("MapPath", checkForExtra);
			 goToMap.putExtra("MapName", getIntent().getStringExtra("MapName"));
		}
		
		
		// Create databases
		dbV2 = new DatabaseHelperV2(this);
		
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setTitle("Alert").setMessage("You need to select at least one item!").setPositiveButton("Ok", new android.content.DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface arg0, int arg1) {
				// TODO Auto-generated method stub							
			}						
		});
		alert = builder.create();
		
		
		
		
		gDetector = new GestureDetector(this, new GestureDetector.SimpleOnGestureListener());
		final GestureDetector gestureDetector = new GestureDetector(this, this);
        View.OnTouchListener gestureListener = new View.OnTouchListener() {
            public boolean onTouch(View v, MotionEvent event) {  
            	view = v;
                return gestureDetector.onTouchEvent(event); 
            }};
        itemlist.setOnTouchListener(gestureListener);
        
        
     
		search.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				String searchtext = searchbar.getText().toString();
				if (!searchtext.matches("")) {
					footerView.setVisibility(View.INVISIBLE);
					loading.setVisibility(View.VISIBLE);
					prevSearch = searchtext;
					getResults(searchtext, 0);
					
				}
			}
		});
		
		
		 
	}
	
	
	public void selectItemOnClickHandler(View v) {
		// http://looksok.wordpress.com/2012/11/03/android-custom-listview-tutorial/
		final DatabaseHelperV2 dbV2 = new DatabaseHelperV2(this);
		ListViewCustom1 itemToAdd = (ListViewCustom1) v.getTag();
		view = v;
		int index = -1;
		for (int i = 0; i < qResults.size(); i++) {
			if (qResults.get(i).equals(itemToAdd)) {
				index = i;
			}
			
		}
		
		
		if (itemToAdd.getSelected()) {
			ArrayList<ListViewCustom1> fullList = (ArrayList<ListViewCustom1>) dbV2.getAllItems();			
			for (int i = 0; i < fullList.size(); i++) {			
				if (itemToAdd.equals(fullList.get(i))) {
					if (index != -1) {
						qResults.get(index).deselect();
					}					
					dbV2.deleteItem(fullList.get(i));
					continue;
				}
			}
		} else {
			if (index != -1) {
				qResults.get(index).select();
			}			
			itemToAdd.select();
			dbV2.addItem(itemToAdd);
		} 
		
		adapter = new CustomAdapter4Query(qResults, getBaseContext(), R.layout.listview_row_custom2);
		setListAdapter(adapter);
	}
	
	public void getResults(String search, int whichState) {
		switch (whichState) {
		case 0:
			new DownloadEntries().execute(search);
			break;
	/*	case 1:
			String request = "page " + nextPage;
			//System.out.println("xx " + request + ".");
			new MoreEntries().execute(request);
			break;
		*/
		
		}
			
	}
	
	
	public class DownloadEntries extends AsyncTask<String, Void, String> {

		ArrayList<ListViewCustom1> itemList = new ArrayList<ListViewCustom1>(); 
		
		@Override
		protected String doInBackground(String... request) {
			Socket sk;
			try {
				
				
				lookupServerAddress = "dmlab.cs.txstate.edu";
				lookupServerPort = 8000;
				
				sk = new Socket(lookupServerAddress, lookupServerPort);				

				InputStream mdbInputStream = sk.getInputStream();
				OutputStream mdbOutputStream = sk.getOutputStream();
				BufferedReader reader = new BufferedReader(new InputStreamReader(mdbInputStream));
				// Send
				// lookup    
				// *item*     
				
				mdbOutputStream.write("lookup".getBytes("US-ASCII"));
				mdbOutputStream.write("\n".getBytes("US-ASCII"));
				mdbOutputStream.write(request[0].getBytes("US-ASCII"));
				mdbOutputStream.write("\n".getBytes("US-ASCII"));
				mdbOutputStream.flush();
				StringBuilder mdbOutput = new StringBuilder();
				String ch = (String) reader.readLine();
				boolean endOfFile = false;
				while (!endOfFile) {
					mdbOutput.append(ch);
					ch = (String) reader.readLine();
					if (ch.equals("</query>")) {
						endOfFile = true;
						break;
					}
					
				}
  
				mdbInputStream.close();
				mdbOutputStream.close();
				sk.close();

				return mdbOutput.toString();
				
				
			} catch (Exception e) {
				return e.toString();
				//return "Connection Error";
			}
		}

		// onPostExecute displays the results of the AsyncTask.
		@Override
		protected void onPostExecute(String result) {
			// Find Views that will be changed after the results finish loading
			noresults = (TextView) findViewById(R.id.noresults2);
			loading = (ProgressBar) findViewById(R.id.marker_progress_search);  
			final DatabaseHelperV2 dbV2 = new DatabaseHelperV2(getBaseContext());
			ArrayList<ListViewCustom1> dbList = (ArrayList<ListViewCustom1>) dbV2.getAllItems();
			// Create the List that will hold the values from Async, then get values
			
			InputStream is = new ByteArrayInputStream(result.getBytes());
			itemList = ItemParser.readItems(is);
			qResults = itemList;
			
			// Makes sure the results are checked with existing database items  to change button check
			// and not add duplicate items
			for (int i = 0; i < itemList.size(); i++) {
				
				for (int j = 0; j < dbList.size(); j++) {
					if (itemList.get(i).equals(dbList.get(j))) {
						itemList.get(i).select();
					}		
				}
				
			}
			
			// Check if there are any results. If so, display them, if not, tell the user

			if (itemList.size() == 15) {
				adapter = new CustomAdapter4Query(itemList, getBaseContext(), R.layout.listview_row_custom2);
				setListAdapter(adapter); 
				noresults.setVisibility(View.INVISIBLE);
				footerView.setVisibility(View.VISIBLE);
				System.out.println("xx HEREREREE");
			} else if (itemList.size() > 0) {
				adapter = new CustomAdapter4Query(itemList, getBaseContext(), R.layout.listview_row_custom2);
				setListAdapter(adapter); 
				noresults.setVisibility(View.INVISIBLE);
				footerView.setVisibility(View.GONE);
			} else {
				adapter = new CustomAdapter4Query(itemList, getBaseContext(), R.layout.listview_row_custom2);
				setListAdapter(adapter);
				noresults.setVisibility(View.VISIBLE);
				footerView.setVisibility(View.GONE);
			}
			loading.setVisibility(View.INVISIBLE);
		}
	}
	
	public class MoreEntries extends AsyncTask<String, Void, String> {

		ArrayList<ListViewCustom1> itemList = new ArrayList<ListViewCustom1>(); 
		
		@Override
		protected String doInBackground(String... request) {
			Socket sk; 
			try {
				sk = new Socket(lookupServerAddress, lookupServerPort);				
				InputStream mdbInputStream = sk.getInputStream();
				OutputStream mdbOutputStream = sk.getOutputStream();
				BufferedReader reader = new BufferedReader(new InputStreamReader(mdbInputStream));
				// Send
				// page 2    
				// *item*     
				System.out.println("xx got here");
				mdbOutputStream.write("page 2".getBytes("US-ASCII"));
				mdbOutputStream.write("\n".getBytes("US-ASCII"));
				mdbOutputStream.write("\n".getBytes("US-ASCII"));
				mdbOutputStream.write("\n".getBytes("US-ASCII"));  
				mdbOutputStream.flush();
				
				StringBuilder mdbOutput = new StringBuilder();
				System.out.println("xx stuck");
				String ch = (String) reader.readLine();
				System.out.println("xx ch " + ch);
				boolean endOfFile = false;
				while (!endOfFile) {
					
					mdbOutput.append(ch);
					ch = (String) reader.readLine();
					if (ch.equals("Not a valid page.")) {
						System.out.println("xx NOT VALID");
						endOfFile = true;
						break;
					}
					 
				}
  
				mdbInputStream.close();
				mdbOutputStream.close();
				sk.close();
				System.out.println("xx OUTPUT " + mdbOutput.toString());
				return mdbOutput.toString();
				
				
			} catch (Exception e) {
				return e.toString();
				//return "Connection Error";
			}
		}

		// onPostExecute displays the results of the AsyncTask.
		@Override
		protected void onPostExecute(String result) {
			// Find Views that will be changed after the results finish loading
			noresults = (TextView) findViewById(R.id.noresults2);
			loading = (ProgressBar) findViewById(R.id.marker_progress_search);  
			final DatabaseHelperV2 dbV2 = new DatabaseHelperV2(getBaseContext());
			ArrayList<ListViewCustom1> dbList = (ArrayList<ListViewCustom1>) dbV2.getAllItems();
			// Create the List that will hold the values from Async, then get values
			
			InputStream is = new ByteArrayInputStream(result.getBytes());
			itemList = ItemParser.readItems(is);
			qResults = itemList;
			
			// Makes sure the results are checked with existing database items  to change button check
			// and not add duplicate items
			for (int i = 0; i < itemList.size(); i++) {
				
				for (int j = 0; j < dbList.size(); j++) {
					if (itemList.get(i).equals(dbList.get(j))) {
						itemList.get(i).select();
					}		
				}
				
			}
			
			// Check if there are any results. If so, display them, if not, tell the user
			if (itemList.size() > 0) {		  
				adapter = new CustomAdapter4Query(itemList, getBaseContext(), R.layout.listview_row_custom2);
				setListAdapter(adapter); 
				noresults.setVisibility(View.INVISIBLE);
				footerView.setVisibility(View.VISIBLE);
			} else {
				adapter = new CustomAdapter4Query(itemList, getBaseContext(), R.layout.listview_row_custom2);
				setListAdapter(adapter);
				noresults.setVisibility(View.VISIBLE);
				footerView.setVisibility(View.INVISIBLE);
			}
			loading.setVisibility(View.INVISIBLE);
		}
	}


	@Override
	public boolean onDown(MotionEvent e) {

		// TODO Auto-generated method stub
		return false;
	}

	@SuppressLint("NewApi")
	@Override
	public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX,
			float velocityY) {
				
				if (e2.getX() < e1.getX() && (Math.abs(e2.getX() - e1.getX())) > 40 && (Math.abs(e2.getY() - e1.getY())) < 60 ) {
            			Bundle bndlanimation =
        						ActivityOptions.makeCustomAnimation(getApplicationContext(), R.anim.ani1, R.anim.ani2).toBundle();
            			startActivity(goToMap, bndlanimation);
				}  /*else if (e2.getX() > e1.getX() && (Math.abs(e2.getX() - e1.getX())) > 40 && (Math.abs(e2.getY() - e1.getY())) < 60 ) {
        			
        			Bundle bndlanimation =
    						ActivityOptions.makeCustomAnimation(getApplicationContext(), R.anim.ani4, R.anim.ani3).toBundle();
        			startActivity(goToChooseMap, bndlanimation);
			}*/  
		   
		return false;
	}

	@Override
	public void onLongPress(MotionEvent e) {
		int id = itemlist.pointToPosition((int) e.getX(), (int) e. getY());
		ListViewCustom1 itemDetail = (ListViewCustom1) adapter.getItem((id));
		buildDetail.setTitle(itemDetail.getTitle().trim()).setMessage(Html.fromHtml(createMessage(itemDetail))).setPositiveButton("Ok", new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface arg0, int arg1) {
				// TODO Auto-generated method stub			
			}				
		});
		alertDetails = buildDetail.create();
		alertDetails.show();
		// TODO Auto-generated method stub
		
	}

	@Override
	public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX,
			float distanceY) {

		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void onShowPress(MotionEvent e) {

		// TODO Auto-generated method stub
		
	}

	@Override
	public boolean onSingleTapUp(MotionEvent e) {
		int id = itemlist.pointToPosition((int) e.getX(), (int) e. getY());
		int total = itemlist.getCount() - 1;
		//String contSearch = prevSearch;
		if (id != -1 && id == total) {
			getResults("", 1);
			++nextPage;
		}
		
		// TODO Auto-generated method stub
		return false;
	}
	
	public String createMessage(ListViewCustom1 lvc) {
		String message = "";
		message += "<b>UPC: </b>" + lvc.getUPC() + "<br>";
		message += "<b>Location: </b>" + lvc.getLocation() + "<br>";
		message += "<b>Description: </b>" + lvc.getDesc() + "<br>";		
		return message;
	}


	
}
