package com.example.searchcell3;

import android.graphics.Rect;

public class DefineRectangles {

	// This class will hold all the information needed to draw the rectangles and will be referenced in an ArrayList
	public int left;
	public int top;
	public int right;
	public int bottom;
	public int type; // 0 is NonFloor, 1 is Floor, 2 is shelves
	public Rect rect;
	
	DefineRectangles() {
		this.left = 0;
		this.top = 0;
		this.right = 0;
		this.bottom = 0;
		this.type = 0;	
		this.rect = new Rect(0,0,0,0);
	}
	
	DefineRectangles(int left, int top, int right, int bottom, int type) {
		this.left = left;
		this.top = top;
		this.right = right;
		this.bottom = bottom;
		this.type = type;
		this.rect = new Rect(left, top, right, bottom);
	}
	
	public int getLeft() {
		return this.left;
	}
	
	public int getTop() {
		return this.top;
	}

	
	public int getRight() {
		return this.right;
	}
	
	public int getBottom() {
		return this.bottom;
	}
	
	public int getType() {
		return this.type;
	}
	
	public Rect getRect() {
		return this.rect;
	}
	
	public void setLeft(int left) {
		this.left = left;
		this.rect = new Rect(left, this.top, this.right, this.bottom);
	}
	
	public void setTop(int top) {
		this.top = top;
		this.rect = new Rect(this.left, top, this.right, this.bottom);
	}
	
	public void setRight(int right) {
		this.right = right;
		this.rect = new Rect(this.left, this.top, right, this.bottom);
	}
	
	public void setBottom(int bottom) {
		this.bottom = bottom;
		this.rect = new Rect(this.left, this.top, this.right, bottom);
	}
	
	public void setType(int type) {
		this.type = type;
	}
	
	public String toString() {
		return "Top: " + this.top + " Left: " + this.left + " Right: " + this.right + " Bottom: " + this.bottom;	
	}
	
}
