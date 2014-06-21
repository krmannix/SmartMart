package com.example.searchcell3;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.ActivityOptions;
import android.app.AlertDialog;
import android.app.ListActivity;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.PointF;
import android.graphics.Bitmap.Config;
import android.graphics.RectF;
import android.graphics.drawable.BitmapDrawable;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.text.Html;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.GestureDetector;
import android.view.GestureDetector.OnGestureListener;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.AnimationSet;
import android.view.animation.TranslateAnimation;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

/**************** 
 * @author K. Rodman Mannix
 * REU IR @ Texas State University Summer 2013
 * Partners: Hannah Bowles, Dylan Hicks
 * Mentor: Dr. Byron Gao
 * 
 * This Activity is the main workhorse of the Application. It parses XML files in the /assests/maps/ folder and draws them
 * using the Android Draw and Canvas classes. Markers will also be drawn on this map and are clickable for more information
 * via AlertDialogs. The map is an ImageView that can be moved around via a finger touch. The ZOOM function has been removed
 * as it has been deemed to buggy at this time. 
 *
 */


public class MapListActivity extends ListActivity implements OnGestureListener {
	
	// Defining all the buttons
	Button clearlist;
	ListView itemlist;
	ImageView map;
	ImageView grid;
	CustomAdapter1 adapter;
	ArrayList<ListViewCustom1> values;
	DatabaseHelperV2 dbV2;
	RelativeLayout rl;
	TextView storenamelabel;
	// gDetector will recognize onLongClicks and onFlings
	GestureDetector gDetector;
	// These AlertDialogs are for the marker longClicks and list longClicks respectively 
	AlertDialog.Builder markerBuilder;
	AlertDialog alertMarker;
	AlertDialog.Builder listBuilder;
	AlertDialog alertList;
	// markerheight/markerwidth 
	int markerheight;
	int markerwidth;
	// "realHeight" is the real pizel size of the screen
	double realHeight;
	// "realImageHeight" is the real pixel size of the map
	float realImageHeight;
	// "completeList" contains all the Rect objects used to define the map 
	ArrayList<ArrayList<DefineRectangles>> completeList;
	int ScreenWidth;
	int ScreenHeight;
	boolean isOpen;
	int deltaY;
	View bottomOfMap;
	float topMarginLine;
	float bottomLine;
	float diffForMap;
	int ImageHeight;
	int ImageWidth;
	TextView noresults;
	ProgressBar loadingimage;
	// SCALE_FACTOR deals with the conversion from units of measurement to pixels
	double SCALE_FACTOR;
	// MAP is the string to which XML file will be used to describe the Map
	String MAP;
	Intent goToSearch;
	float[] valuesScale; 
	
	
	/////////////////////////////////////////////////////////////////////
	// For the Map Image Matrices:
	// These matrices will be used to move and zoom image
	Matrix matrix = new Matrix();
	Matrix savedMatrix = new Matrix();
	Matrix markerMatrix = new Matrix();
	int oldX;
	int oldY;
    // We can be in one of these 3 states
	static final int NONE = 0;
	static final int DRAG = 1;
	static final int ZOOM = 2;
	int mode = NONE;

	// Remember some things for zooming
	PointF start = new PointF();
	PointF longStart = new PointF();
	PointF mid = new PointF();
	float oldDist = 1f;
	float permOldDist = 1f;
	   
	// Set the link to the drawable map that will be used
	static final int DRAWABLE_LOC = R.drawable.biggerwalmart;
	
	// Create the scale variable
	float scale = 1;
	float oldScale = 1;
	int countTimes;
	
	// bigY is a variable used in the calculation for the openList method's animation
	float bigY;
	
	// Create the handler for the longClick event
	final Handler handler = new Handler(); 
	
	float wholeLayoutHeight;
	float difBetween;
	

	//////////////////////////////////////////////////////////////////////
	
