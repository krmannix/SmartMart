package com.example.searchcell3;

import android.os.Parcel;
import android.os.Parcelable;

public class ListViewCustom1 implements Parcelable {
	
	private long id;
	public String title;
	public String subtitle;
	public String location;
	public String x;
	public String y;
	public String itemid;
	public String description;
	public String upc;
	public boolean selected;
	

	public ListViewCustom1() {
		this.title = "Default";
		this.subtitle = "Default";
		this.location = "A1";
		this.x = "0";
		this.y = "0";
		this.itemid = "1";
		this.description = "Nothing important";
		this.upc="900068";
		this.selected = false;
	}
	
	public long getID() {
		return id;
	}
	
	public String getTitle() {
		return title;
	}
	
	public String getSubTitle() {
		return subtitle;
	}
	
	public String getLocation() {
		return location;
	}
	
	public String getX() {
		return x;
	}
	
	public String getY() {
		return y;
	}
	
	public String getItemID() {
		
		return itemid;
	}
	
	public String getDesc() {
		return description;
	}
	
	public String getUPC() {
		return upc;
	}
	
	public boolean getSelected() {
		return selected;
	}
	
	public void setID(long id) {
		this.id = id;
	}
	
	public void setTitle(String title) {
		this.title = title;
	}
	
	public void setSubTitle(String subtitle) {
		this.subtitle = subtitle;
	}
	
	public void setLocation(String location) {
		this.location = location;
	}
	
	public void setX(String x) {
		this.x = x;
	}
	
	public void setY(String y) {
		this.y = y;
	}
	
	public void setItemID(String i) {
		this.itemid = i;
	}
	
	public void setDesc(String desc) {
		this.description = desc;
	}
	
	public void setUPC(String upc) {
		this.upc = upc;
	}
	
	public void select() {
		this.selected = true;
	}
	
	public void deselect() {
		this.selected = false;
	}
	
	public boolean equals(ListViewCustom1 lvc) {
		if (Integer.parseInt(this.getItemID()) == Integer.parseInt(lvc.getItemID())) {
			return true;
		} else {
			return false;
		}
		
	}
	
	public ListViewCustom1(int id, String title, String subtitle) {
		this();
		this.id = id;
		this.title = title;
		this.subtitle = subtitle;
		this.location = "A1";
		this.selected = false;

	}
	
	public ListViewCustom1(int id, String title, String subtitle, String location) {
		this();
		this.id = id;
		this.title = title;
		this.subtitle = subtitle;
		this.location = location;
		this.selected = false;

	}
	
	public ListViewCustom1(int id) {
		this();
		this.id = id;
		this.title = "Default";
		this.subtitle = "Default";
		this.location = "A1";
		this.selected = false;

	}
	
	public ListViewCustom1(String title, String subtitle) {
		this();
		this.title = title;
		this.subtitle = subtitle;
		this.location = "A1";
		this.selected = false;
	}
	
	public ListViewCustom1(String title, String subtitle, String location) {
		this();
		this.title = title;
		this.subtitle = subtitle;
		this.location = location;
		this.selected = false;
	}
	
	// This is all for "Parcel" which is needed to send custom objects in intents. Here's the source
	// code: http://prasanta-paul.blogspot.com/2010/06/android-parcelable-example.html
	public static final Parcelable.Creator<ListViewCustom1> CREATOR = new Parcelable.Creator<ListViewCustom1>() {
        public ListViewCustom1 createFromParcel(Parcel in) {
            return new ListViewCustom1(in);
        }

		@Override
		public ListViewCustom1[] newArray(int size) {
			// TODO Auto-generated method stub
			return new ListViewCustom1[size];
		}
	};
	
	
	public ListViewCustom1(Parcel source){
        // Unwraps the parcel
        title = source.readString();
        subtitle = source.readString();
        location = source.readString();
        x = source.readString();
        y = source.readString();
        itemid = source.readString();
        description = source.readString();
        upc = source.readString();
  }

	@Override
	public int describeContents() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		// Creates the parcel
		//Log.v(TAG, "writeToParcel..."+ flags)
		dest.writeString(title);
		dest.writeString(subtitle);
		dest.writeString(location);
		dest.writeString(x);
		dest.writeString(y);
		dest.writeString(itemid);
		dest.writeString(description);
		dest.writeString(upc);
		
	}
	/*
	private void readFromParcel(Parcel in) {
		  title = in.readString();
		  subtitle = in.readString();
		  location = in.readString();
		  x = in.readString();
		  y = in.readString();
		  itemid = in.readString();
		  description = in.readString();
		  upc = in.readString();
		 
		}
*/
}
