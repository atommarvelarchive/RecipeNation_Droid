package com.atommarvel.RecipeNation;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;

import android.R;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.EditText;
import android.widget.Toast;



public class WebCalls {
	public static final String DEBUG_TAG = "Interwebs Response";
	final String baseURL = "http://balanced-meal.herokuapp.com/";
	Intent intent;
	Context context;
	
	WebCalls(Intent intent, Context context){
		this.context = context;
		this.intent = intent;
	}
	
	void getCommand(String command){	  
		ConnectivityManager connMgr = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
		if (networkInfo !=null && networkInfo.isConnected()){
			// build your URL
			String finalURL = baseURL.concat(command);			
			// fetch yo data
			 new DownloadWebpageTask().execute(finalURL);
		} else {
			// toast the user that they got no data	
			Toast toast = Toast.makeText(context, "@string/no_connection", Toast.LENGTH_SHORT);
			toast.show();
		}
		
	}
	
	private class DownloadWebpageTask extends AsyncTask<String, Void, String> {
		@Override
		protected String doInBackground(String...urls){
			try{
				return downloadUrl(urls[0]);			
			} catch (IOException e) {
				return "Unable to retrieve web page. URL may be invalid.";
			}
		}
		
		 @Override
	        protected void onPostExecute(String result) {
	            // forward to next activity				
				intent.putExtra("@string/xml_string", result);
				context.startActivity(intent);
		 }
		
		private String downloadUrl(String myurl) throws IOException {
		    InputStream is = null;
		    // Only display the first 500 characters of the retrieved
		    // web page content.
		        
		    try {
		        URL url = new URL(myurl);
		        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
		        conn.setReadTimeout(10000 /* milliseconds */);
		        conn.setConnectTimeout(15000 /* milliseconds */);
		        conn.setRequestMethod("GET");
		        conn.setDoInput(true);
		        // Starts the query
		        conn.connect();
		        int response = conn.getResponseCode();
		        Log.d(DEBUG_TAG, "The response is: " + response);
		        is = conn.getInputStream();

		        // Convert the InputStream into a string
		        String contentAsString = readIt(is);
		        return contentAsString;
		        
		    // Makes sure that the InputStream is closed after the app is
		    // finished using it.
		    } finally {
		        if (is != null) {
		            is.close();
		        } 
		    }
		}
		
		public String readIt(InputStream stream) throws IOException, UnsupportedEncodingException {
		    Reader reader = null;
		    reader = new InputStreamReader(stream, "UTF-8");        
		    char[] buffer = new char[(int) 1e5];
		    reader.read(buffer);
		    return new String(buffer);
		}	
		
	}

}

