package com.example.searchcell3;

import java.util.ArrayList;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringReader;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

public class MapParser {
	static final String TOP = "top";
	static final String LEFT = "left";
	static final String RIGHT = "right";
	static final String BOTTOM = "bottom";
	static final String FLOOR = "floor";
	static final String SHELF = "shelf";
	static final String STORE = "store";
	
	public static ArrayList<ArrayList<DefineRectangles>> readMapFile(InputStream instream) {
		String XMLin;
		ArrayList<ArrayList<DefineRectangles>> completeList = new ArrayList<ArrayList<DefineRectangles>>();
		ArrayList<DefineRectangles> floorList = new ArrayList<DefineRectangles>();
		ArrayList<DefineRectangles> shelfList = new ArrayList<DefineRectangles>();
		ArrayList<DefineRectangles> floorBGList = new ArrayList<DefineRectangles>();
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
					if (tag.equals("floor") && eventType == XmlPullParser.START_TAG) {
		 				DefineRectangles floor = new DefineRectangles();
		 				DefineRectangles floorBG = new DefineRectangles();
		 				floor.setType(1);
		 				floorBG.setType(3);
						// Take the space away
						eventType = xpp.next();
						// Start the loop
						do {
							// Cycle through the potential tags
								if (tag.equals("top") && eventType == XmlPullParser.START_TAG) {
									eventType = xpp.next();
									String text = xpp.getText();
									floor.setTop(Integer.parseInt(text));
									floorBG.setTop(Integer.parseInt(text) - 5);
								} else if (tag.equals("bottom") && eventType == XmlPullParser.START_TAG) {
									eventType = xpp.next();
									String text = xpp.getText();
	           						floor.setBottom(Integer.parseInt(text));
	           						floorBG.setBottom(Integer.parseInt(text) + 5);
								} else if (tag.equals("right") && eventType == XmlPullParser.START_TAG) {
									eventType = xpp.next();
									String text = xpp.getText();
									floor.setRight(Integer.parseInt(text));
									floorBG.setRight(Integer.parseInt(text) + 5);
								} else if (tag.equals("left") && eventType == XmlPullParser.START_TAG) {
									eventType = xpp.next(); 
									String text = xpp.getText();
									floor.setLeft(Integer.parseInt(text));
									floorBG.setLeft(Integer.parseInt(text) - 5);
							}	
								// Cycle through the tags
							eventType = xpp.next();
							if (eventType == XmlPullParser.TEXT) {
								tag = xpp.getText();
							} else {
								tag = xpp.getName();
							}
						} while(!(tag.equals("floor")));
						floorList.add(floor);
						floorBGList.add(floorBG);
					} else if (tag.equals("shelf") && eventType == XmlPullParser.START_TAG) {
						DefineRectangles shelf = new DefineRectangles();
						shelf.setType(2);
						// Take the space away
						eventType = xpp.next();
						// Start the loop
						do {
							// Cycle through the potential tags
								if (tag.equals("top") && eventType == XmlPullParser.START_TAG) {
									eventType = xpp.next();
									shelf.setTop(Integer.parseInt(xpp.getText()));
								} else if (tag.equals("bottom") && eventType == XmlPullParser.START_TAG) {
									eventType = xpp.next();
									shelf.setBottom(Integer.parseInt(xpp.getText()));
								} else if (tag.equals("right") && eventType == XmlPullParser.START_TAG) {
									eventType = xpp.next();
									shelf.setRight(Integer.parseInt(xpp.getText()));
								} else if (tag.equals("left") && eventType == XmlPullParser.START_TAG) {
									eventType = xpp.next(); 
									shelf.setLeft(Integer.parseInt(xpp.getText()));
							}	
								// Cycle through the tags
							eventType = xpp.next();
							if (eventType == XmlPullParser.TEXT) {
								tag = xpp.getText();
							} else {
								tag = xpp.getName();
							}
						} while(!(tag.equals("shelf"))); 
						shelfList.add(shelf);
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
		completeList.add(floorBGList);
		completeList.add(floorList);
		completeList.add(shelfList);
		return completeList;
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

