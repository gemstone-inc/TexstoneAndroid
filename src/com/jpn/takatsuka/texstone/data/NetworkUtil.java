package com.jpn.takatsuka.texstone.data;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;

import com.jpn.takatsuka.texstone.Config;

public class NetworkUtil {
	
	static final String TAG = Config.makeLogTag(NetworkUtil.class);
	
	public static boolean isOnline(Context context) {
		ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
	    NetworkInfo netInfo = cm.getActiveNetworkInfo();
	    if (netInfo != null && netInfo.isConnectedOrConnecting()) {
	        return true;
	    }
	    return false;
	 }
	
//    private static Calendar LAST_IO_EXCEPTION_TIME_FOR_APP_SERVER;
//    
//    public static void setAppServerConnectionFailure(){
//    	LAST_IO_EXCEPTION_TIME_FOR_APP_SERVER = Calendar.getInstance(Config.TIME_ZONE);
//    }
//    
//    public static void setAppServerConnectionSuccess(){
//    	LAST_IO_EXCEPTION_TIME_FOR_APP_SERVER = null;
//    }
    
    public static  boolean isDownloadTryOK(Context context){
    	if(!isOnline(context)){
    		return false;
    	}
    	
//		if(LAST_IO_EXCEPTION_TIME_FOR_APP_SERVER == null){
//			return true;
//		}
//		
//		long nowMs = Calendar.getInstance(Config.TIME_ZONE).getTimeInMillis();
//		long lastCheckMs = LAST_IO_EXCEPTION_TIME_FOR_APP_SERVER.getTimeInMillis();
//		if((nowMs - lastCheckMs) > context.getResources().getInteger(R.integer.server_down_check_interval_ms)){
//			return true;
//		}
		
		Log.e(TAG, "isDownloadTryOK returning false");
		
		
		return true;
	}
	
}
