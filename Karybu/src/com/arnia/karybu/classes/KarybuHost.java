package com.arnia.karybu.classes;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.conn.ClientConnectionManager;
import org.apache.http.cookie.Cookie;
import org.apache.http.entity.StringEntity;
import org.apache.http.entity.mime.HttpMultipartMode;
import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.entity.mime.content.StringBody;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.tsccm.ThreadSafeClientConnManager;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpParams;
import org.apache.http.util.EntityUtils;

import android.util.Log;

public class KarybuHost 
{
	private static KarybuHost INSTANCE = null;
	
	private DefaultHttpClient client;
	
	private String URL;
	
	
	public String getURL() {
		return URL;
	}
	
	public void setURL(String uRL) {
		URL = uRL;
	}
	
	protected KarybuHost()
	{
	}
	
	public String getDomainName(){
		if(URL==null) return null;
		Pattern pattern = Pattern.compile("\\w+://[\\w+.]+(:[0-9]+)*");
		Matcher matcher = pattern.matcher(URL);
		if(matcher.find()){
			return matcher.group();
		}
		return "";
		
	}
	
	public DefaultHttpClient getThreadSafeClient() 
	{
		if( client == null)
		{
		 //define timeout connection 
		 HttpParams httpParams = new BasicHttpParams();
//		 HttpConnectionParams.setConnectionTimeout(httpParams, 3000);
//		 HttpConnectionParams.setSoTimeout(httpParams, 5000);
		 //set param for connection
		 
		 client = new DefaultHttpClient(httpParams);		 
		 ClientConnectionManager mgr = client.getConnectionManager();		 
		 HttpParams params = client.getParams();
		 client = new DefaultHttpClient(new ThreadSafeClientConnManager(params,mgr.getSchemeRegistry()), params);
		 
		}
		 return client;
	}

	
	public static KarybuHost getINSTANCE() 
	{
		if(INSTANCE == null ) INSTANCE = new KarybuHost();
		return INSTANCE;
	}
	
	public List<Cookie> getCookies()
	{
		return getThreadSafeClient().getCookieStore().getCookies();
	}
	
	public String getRequest(String url) 
	{
		String request = KarybuHost.getINSTANCE().getURL() + url;
        HttpGet getRequest = new HttpGet(request);
        

        try {

            HttpResponse getResponse = getThreadSafeClient().execute(getRequest);
            
            final int statusCode = getResponse.getStatusLine().getStatusCode();

            if (statusCode != HttpStatus.SC_OK) {
                return null;
            }

            HttpEntity getResponseEntity = getResponse.getEntity();

            if (getResponseEntity != null) 
            {
                return EntityUtils.toString(getResponseEntity);
            }

        } 
        catch (IOException e) {
            getRequest.abort();
            
            
            Log.w(getClass().getSimpleName(), "Error for URL " + url, e);
        }

        return null;
    }
	
	public String postRequest(String url,String xml)
	{
		String request = KarybuHost.getINSTANCE().getURL() + url;
		
		HttpPost postRequest = new HttpPost(request);
		
		
		try {
			StringEntity se = new StringEntity(xml,"UTF-8");
			se.setContentType("application/xml");
			
			postRequest.setEntity(se);
			
            HttpResponse getResponse = getThreadSafeClient().execute(postRequest);
            
            final int statusCode = getResponse.getStatusLine().getStatusCode();

            if (statusCode != HttpStatus.SC_OK) {
                return null;
            }

            HttpEntity getResponseEntity = getResponse.getEntity();

            if (getResponseEntity != null) {
                return EntityUtils.toString(getResponseEntity);
            }

        } 
        catch (IOException e) {
            postRequest.abort();
            Log.w(getClass().getSimpleName(), "Error for URL " + url, e);
        }
        return null;
	}
	
	public String postMultipart(HashMap<String,?> params, String url)
	{
		String request = KarybuHost.getINSTANCE().getURL() + url;
		
		try { 
	         HttpPost post = new HttpPost(request); 
	     
	     MultipartEntity reqEntity = new MultipartEntity(HttpMultipartMode.BROWSER_COMPATIBLE);  
	     
	     for(Map.Entry<String,?> entity : params.entrySet())
	     {
	    	 if( entity.getValue() instanceof ArrayList ) 
	    	 {
	    		 @SuppressWarnings("unchecked")
				ArrayList<String> arrayList = (ArrayList<String>) entity.getValue();
	    		 for(int i =0;i<arrayList.size();i++) 
	    			 reqEntity.addPart((String) entity.getKey(),new StringBody((String) arrayList.get(i)));
	    	 }
	    	 else
	    	 reqEntity.addPart((String) entity.getKey(),new StringBody((String) entity.getValue() ));
	     }
	     
	     
	     post.setEntity(reqEntity);  
	     HttpResponse response = getThreadSafeClient().execute(post);  
	     HttpEntity resEntity = response.getEntity();  
	     if (resEntity != null)
	     	 {    
	               return EntityUtils.toString(resEntity);
	         }
		} catch (Exception e) 
						  {
								e.printStackTrace();
						  }
	
		return null;
	}
}
