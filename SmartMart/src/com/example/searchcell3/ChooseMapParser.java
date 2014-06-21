package com.example.searchcell3;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringReader;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

public class ChooseMapParser {

	
	
	public static MapItems readMapFile(InputStream instream) {
		String XMLin;
		MapItems item = new MapItems();
		XmlPullParserFactory factory;
		
		try {
			XMLin = readString(instream);
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			XMLin = "Something went wrong";
		}
		try {
			String tag;
			// Set up the Class that will be parsing the xml file
			factory = XmlPullParserFactory.newInstance();
			factory.setNamespaceAware(true);
			XmlPullParser xpp = factory.newPullParser();
			xpp.setInput(new StringReader (XMLin));
			
			// Get the first event type
			int eventType = xpp.getEventType();
			while (eventType != XmlPullParser.END_DOCUMENT) {
				if (eventType == XmlPullParser.START_TAG) {
					tag = xpp.getName();
					if (tag.equals("info")) {
						
						do {
							// Run through to find tags
							if (tag.equals("name") && eventType == XmlPullParser.START_TAG) {
								eventType = xpp.next();
								String text = xpp.getText();
								item.setName(text);
							} else if (tag.equals("street") && eventType == XmlPullParser.START_TAG) {
								eventType = xpp.next();
								String text = xpp.getText();
           						item.setStreet(text);
							} else if (tag.equals("city") && eventType == XmlPullParser.START_TAG) {
								eventType = xpp.next();
								String text = xpp.getText();
								item.setCity(text);
							} else if (tag.equals("zip") && eventType == XmlPullParser.START_TAG) {
								eventType = xpp.next(); 
								String text = xpp.getText();
								item.setZip(Integer.parseInt(text));
						}	
							
							
							eventType = xpp.next();
							if (eventType == XmlPullParser.TEXT) {
								tag = xpp.getText();
							} else {
								tag = xpp.getName();
							}
						} while (!(eventType == XmlPullParser.END_TAG && tag.equals("info")));
						break;
						
						
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
		return item;
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
