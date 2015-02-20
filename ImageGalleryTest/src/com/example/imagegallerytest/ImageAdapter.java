package com.example.imagegallerytest;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.provider.MediaStore;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

public class ImageAdapter extends PagerAdapter {
	Context context;
    private FlickrImage[] FlickrAdapterImage;
    TextView mTitle;
    
    ImageAdapter(Context context,  FlickrImage[] fImage, TextView tx){
    	this.context=context;
    	FlickrAdapterImage = fImage;
    	mTitle = tx;
    }
    @Override
    public int getCount() {
      return FlickrAdapterImage.length;
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
      return view == ((ImageView) object);
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
      ImageView imageView = new ImageView(context);
      
      mTitle.setText(" Title: " + FlickrAdapterImage[position].getTitle());
      imageView.setImageBitmap(FlickrAdapterImage[position].getBitmap());
      // change if scaling is not proper
      //imageView.setScaleType(ImageView.ScaleType.CENTER_INSIDE);
      imageView.setScaleType(ImageView.ScaleType.FIT_XY);
      
      MediaStore.Images.Media.insertImage(context.getContentResolver(), FlickrAdapterImage[position].getBitmap(),
    		  FlickrAdapterImage[position].getTitle() , "Desc..");
            ((ViewPager) container).addView(imageView, 0);
        return imageView;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
      ((ViewPager) container).removeView((ImageView) object);
    }
    

    
  }
