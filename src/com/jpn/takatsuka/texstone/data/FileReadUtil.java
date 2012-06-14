package com.jpn.takatsuka.texstone.data;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import android.content.Context;

import com.jpn.takatsuka.texstone.Config;

public class FileReadUtil {
	
	static final String TAG = Config.makeLogTag(FileReadUtil.class);
	
    public static String readRawTxt(Context context, int resourceId) throws IOException{

        InputStream inputStream = context.getResources().openRawResource(resourceId);
        BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
        StringBuffer contents = new StringBuffer();
        String text = null;
        // repeat until all lines is read
        while ((text = reader.readLine()) != null) {
            contents.append(text).append(System.getProperty("line.separator"));
        }
        
        return contents.toString();
      }
}
