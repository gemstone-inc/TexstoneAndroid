package com.jpn.takatsuka.texstone.data;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.Map;

import android.content.Context;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.StateListDrawable;
import android.util.Log;
import android.util.StateSet;

import com.jpn.takatsuka.texstone.Config;

public class MediaManager {
	
	private Map<String, Bitmap> categoryImageMap =  new HashMap<String, Bitmap>();
	
	
	private Map<Category, StateListDrawable> categoryStateListDrawableMap =  new HashMap<Category, StateListDrawable>();
	

	private static MediaManager sharedInstance;
	
	static final String TAG = Config.makeLogTag(MediaManager.class);
	
	public static MediaManager getInstance(Context context){
		if(sharedInstance == null){
			sharedInstance = new MediaManager(context);
		}
		return sharedInstance;
	}
	
	private Context context;
	

	public synchronized void CopyAssets() {
	    AssetManager assetManager = context.getAssets();
	    String[] files = null;
	    try {
	        files = assetManager.list("");
	    } catch (IOException e) {
	        Log.e("tag", e.getMessage());
	    }
	    for(String fileName : files) {
	        InputStream in = null;
	        OutputStream out = null;
	        try {
	          in = assetManager.open(fileName);
	          String dirPath = context.getDir("category_image", Context.MODE_PRIVATE).getAbsolutePath();
			  String filePath = dirPath+"/"+fileName;
	          out = new FileOutputStream(filePath);
	          copyFile(in, out);
	          in.close();
	          in = null;
	          out.flush();
	          out.close();
	          out = null;
	        } catch(Exception e) {
	            Log.e("tag", e.getMessage());
	        }       
	    }
	}
	private void copyFile(InputStream in, OutputStream out) throws IOException {
	    byte[] buffer = new byte[1024];
	    int read;
	    while((read = in.read(buffer)) != -1){
	      out.write(buffer, 0, read);
	    }
	}
	

	
	public synchronized Bitmap getCategoryImage(String fileName){
		Bitmap bmp = categoryImageMap.get(fileName);
		if(bmp == null){
			String dirPath = context.getDir("category_image", Context.MODE_PRIVATE).getAbsolutePath();
			String filePath = dirPath+"/"+fileName;
			bmp = BitmapFactory.decodeFile(filePath);
			categoryImageMap.put(fileName, bmp);
		}
		return bmp;
	}

	
	public synchronized StateListDrawable getStateListDrawable(Category ctg){
		StateListDrawable sld = categoryStateListDrawableMap.get(ctg);
		if(sld == null){
			sld = new StateListDrawable();
			
			BitmapDrawable bmp = new  BitmapDrawable(context.getResources(), getCategoryImage(ctg.getDeviceImageFilename()));
			BitmapDrawable bmpOver = new  BitmapDrawable(context.getResources(), getCategoryImage(ctg.getDeviceImageOverFilename()));

			
			sld.addState(new int[]{android.R.attr.state_pressed}, bmpOver);
			sld.addState(StateSet.WILD_CARD, bmp);

			categoryStateListDrawableMap.put(ctg, sld);
		}
		return sld;
	}
 
	
	
	public synchronized String getCategoryImageDirectory() {
		return context.getDir("category_image", Context.MODE_PRIVATE).getAbsolutePath();
	}
	

	//FF5555
	//投稿



	
	private MediaManager(Context context){
		this.context = context;
	}
}
