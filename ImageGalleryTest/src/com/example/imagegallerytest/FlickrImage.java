package com.example.imagegallerytest;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;


public class FlickrImage {
	String Id;
	String Owner;
	String Secret;
	String Server;
	String Farm;
	String Title;
	
	Bitmap FlickrBitmap;
	
	FlickrImage(String _Id, String _Owner, String _Secret, 
			String _Server, String _Farm, String _Title){
		Id = _Id;
    	Owner = _Owner;
    	Secret = _Secret;
    	Server = _Server;
    	Farm = _Farm;
    	Title = _Title;
    	
    	FlickrBitmap = preloadBitmap();
	}
	
	private Bitmap preloadBitmap(){
		Bitmap bm= null;
    	
    	String FlickrPhotoPath = 
    			"http://farm" + Farm + ".static.flickr.com/" 
    			+ Server + "/" + Id + "_" + Secret + "_m.jpg";
    	
    	URL FlickrPhotoUrl = null;
    	
    	try {
			FlickrPhotoUrl = new URL(FlickrPhotoPath);
			
			HttpURLConnection httpConnection 
				= (HttpURLConnection) FlickrPhotoUrl.openConnection();
			httpConnection.setDoInput(true);
			httpConnection.connect();
			InputStream inputStream = httpConnection.getInputStream();
			bm = BitmapFactory.decodeStream(inputStream);
			
		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    	
    	return bm;
	}
	
	public Bitmap getBitmap(){
		return FlickrBitmap;
	}
	
	public String getTitle(){
		return Title;
	}

}

