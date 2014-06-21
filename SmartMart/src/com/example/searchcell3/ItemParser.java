package com.example.searchcell3;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringReader;
import java.util.ArrayList;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

public class ItemParser {

	public static ArrayList<ListViewCustom1> readItems(InputStream instream) {
		String XMLin;
		ArrayList<ListViewCustom1> itemList = new ArrayList<ListViewCustom1>();
		XmlPullParserFactory factory;
		String tag;
		try {
			XMLin = readString(instream);
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			XMLin = "Something went wrong";
		}
		try {
			// Set up the Class that will be parsing the xml file
			factory = XmlPullParserFactory.newInstance();
			factory.setNamespaceAware(true);
			XmlPullParser xpp = factory.newPullParser();
			xpp.setInput(new StringReader (XMLin));
			
			// Get the first event type
			int eventType = xpp.getEventType();
			while (eventType != XmlPullParser.END_DOCUMENT) {
			// While we have not reached the end of the document, check for start tags	
				if (eventType == XmlPullParser.START_TAG) {
					tag = xpp.getName();
					
					
					// If it is floor, create a new object and circle through look for tags
					if (tag.equals("item") && eventType == XmlPullParser.START_TAG) {
						ListViewCustom1 item = new ListViewCustom1();
						String xLocation = "";
						String yLocation = "";
						// Take the space away
						eventType = xpp.next();
						// Start the loop  
						do {
							// Cycle through the potential tags
								if (tag.equals("id") && eventType == XmlPullParser.START_TAG) {
									eventType = xpp.next();
									String id = xpp.getText().trim();
									item.setItemID(id);
								} else if (tag.equals("name") && eventType == XmlPullParser.START_TAG) {
									eventType = xpp.next();
	           						item.setTitle(xpp.getText());
								} else if (tag.equals("snippet") && eventType == XmlPullParser.START_TAG) {							
									eventType = xpp.next();
									String snip = xpp.getText().trim();
									item.setDesc(snip);
								} else if (tag.equals("upc") && eventType == XmlPullParser.START_TAG) {
									eventType = xpp.next(); 
									item.setUPC(xpp.getText());
								} else if (tag.equals("location") && eventType == XmlPullParser.START_TAG) {									
									while (!(tag.equals("location") && eventType == XmlPullParser.END_TAG)) {
										
										eventType = xpp.next(); 
										if (eventType == XmlPullParser.TEXT) {
											tag = xpp.getText();
										} else {
											tag = xpp.getName();
										}
										if (tag.equals("x") && eventType == XmlPullParser.START_TAG) {
											eventType = xpp.next(); 
											tag = xpp.getText();
											xLocation += tag +",";
											item.setX(xLocation);
										} else if (tag.equals("y") && eventType == XmlPullParser.START_TAG) {
											eventType = xpp.next(); 
											tag = xpp.getText();
											yLocation += tag +",";
											item.setY(yLocation);
											
										}
										
									}
								} 
								// Cycle through the tags
							eventType = xpp.next();
							if (eventType == XmlPullParser.TEXT) {
								tag = xpp.getText();
							} else {
								tag = xpp.getName();
							}
						} while(!(tag.equals("item")));
						itemList.add(item);
						
					} 
				} 
				eventType = xpp.next();
			}

		} catch (XmlPullParserException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		for (int i = 0; i < itemList.size(); i++) {
				System.out.println("xx new " + itemList.get(i).getY() + " " + itemList.get(i).getTitle());
				int parsed = Integer.parseInt(itemList.get(i).getItemID().trim());
				if (parsed == 20 || parsed == 21) {
					int newNum = (int) Math.round(((Float.parseFloat(itemList.get(i).getY().replace(",","").trim()))*22 + 150));
					System.out.println("xx " + newNum);
					itemList.get(i).setY(newNum + "");
				} 
		
		}
		return itemList;
	}
	
	  public static String readString(InputStream inputStream) throws IOException {

		    ByteArrayOutputStream into = new ByteArrayOutputStream();
		    byte[] buf = new byte[4096];
		    for (int n; 0 < (n = inputStream.read(buf));) {
		        into.write(buf, 0, n);
		    }
		    into.close();
		    return new String(into.toByteArray(), "UTF-8"); // Or whatever encoding
		}
	
}
