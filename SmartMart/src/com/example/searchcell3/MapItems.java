package com.example.searchcell3;

public class MapItems {

	private String name;
	private String street;
	private String city;
	private String state;
	private int zip;
	private String path;
	
	MapItems() {
		this.name = "Default";
		this.street = "Default";
		this.city = "Default";
		this.state = "Default";
		this.zip = 0;
		this.path = null;
	}
	
	public void setName(String name) {
		this.name = name;
	}
	
	public void setStreet(String street) {
		this.street = street;
	}
	
	public void setCity(String city) {
		this.city = city;
	}
	
	public void setState(String state) {
		this.state= state;
	}
	
	public void setZip(int zip) {
		this.zip = zip;
	}
	
	public void setPath(String path) {
		this.path = path;
	}
	
	public String getName() {
		return this.name;
	}
	
	public String getStreet() {
		return this.street;
	}
	
	public String getCity() {
		return this.city;
	}
	
	public String getState() {
		return this.state;
	}
	
	public int getZip() {
		return this.zip;
	}
	
	public String getPath() {
		return this.path;
	}
	
	
	
	
}