	@SuppressLint("NewApi")
	@SuppressWarnings({ "deprecation" })
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.maplistlayout);
		// Open the activities database
		dbV2 = new DatabaseHelperV2(this);
		values = new ArrayList<ListViewCustom1>(dbV2.getAllItems());
		
		// Define all the tools in layout by their IDs
		clearlist = (Button) findViewById(R.id.clearmapbutton);
		map = (ImageView) findViewById(R.id.map);
		itemlist = (ListView) findViewById(android.R.id.list);
		rl = (RelativeLayout) findViewById(R.id.undermapbuttons);
		storenamelabel = (TextView) findViewById(R.id.storename);
		
		// Set alert Builder for later pop ups
		markerBuilder = new AlertDialog.Builder(this);
		listBuilder = new AlertDialog.Builder(this);
		isOpen = false;
		
		// For use in map scaling
		valuesScale = new float[9];
		// Set the map
		
		String checkForExtra = getIntent().getStringExtra("MapPath");
		if (checkForExtra != null) {
			 MAP = checkForExtra;
			 if (getIntent().getStringExtra("MapName").length() < 10) {
				 storenamelabel.setText(getIntent().getStringExtra("MapName"));
			 } else {
				 storenamelabel.setText(getIntent().getStringExtra("MapName").substring(0, 7) + "...");
			 }
			 
		} else {
			MAP = "maps/demomap1.xml";
		}
		
		// Make the intent that goes back to the Search page
		goToSearch = new Intent(this, SearchActivity.class);
		goToSearch.putExtra("MapPath", checkForExtra);
		goToSearch.putExtra("MapName", getIntent().getStringExtra("MapName"));
		
		
		// Set the adapter for the ListView to the custom one that was made along with the values in the DB
		adapter = new CustomAdapter1(values, this, R.layout.listview_row_custom1);
		itemlist.setAdapter(adapter);
		if (values.size() == 0) {
			
		}
		
		// Set the various variables that will be used in methods later on. These are needed on a global scale as multiple
		// methods will need to use them
		countTimes = 0;
		bigY = 500;
		SCALE_FACTOR = 1;	
		
		// THIS IS FOR THE MAP IMAGE //////////////////////////////////////////////////////////////////
		// This gets the screen dimensions
		// Create a new Point object to contain our size data
		// WindowManager will be the object that can realize the screen size
		DisplayMetrics metrics = new DisplayMetrics();
	    int SWidth = 0;  
	    int SHeight = 0;  
	    Point size = new Point();
	    WindowManager w = getWindowManager();
	    w.getDefaultDisplay().getMetrics(metrics);

	    // Check the SDK to get the total screen height
	    if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
	          w.getDefaultDisplay().getSize(size);
	          SWidth = size.x;
	          SHeight = size.y; 
	        } else {
	          Display d = w.getDefaultDisplay(); 
	          SWidth = d.getWidth(); 
	          SHeight = d.getHeight(); 
	        }
	    ScreenWidth = SWidth;  
	    ScreenHeight = SHeight;
	    // Creates size to hold ActionBar size
	    
	    // Check the screen density to get the title bar and status bar height, and find the real screen height
	    realHeight = 0;
	    switch (metrics.densityDpi) {
        case DisplayMetrics.DENSITY_HIGH:
            realHeight = ScreenHeight - 48 - (25 * 1);
            break;
        case DisplayMetrics.DENSITY_MEDIUM:
            realHeight = ScreenHeight - 32 - (25 * 1);
            break;
        case DisplayMetrics.DENSITY_LOW:
            realHeight = ScreenHeight - 24 - (25 * .75);
            break;
        default:
            realHeight = ScreenHeight;
	    }
	    
	    // Get the status bar height
	    
	    /////////////////////////////////////////////////////////////////////////////////////////
	    
		//
		//
	    // SET IMAGE SOURCE AND FIND BOUNDS Of OBJECT (as we may have an option for choosing different maps 
	    // 
	    // Get the margin of the non-image part of layout. This portion is able to be adjusted further down the line,
	    // so we do not want to hardcode a specific margin amount. The margin value will be used to adjust the scrollable bound
	    // of the image in the y-direction
	    bottomOfMap = findViewById(R.id.horizabove); //Our "line" view that is below the map image
	    ViewGroup.MarginLayoutParams layoutp = (ViewGroup.MarginLayoutParams) bottomOfMap.getLayoutParams();
	    topMarginLine = layoutp.topMargin;
	    bottomLine = topMarginLine;
	   // realImageHeight = (float) (realHeight - (realHeight - topMarginLine));
	    
	    
	   ViewGroup.MarginLayoutParams layoutWhole = (ViewGroup.MarginLayoutParams) rl.getLayoutParams();
	   wholeLayoutHeight = layoutWhole.height;
	   realImageHeight = (float) (realHeight - (wholeLayoutHeight - map.getHeight()));
	   
	   
	   
	    // Creates the bitmaps from these arrayLists
	    AssetManager assetManager = getAssets();
		InputStream instream;
		completeList = new ArrayList<ArrayList<DefineRectangles>>();
		try {
		instream = assetManager.open(MAP);	    
	    completeList = MapParser.readMapFile(instream);
		} catch (IOException e) {
			
		}
	    Bitmap realMap = createMap(ScreenWidth, realImageHeight, completeList);
	    map.setImageBitmap(realMap);
	    
	    
	    // get the bounds of the picture
	    BitmapDrawable bd=(BitmapDrawable) new BitmapDrawable(getResources(), realMap);
	    ImageHeight=bd.getBitmap().getHeight();
	    ImageWidth=bd.getBitmap().getWidth();
	    difBetween = (float) Math.abs(ImageHeight - ScreenHeight);
	    
	    
	    
		
	    
	    // Center Matrix on start of activity
	   // matrix.postTranslate(-(ImageWidth)/4, -(ImageHeight)/3);
	    map.setImageMatrix(matrix);
	    
	    ////////////////////////////////////////////////////////////////	

	    ////////////////////// ON-TOUCH LISTENERS FOR ALL BUTTONS /////////////////////////////////
        
		gDetector = new GestureDetector(this, new GestureDetector.SimpleOnGestureListener());
		final GestureDetector gestureDetector = new GestureDetector(this, this);
        View.OnTouchListener gestureListener = new View.OnTouchListener() {
            public boolean onTouch(View v, MotionEvent event) {  
                return gestureDetector.onTouchEvent(event); 
            }};
        itemlist.setOnTouchListener(gestureListener);
	    
		map.setOnTouchListener(new OnTouchListener() {
		    
			Runnable mLongPressed = new Runnable() { 
			    public void run() { 
			        // Call to method to display item information
			    	displayPopUp();
			    }   
			};
			 
		    @Override
		    public boolean onTouch(View v, MotionEvent event) {
		    	// Below is the code used to move the image around
		    	
		    	ImageView view = (ImageView) v;
		
			      // Handle touch events here... 
			      switch (event.getAction() & MotionEvent.ACTION_MASK) {
			      case MotionEvent.ACTION_DOWN:
			         savedMatrix.set(matrix);
			         start.set(event.getX(), event.getY());
			         handler.postDelayed(mLongPressed, 1000);
			         mode = DRAG;
			         break;
			      case MotionEvent.ACTION_POINTER_DOWN:
			         oldDist = spacing(event);
			         if (oldDist > 10f) {
			            savedMatrix.set(matrix);
			            midPoint(mid, event);
			            mode = ZOOM;
			         }
			         break;
			      case MotionEvent.ACTION_UP:
			    	  oldX = (int) event.getX();
			    	  handler.removeCallbacks(mLongPressed);
			      case MotionEvent.ACTION_POINTER_UP:
			         mode = NONE;
			         checkMatrix(event, ImageWidth, ScreenWidth, ImageHeight, topMarginLine);
			         break;
			      case MotionEvent.ACTION_MOVE:
			         if (mode == DRAG) {
			            // ...
			        	handler.removeCallbacks(mLongPressed);
			            matrix.set(savedMatrix);
			            float xdif = event.getX() - start.x;
			            float ydif = event.getY() - start.y;
			            matrix.postTranslate(xdif, ydif); 
			            checkMatrix(event, ImageWidth, ScreenWidth, ImageHeight, topMarginLine);
			         }
			         else if (mode == ZOOM) {
			         // **********************************
			         // This ZOOM function has currently been removed due to time constraints and bugs with the function
			         // that could not be solved in time
			         // **********************************
			        	 
			        	 
			        /* 	 
     	            float newDist = spacing(event);
		            if (newDist > 10f) {

		               matrix.getValues(valuesScale);
		               matrix.set(savedMatrix);
		               scale = newDist / oldDist;
		               matrix.postScale(scale, scale, mid.x, mid.y); 
		               checkMatrix(event, ImageWidth, ScreenWidth, ImageHeight, topMarginLine);
		            } */
			         }
			         break;
			      }
			      
			      // Set all Image Matrices
			      view.setImageMatrix(matrix);
			      
			      return true; // indicate event was handled
				
		    }
		});
			
		clearlist.setOnClickListener(new OnClickListener() {
			// On done, the user is sent back to the search activity and the database is cleared
			public void onClick(View v) {
				System.out.println("xx InClearList");
					ArrayList<ListViewCustom1> listToClear = (ArrayList<ListViewCustom1>) dbV2.getAllItems();
					for (int i = 0; i < listToClear.size(); i++) {
						dbV2.deleteItem(listToClear.get(i));
					}
					values = (ArrayList<ListViewCustom1>) dbV2.getAllItems();
					adapter = new CustomAdapter1(values, getBaseContext(), R.layout.listview_row_custom1);
		        	setListAdapter(adapter);
		        	// Animation if listview is open
		        	final RelativeLayout root = (RelativeLayout) findViewById( R.id.undermapbuttons );
		 		    ObjectAnimator moveYlist = ObjectAnimator.ofFloat(itemlist,  "y", deltaY + bigY);
		 		    moveYlist.setDuration(1);
		 		    AnimatorSet trans = new AnimatorSet();
		 		    trans.play(moveYlist);
		 	        Animation anim = new TranslateAnimation( 0, 0, 0, 0);
		 		    anim.setDuration(1);
		 		    anim.setFillEnabled(true);
		 		    anim.setFillBefore(true); 
		 		    anim.setAnimationListener(new AnimationListener() {
		 	        @Override
		 		        public void onAnimationStart(final Animation animi) {
		 		        };
		 	
		 		        @Override
		 		        public void onAnimationRepeat(final Animation animi) {
		 		        };  
		 	
		 		        @Override
		 		        public void onAnimationEnd(final Animation animi) {
		 		        	
		 		        	// To prevent flickering
		 		        	itemlist.clearAnimation();

		 		            // set new "real" position of wrapper
		 		        	itemlist.layout(itemlist.getLeft(), (int) itemlist.getTop(), itemlist.getRight(), (int) (itemlist.getTop() + root.getMeasuredHeight()));
		 		            
		 		        }
		 		    });
		 		    itemlist.startAnimation(anim);
		 		    anim.setFillAfter(true);
		 		    trans.start();	        
		 		    // End of animation for deletion
				    Bitmap realMap = createMap(ScreenWidth, realImageHeight, completeList);
				    map.setImageBitmap(realMap);
		        	}
		});
		
	/////////////////////////////////////////////////////////////////////////////////////
		
	}
	
	
	
	////////////////////// METHODS USED BY ACTIVITY //////////////////////////////////////
	
	@SuppressLint("NewApi")
	public void removeItemOnClickHandler(View v) {
		// http://looksok.wordpress.com/2012/11/03/android-custom-listview-tutorial/
		ListViewCustom1 itemToRemove = (ListViewCustom1 ) v.getTag();
		dbV2.deleteItem(itemToRemove);
		values = (ArrayList<ListViewCustom1>) dbV2.getAllItems();
		adapter = new CustomAdapter1(values, getBaseContext(), R.layout.listview_row_custom1);
    	setListAdapter(adapter);
    	if (isOpen) {
        	//final RelativeLayout root = (RelativeLayout) findViewById( R.id.undermapbuttons );
 		    ObjectAnimator moveYlist = ObjectAnimator.ofFloat(itemlist,  "y", deltaY + bigY);
 		    moveYlist.setDuration(1);
 		    AnimatorSet trans = new AnimatorSet();
 		    trans.play(moveYlist);
 	        Animation anim = new TranslateAnimation( 0, 0, 0, 0);
 		    anim.setDuration(1);
 		    anim.setFillEnabled(true);
 		    anim.setFillBefore(true);
 		    anim.setAnimationListener(new AnimationListener() {
 	        @Override
 		        public void onAnimationStart(final Animation animi) {
 		        };
 	
 		        @Override
 		        public void onAnimationRepeat(final Animation animi) {
 		        };  
 	
 		        @Override
 		        public void onAnimationEnd(final Animation animi) {
 		        	
 		        	// To prevent flickering
 		        	itemlist.clearAnimation();

 		            // set new "real" position of wrapper
 		        	itemlist.layout(itemlist.getLeft(), (int) itemlist.getTop(), itemlist.getRight(), (int) (itemlist.getTop() + Math.abs(deltaY)));
 		            
 		        }
 		    });
 		    itemlist.startAnimation(anim);
 		    anim.setFillAfter(true);
 		    trans.start();	        
    	}
		
	    Bitmap realMap = createMap(ScreenWidth, realImageHeight, completeList);
	    map.setImageBitmap(realMap);
		
	}
	  
	@TargetApi(Build.VERSION_CODES.HONEYCOMB)
	@SuppressLint("NewApi")
	public void openList(View v) {
		
		AnimationSet set = new AnimationSet(true);
		final RelativeLayout root = (RelativeLayout) findViewById( R.id.undermapbuttons );
		final Button openButton = (Button) findViewById(R.id.openbutton);
	    DisplayMetrics dm = new DisplayMetrics();
	    this.getWindowManager().getDefaultDisplay().getMetrics( dm );
	    int statusBarOffset = dm.heightPixels - root.getMeasuredHeight();
	    int yDest = dm.heightPixels/2 - (v.getMeasuredHeight()/2) - statusBarOffset + 180;
	    int originalPos[] = new int[2]; 
	    if (isOpen) {
	    	v.getLocationOnScreen( originalPos );
		    deltaY *= -1;
		    ObjectAnimator moveYrl = ObjectAnimator.ofFloat(rl,  "y", 0);
			ObjectAnimator moveYlist = ObjectAnimator.ofFloat(itemlist,  "y", bigY);
			AnimatorSet trans = new AnimatorSet();
			trans.play(moveYrl).with(moveYlist);
			    
		    
		    Animation anim = new TranslateAnimation( 0, 0, 0, 0);
		    anim.setDuration(1000);
		    anim.setFillEnabled(true);  
		    anim.setFillBefore(true);
		    
		    anim.setAnimationListener(new AnimationListener() {

		        @Override
		        public void onAnimationStart(final Animation animi) {
		        };

		        @Override
		        public void onAnimationRepeat(final Animation animi) {
		        };  

		        @Override
		        public void onAnimationEnd(final Animation animi) {
		        	
		        	// To prevent flickering
		        	rl.clearAnimation();
		        	itemlist.clearAnimation();

		            // set new "real" position of wrapper
		        	itemlist.layout(itemlist.getLeft(), (int) (itemlist.getTop()), itemlist.getRight(), (int) (itemlist.getTop() + 300));
		            rl.layout(rl.getLeft(), (int) (rl.getTop()), rl.getRight(), (int) (rl.getTop() + root.getMeasuredHeight()));
		            Animation animation = new TranslateAnimation(0.0f, 0.0f, 0.0f, 0.0f);
	                   animation.setDuration(1);
	                   itemlist.startAnimation(animation);
	                
		        }  
		    });
		    set.addAnimation(anim);
		    
		    
		    rl.startAnimation(anim);

		    itemlist.startAnimation(anim);
		    anim.setFillAfter(true);
		    trans.start();
		    
		    openButton.setText("Open");
		    bottomLine += deltaY;
		    isOpen = false;
	    } else {
	    
		    v.getLocationOnScreen( originalPos );
		    deltaY = yDest - originalPos[1];
		    if (countTimes == 0) {
		    	bigY = itemlist.getY();
		    }
		    ObjectAnimator moveYrl = ObjectAnimator.ofFloat(rl,  "y", deltaY);
		    
		       
		    ObjectAnimator moveYlist = ObjectAnimator.ofFloat(itemlist,  "y", deltaY + bigY);
		        
		    AnimatorSet trans = new AnimatorSet();
		    trans.play(moveYrl).with(moveYlist);
		    
		    
		   
	        Animation anim = new TranslateAnimation( 0, 0, 0, 0);
		    anim.setDuration(1);		    
		    anim.setAnimationListener(new AnimationListener() {
	
		        @Override
		        public void onAnimationStart(final Animation animi) {
		        };
	
		        @Override
		        public void onAnimationRepeat(final Animation animi) {
		        };  
	
		        @Override
		        public void onAnimationEnd(final Animation animi) {
		        	
		        	// To prevent flickering
		        	rl.clearAnimation();
		        	itemlist.clearAnimation();
		            // set new "real" position of wrapper    	
		            rl.layout(rl.getLeft(), (int) (rl.getTop()), rl.getRight(), (int) (rl.getTop() + root.getMeasuredHeight()));

		            // set new "real" position of wrapper
		       	
		        	itemlist.layout(itemlist.getLeft(), (int) itemlist.getTop(), itemlist.getRight(), (int) (itemlist.getTop() + Math.abs(deltaY)));
		        	
	
		            
		        } 
		    });
		    trans.start();
		    set.addAnimation(anim);
		    rl.startAnimation(anim);
		    itemlist.startAnimation(anim);
		    openButton.setText("Close");
		    bottomLine += deltaY;
		    isOpen = true;
		   
	    }
	    checkMatrix(ImageWidth, ScreenWidth, ImageHeight, topMarginLine);
	}
	
    public Bitmap createMap(int ScreenWidth, float realImageHeight, ArrayList<ArrayList<DefineRectangles>> completeList) {
    	
    	int[] bounds = new int[2];
    	
    	// Create the bitmap first and set the bitmap to the canvas
    	bounds = canvasBounds(ScreenWidth, realImageHeight, completeList);
    	Bitmap myBitmap = Bitmap.createBitmap((int)bounds[0], (int)bounds[1], Config.RGB_565);
	    Canvas canvas = new Canvas(myBitmap);
	    
	    // Define the paints and colors
	    Paint backgroundColor = new Paint();
	    backgroundColor.setColor(Color.rgb(50, 50, 50));
		Paint whatColor = new Paint();
		
		
		// Draw the background to cover the size of the Bitmap
		canvas.drawRect((float)0, (float)0, (float)bounds[0], (float)bounds[1], backgroundColor);

		for (int j = 0; j < completeList.size(); j++) {
				// These loops will draw the rectangles provided by the arrayLists
			for (int i = 0; i < completeList.get(j).size(); i++) {
				if (completeList.get(j).get(i).getType() == 0) {
					// NonFloor
					whatColor.setColor(Color.rgb(50, 50, 50));

				} else if (completeList.get(j).get(i).getType() == 1) {
					// Floor
					whatColor.setColor(Color.rgb(224, 224, 0));
				} else if (completeList.get(j).get(i).getType() == 2) {
					// Shelves
					whatColor.setColor(Color.rgb(0, 57, 255));
					
				} else if (completeList.get(j).get(i).getType() == 3) {
					// Background
					whatColor.setColor(Color.rgb(161, 161, 0));
					
				} else {
					// NonFloor
					whatColor.setColor(Color.rgb(50, 50, 50));
				}
				canvas.drawRect(completeList.get(j).get(i).getRect(), whatColor);
			}

		}

		
		// Put the markers on the map
		drawMarkers(canvas, values);
		
		return myBitmap;
    }
	
	public void checkMatrix(MotionEvent event, float ImageWidth, float ScreenWidth, float ImageHeight, float topMarginLine) {
	      
	      // This portion will limit the ability of the image view to navigate in the screen:
	      // i.e., it will only be able to go where we desire and not have the ability
	      // to freely move around the screen
		
		   float xDif = (ImageWidth*scale) - ScreenWidth;
		   float negxDif = xDif*-1;
		   //float yDif = (ImageHeight*scale) - topMarginLine;
		   //float negyDif = yDif*-1;
		   float checkForOpenOrClose = topMarginLine - bottomLine;
		      if (getX(matrix) < negxDif) {
		    	  float dif = Math.abs((getX(matrix) - negxDif));
		    	  float deltay = event.getY() - start.y;
			    	// This is if the image is too far to the left
			    	matrix.postTranslate(dif,
			                  deltay);
		      } 
		      if (getX(matrix) > 0) {
			    	// This is if the image is too far to the right
			    	matrix.postTranslate(getX(matrix)*-1,
			                  event.getY() - start.y);	
			  } 
		      if (getY(matrix) > checkForOpenOrClose) {
		    		  matrix.postTranslate(0, (getY(matrix) - checkForOpenOrClose)*-1);
			  } 
		      if (getY(matrix) < difBetween*-1) {
		    	// This is if the image is too far up
				    matrix.postTranslate(0, Math.abs(getY(matrix) + difBetween));
		      }
	   }
	
	public void checkMatrix(float ImageWidth, float ScreenWidth, float ImageHeight, float topMarginLine) {
	      // This is for the open and close checking
	      savedMatrix.set(matrix);
	      if (getY(matrix) > 0) {
	    		  matrix.postTranslate(0, (getY(matrix))*-1); 	  	  
		  } 
	      map.setImageMatrix(matrix);
	   }

	   /** Determine the space between the first two fingers */
	   private float spacing(MotionEvent event) {
	      float x = event.getX(0) - event.getX(1);
	      float y = event.getY(0) - event.getY(1);
	      return (float) Math.sqrt(x * x + y * y);
	   }

	   /** Calculate the mid point of the first two fingers */
	   private void midPoint(PointF point, MotionEvent event) {
	      float x = event.getX(0) + event.getX(1);
	      float y = event.getY(0) + event.getY(1);
	      point.set(x / 2, y / 2);
	   }
	   
	  public float getX(Matrix matrix) {
		   float[] values = new float[9];
		   matrix.getValues(values);
		   float x = values[2];
		   return x;
	   }
	   
	  public float getY(Matrix matrix) {
		   float[] values = new float[9];
		   matrix.getValues(values);
		   float y = values[5];
		   return y;
	   }
	   
	  public float getWidth(Matrix matrix, ImageView view) {
		   float[] values = new float[9];
		   matrix.getValues(values);
		   float w = values[0] * view.getWidth();
		   return w;
	   }
	   
	  public float getHeight(Matrix matrix, ImageView view) {
		   float[] values = new float[9];
		   matrix.getValues(values);
		   float h = values[2]*view.getHeight();
		   return h;
	   }	  
	  
	  public void drawMarkers(Canvas canvas, ArrayList<ListViewCustom1> values) {
		  Paint markerTop = new Paint();
		  Paint markerBase = new Paint();
		  Paint text = new Paint();
		  markerBase.setColor(Color.rgb(152, 0, 0));
		  markerTop.setColor(Color.rgb(255, 0, 0));
		  text.setColor(Color.WHITE);
		  text.setTextSize(22);
		  markerwidth = 25;
		  markerheight = 25;
		  
		  for (int i = 0; i < values.size(); i++) {
			  //int counter = 0;
			  for (int j = 0; j < values.get(i).getX().length(); j++) {
				  if (values.get(i).getX().charAt(j) == ',') {
					 // counter++;
				  }
			  }
			  String[] xLocations = values.get(i).getX().split(",");
			  String[] yLocations = values.get(i).getY().split(",");
			 for (int k = 0; k < xLocations.length; k++) {			  
			  RectF markerRectTop = new RectF(Float.parseFloat(xLocations[k].trim()), 
					  Float.parseFloat(yLocations[k].trim()), 
					  Float.parseFloat(xLocations[k].trim()) + markerwidth, 
					  Float.parseFloat(yLocations[k].trim()) + markerheight);
			  RectF markerRectBase = new RectF(Float.parseFloat(xLocations[k].trim()) - 3, 
					  Float.parseFloat(yLocations[k].trim()) - 3, 
					  Float.parseFloat(xLocations[k].trim()) + markerwidth + 3, 
					  Float.parseFloat(yLocations[k].trim()) + markerheight + 3);
			  canvas.drawRoundRect(markerRectBase, 3, 3, markerBase);
			  canvas.drawRect(markerRectTop, markerTop);
			  if (i < 10) {
				  canvas.drawText("" + (i + 1),  
						  Float.parseFloat(xLocations[k].trim()) + 7, 
						  Float.parseFloat(yLocations[k].trim()) + 20, text);  
			  } else {
				  canvas.drawText("" + (i + 1), 
						  Float.parseFloat(xLocations[k].trim()) + 1, 
						  Float.parseFloat(yLocations[k].trim()) + 20, text); 
			  }
			  
			 }
		  }
	  }
	  
	  public int[] canvasBounds(int ScreenWidth, float realImageHeight, ArrayList<ArrayList<DefineRectangles>> completeList) {
		  int[] bounds = new int[2];
		  int maxWidth = 0;
		  int maxDepth = 0;
		  for (int i = 0; i < completeList.size(); i++) {
			  for (int j = 0; j < completeList.get(i).size(); j++) {
				  if (completeList.get(i).get(j).getRight() > maxWidth) {
					  maxWidth = completeList.get(i).get(j).getRight();
				  }
				  if (completeList.get(i).get(j).getBottom() > maxDepth) {
					  maxDepth = completeList.get(i).get(j).getBottom();
				  }  
			  }
		  }   
		  if (maxWidth < ScreenWidth) {
			  maxWidth = ScreenWidth;
		  } else {
			  maxWidth += 50;
		  }
		  if (maxDepth < realImageHeight) {
			  maxDepth = (int) realImageHeight + 10;
		  } else {
			  maxDepth += 50;
		  }
		  
		  bounds[0] = maxWidth; bounds[1] = maxDepth;		  	  
		  return bounds;
	  }
	  
	  public void displayPopUp() {
		  // Set min to some arbitrarily large number
		  int min = 10000; 
		  // Set a variable to store which element has the minimum
		  int minElement = -1;
		  for (int i = 0; i < values.size(); i++) {
			  String[] xLocations = values.get(i).getX().split(",");
			  String[] yLocations = values.get(i).getY().split(",");
			  for (int j = 0; j < xLocations.length; j++) {
				 	  
				  int hyp = (int) Math.sqrt(Math.pow(Math.abs((Float.parseFloat(xLocations[j].trim()) + (markerwidth/2))- start.x) - getX(matrix), 2)
						  + Math.pow(Math.abs((Float.parseFloat(yLocations[j].trim()) + (markerheight/2)) - start.y) - getY(matrix), 2));
				  if (hyp < min) {
					  min = hyp;
					  minElement = i;
				  }   
			  }
		  }
		  if (min <= 150) {
			  markerBuilder.setMessage(Html.fromHtml(createMessage(values.get(minElement)))).setTitle(values.get(minElement).getTitle().trim()).setPositiveButton("Ok", new DialogInterface.OnClickListener()  {
					
					@Override
					public void onClick(DialogInterface dialog, int which) {
						// TODO Auto-generated method stub
						
					}
				});
			  
			  alertMarker = markerBuilder.create();
			  alertMarker.show();
		  } 
	  }



	@Override
	public boolean onDown(MotionEvent arg0) {
		// TODO Auto-generated method stub
		return false;
	}



	@TargetApi(Build.VERSION_CODES.JELLY_BEAN)
	@SuppressLint("NewApi")
	@Override
	public boolean onFling(MotionEvent e1, MotionEvent e2, float arg2,
			float arg3) {
		// TODO Auto-generated method stub
		if (e1.getX() < e2.getX() && (Math.abs(e2.getX() - e1.getX())) > 40 && (Math.abs(e2.getY() - e1.getY())) < 60 ) {
    			
    			Bundle bndlanimation =
						ActivityOptions.makeCustomAnimation(getApplicationContext(), R.anim.ani4, R.anim.ani3).toBundle();
    			startActivity(goToSearch, bndlanimation);
 
		}	
		  
		return false;
	}



	@Override
	public void onLongPress(MotionEvent e) {
		int id = itemlist.pointToPosition((int) e.getX(), (int) e. getY());
		ListViewCustom1 itemDetail = (ListViewCustom1) adapter.getItem((id));
		listBuilder.setTitle(itemDetail.getTitle().trim()).setMessage(Html.fromHtml(createMessage(itemDetail))).setPositiveButton("Ok", new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface arg0, int arg1) {
				// TODO Auto-generated method stub			
			}				
		});
		alertList = listBuilder.create();
		listBuilder.show();
		// TODO Auto-generated method stub
		
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
	
	public String createMessage(ListViewCustom1 lvc) {
		String message = "";
		message += "<b>UPC: </b>" + lvc.getUPC() + "<br>";
		message += "<b>Location: </b>" + lvc.getLocation() + "<br>";
		message += "<b>Description: </b>" + lvc.getDesc() + "<br>";		
		return message;
	}
}  

