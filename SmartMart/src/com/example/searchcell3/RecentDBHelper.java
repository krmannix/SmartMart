package com.example.searchcell3;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;



import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;


public class RecentDBHelper extends SQLiteOpenHelper {

	/*	
	private SQLiteDatabase database;
	private DatabaseHelperV2 dbhelper;
	private String[] allColumns = { DatabaseHelperV2.colNum, DatabaseHelperV2.colTitle, DatabaseHelperV2.colSubtitle, DatabaseHelperV2.colLoc};
	*/
	
	static final String dbName = "itemlistRecent.db";
	static final String itemTable = "ItemsRecent";
	static final String colNum = "_id";
	static final String colTitle = "ItemTitle";
	static final String colSubtitle = "ItemSubtitle";
	static final String colLoc = "ItemLocation";
	
	static final String viewItems="ViewItems";
	static final String DATABASE_CREATE = "CREATE TABLE "+itemTable+" ("+colNum+" INTEGER PRIMARY KEY AUTOINCREMENT, "+
			colTitle+ " TEXT, "+colSubtitle+" TEXT, " + colLoc+ " TEXT)";
	
	public RecentDBHelper(Context context) {
		  super(context, dbName, null,1); 
		  }
	
	public void onCreate(SQLiteDatabase db) {
		db.execSQL("CREATE TABLE "+itemTable+" ("+colNum+" INTEGER PRIMARY KEY AUTOINCREMENT, "+
	colTitle+ " TEXT, "+colSubtitle+" TEXT, " + colLoc+ " TEXT)");
	}
	
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		db.execSQL("DROP TABLE IF EXISTS " + itemTable);
		onCreate(db);
	}
	
	public void addItem(ListViewCustom1 item) {
		SQLiteDatabase db = this.getWritableDatabase();
		
		ContentValues values = new ContentValues();
		values.put("ItemTitle", item.getTitle());
		values.put("ItemSubtitle", item.getSubTitle());
		values.put("ItemLocation", item.getLocation());
		db.insert(itemTable, null, values);
		db.close();
		
	}
	
	public ListViewCustom1 getItem(int id) {
		SQLiteDatabase db = this.getReadableDatabase();
		
		 Cursor cursor = db.query(itemTable, new String[] { colNum,
		            colTitle, colSubtitle, colLoc }, colNum + "=?",
		            new String[] { String.valueOf(id) }, null, null, null, null);
		    if (cursor != null)
		        cursor.moveToFirst();
		 
		    ListViewCustom1 contact = new ListViewCustom1(Integer.parseInt(cursor.getString(0)),
		            cursor.getString(1), cursor.getString(2), cursor.getString(3));
		    // return contact
		    return contact;
	}
	
	public List<ListViewCustom1> getAllItems() {
		
		List<ListViewCustom1> contactList = new ArrayList<ListViewCustom1>();
	    // Select All Query
	    String selectQuery = "SELECT  * FROM " + itemTable;
	 
	    SQLiteDatabase db = this.getWritableDatabase();
	    Cursor cursor = db.rawQuery(selectQuery, null);
	 
	    // looping through all rows and adding to list
	    if (cursor.moveToFirst()) {
	        do {
	        	ListViewCustom1 contact = new ListViewCustom1();
	            contact.setID(Integer.parseInt(cursor.getString(0)));
	            contact.setTitle(cursor.getString(1));
	            contact.setSubTitle(cursor.getString(2));
	            contact.setLocation(cursor.getString(3));
	            // Adding contact to list
	            contactList.add(contact);
	        } while (cursor.moveToNext());
	    }
	 
	    // return contact list
	    db.close();
	    Collections.reverse(contactList);
	    return contactList;
	}
	
	public int getItemsCount() {
		
        String countQuery = "SELECT  * FROM " + itemTable;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(countQuery, null);
        cursor.close();
 
        // return count
        return cursor.getCount();
	}
	
	public void deleteItem(ListViewCustom1 contact) {
		SQLiteDatabase db = this.getWritableDatabase();
		//long id = contact.getID();
	    db.delete(itemTable, colNum + " = ?",
	            new String[] { String.valueOf(contact.getID()) });
	    db.close();
	}
	
	

		
		
	}
