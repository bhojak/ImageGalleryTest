package com.example.imagegallerytest;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;

import org.apache.http.HttpEntity;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBarActivity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

public class MainActivity extends ActionBarActivity {
	
	ProgressDialog progressDialog;
	BackgroundThread backgroundThread;
	
	FlickrImage[] myFlickrImage;

	String FlickrQuery_url = "https://api.flickr.com/services/rest/?method=flickr.photos.search";
	String FlickrQuery_per_page = "&per_page=10";
	String FlickrQuery_nojsoncallback = "&nojsoncallback=1";
	String FlickrQuery_format = "&format=json";
	String FlickrQuery_tag = "&tags=";
	String FlickrQuery_key = "&api_key=";
	
	String FlickrApiKey = "3468daf83c6538caac67dc9e447b4b02";
	
	final String DEFAULT_SEARCH = "new_york";
	
	EditText searchText;
    Button searchButton;
    public static TextView mTitle ;
    
    Bitmap bmFlickr;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		mTitle = (TextView) findViewById(R.id.title);
		
	    searchText = (EditText)findViewById(R.id.searchtext);
	    searchText.setText(DEFAULT_SEARCH);
	    searchButton = (Button)findViewById(R.id.searchbutton); 
	    searchButton.setOnClickListener(searchButtonOnClickListener);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.action_settings) {
			return true;
		}
		return super.onOptionsItemSelected(item);
	}
	
	  private Button.OnClickListener searchButtonOnClickListener
	  = new Button.OnClickListener(){

			public void onClick(View arg0) {
				progressDialog = ProgressDialog.show(MainActivity.this, "ProgressDialog", "Wait!");
		        backgroundThread = new BackgroundThread();
		        backgroundThread.setRunning(true);
		        backgroundThread.start();
			}};

	  // private methods
	  Handler handler = new Handler(){

			@Override
			public void handleMessage(Message msg) {
				
				progressDialog.dismiss();
			    ViewPager viewPager = (ViewPager) findViewById(R.id.view_pager);
			    ImageAdapter adapter = new ImageAdapter(MainActivity.this, myFlickrImage, mTitle);
			    viewPager.setAdapter(adapter);
			}
	  	
	  }; 
	  
	  private FlickrImage[] ParseJSON(String json){

	  	FlickrImage[] flickrImage = null;
	  	
	  	bmFlickr = null;
	  	String flickrId;
	  	String flickrOwner;
	  	String flickrSecret;
	  	String flickrServer;
	  	String flickrFarm;
	  	String flickrTitle;
	  	
	  	try {
				JSONObject JsonObject = new JSONObject(json);
				JSONObject Json_photos = JsonObject.getJSONObject("photos");
				JSONArray JsonArray_photo = Json_photos.getJSONArray("photo");
				
				flickrImage = new FlickrImage[JsonArray_photo.length()];
				for (int i = 0; i < JsonArray_photo.length(); i++){
					JSONObject FlickrPhoto = JsonArray_photo.getJSONObject(i);
					flickrId = FlickrPhoto.getString("id");
					flickrOwner = FlickrPhoto.getString("owner");
					flickrSecret = FlickrPhoto.getString("secret");
					flickrServer = FlickrPhoto.getString("server");
					flickrFarm = FlickrPhoto.getString("farm");
					flickrTitle = FlickrPhoto.getString("title");
					flickrImage[i] = new FlickrImage(flickrId, flickrOwner, flickrSecret,
							flickrServer, flickrFarm, flickrTitle);
					
				}
				
			} catch (JSONException e) {
				e.printStackTrace();
			}
	  	
	  	return flickrImage;
	  }
	  
	  private String QueryFlickr(String q){
	  	
	  	String qResult = null;
	  	
	  	String qString = 
	  			FlickrQuery_url 
	  			+ FlickrQuery_per_page 
	  			+ FlickrQuery_nojsoncallback 
	  			+ FlickrQuery_format 
	  			+ FlickrQuery_tag + q  
	  			+ FlickrQuery_key + FlickrApiKey;
	  	
	  	HttpClient httpClient = new DefaultHttpClient();
	      HttpGet httpGet = new HttpGet(qString);
	      
	      try {
				HttpEntity httpEntity = httpClient.execute(httpGet).getEntity();
				
				if (httpEntity != null){
					InputStream inputStream = httpEntity.getContent();
					Reader in = new InputStreamReader(inputStream);
					BufferedReader bufferedreader = new BufferedReader(in);
					StringBuilder stringBuilder = new StringBuilder();
					
					String stringReadLine = null;
					
					while ((stringReadLine = bufferedreader.readLine()) != null) {
						stringBuilder.append(stringReadLine + "\n");
					}
					
					qResult = stringBuilder.toString();
					inputStream.close();
				}
				
			} catch (ClientProtocolException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
	      
	      return qResult;
	  }
	  
	  // public class
	  public class BackgroundThread extends Thread {
	  	volatile boolean running = false;
	  	int cnt;
	  	
	  	void setRunning(boolean b){
	  		running = b;	
	  		cnt = 10;
	  	}

			@Override
			public void run() {
				String searchQ = searchText.getText().toString();
				String searchResult = QueryFlickr(searchQ);
				myFlickrImage = ParseJSON(searchResult);
				handler.sendMessage(handler.obtainMessage());
			}
	  }
	  
	    private void sharePhoto (String fileName) {
	    	Intent emailIntent = new Intent(android.content.Intent.ACTION_SEND); 
	    	emailIntent.setType("image/jpeg");
	    	emailIntent.putExtra(android.content.Intent.EXTRA_EMAIL, new String[] {""}); 
	    	emailIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, " PhotoShare test"); 
	    	emailIntent.putExtra(android.content.Intent.EXTRA_TEXT, "Photo shate test");
	    	emailIntent.putExtra(Intent.EXTRA_STREAM, Uri.parse("file://"+fileName));
	    	startActivity(Intent.createChooser(emailIntent, "Sharing Options"));
	    }
	  
}
