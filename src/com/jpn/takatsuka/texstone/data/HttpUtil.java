package com.jpn.takatsuka.texstone.data;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLConnection;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

import android.os.AsyncTask;

public class HttpUtil {

    public static String executeHttpGet(String url, AsyncTask task) throws IOException, URISyntaxException {
    	BufferedReader in = null;
    	try {
            HttpClient client = new DefaultHttpClient();
            HttpGet request = new HttpGet();
            request.setURI(new URI(url));
            HttpResponse response = client.execute(request);
            in = new BufferedReader(new InputStreamReader(response.getEntity().getContent()));
            StringBuffer sb = new StringBuffer("");
            String line = "";
            String NL = System.getProperty("line.separator");
            while ((line = in.readLine()) != null) {
            	if(task != null && task.isCancelled()){
					return null;
				}
                sb.append(line + NL);
            }
            in.close();
            in = null;
             return sb.toString();
        }
    	finally {
        	if (in != null) {
        		try{in.close();}catch(Exception e){}
        	}
        }
    }
    
    
    public static long downloadFile(String urlStr, FileOutputStream output, AsyncTask task) throws IOException{
		URL url = new URL(urlStr);
		InputStream input = null;
		try{
			URLConnection conexion = url.openConnection();
            conexion.connect();
            input = new BufferedInputStream(url.openStream());
            byte data[] = new byte[1024];

            long total = 0;
            int count = 0;

            while ((count = input.read(data)) != -1) {
            	if(task.isCancelled()){
					return -1;
				}
                total += count;
                output.write(data, 0, count);
            }
            
            output.flush();
            output.close();
            output = null;
            input.close();
            input = null;
            return total;
		}
		finally{
			if(output != null){
				try{output.close();}catch(Exception e){}					
			}

			if(input != null){
				try{input.close();}catch(Exception e){}					
			}
		}
	}
    
    
}
