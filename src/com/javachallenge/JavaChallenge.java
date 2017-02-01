package com.javachallenge;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.security.cert.X509Certificate;
import java.util.Iterator;
import java.util.Map.Entry;
import java.util.Set;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

public class JavaChallenge {

	/**
	 * Retrieve data from the a URL, parse it in JSON format, and return a sum of all recv and sent values
	 * @param args
	 */
	public static void main(String[] args) {
		challenge1();
		challenge2();
		
		System.out.println("All Challenges Complete.");
		System.exit(0);
		return;
	}
	
	public static void challenge1() {
		BufferedReader in = null;
		
		try{
			establishTrust();
			String url ="https://friendpaste.com/71KNTMaFCZ6diD2esfC4Vo/raw?rev=626665323233";
			System.out.println(String.format("Challenge 1 : Requesting data from %s", url));
			
			HttpsURLConnection conn = (HttpsURLConnection)new URL(url).openConnection();
			
			int resp = conn.getResponseCode();
			System.out.println(String.format("Response : %d", resp));
	
			in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
			StringBuffer data = new StringBuffer();
	
			String s;
			while ((s = in.readLine()) != null) {
				data.append(s);
			}
			
			System.out.println(String.format("Parse Response to JSON : %s", data.toString()));
			
			JSONParser jp = new JSONParser();
			JSONObject jo = (JSONObject) jp.parse(data.toString());

			Totals tot = new JavaChallenge().new Totals();
			parseJSON(jo, tot);
			
			System.out.println(String.format("Total Sent : %d", tot.getTotSent()));
			System.out.println(String.format("Total Recv : %d", tot.getTotRecv()));
		}catch(Exception e){
			e.printStackTrace();
		}finally{
			if (in != null){
				try{
					in.close();
				}catch(Exception e){}
			}
		}
		return;
	}
	
	@SuppressWarnings("unchecked")
	public static void challenge2() {
		BufferedReader in = null;
		
		try{
			String url ="https://gist.githubusercontent.com/anonymous/8f60e8f49158efdd2e8fed6fa97373a4/raw/01add7ea44ed12f5d90180dc1367915af331492e/java-data2.json";
			System.out.println(String.format("Challege 2, fun with threads, Requesting data from %s", url));
			
			HttpsURLConnection conn = (HttpsURLConnection)new URL(url).openConnection();
			
			int resp = conn.getResponseCode();
			System.out.println(String.format("Response : %d", resp));
	
			in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
			StringBuffer data = new StringBuffer();
	
			String s;
			while ((s = in.readLine()) != null) {
				data.append(s);
			}
			
			System.out.println(String.format("Parse Response to JSON : %s", data.toString()));
			
			JSONParser jp = new JSONParser();
			JSONObject jo = (JSONObject) jp.parse(data.toString());
			JSONArray items = (JSONArray) jo.get("items");
			
			 Iterator<JSONObject> it = items.iterator();
			 while (it.hasNext()) {
				 JSONObject item = (JSONObject)it.next();
				 long idx = (Long)item.get("index");
				 String uid = (String)item.get("uid");
				 CheckSumThread cst = new CheckSumThread(idx, uid);
				 cst.start();
            }
		}catch(Exception e){
			e.printStackTrace();
		}finally{
			if (in != null){
				try{
					in.close();
				}catch(Exception e){}
			}
		}
		return;
	}
	
	@SuppressWarnings("unchecked")
	private static void parseJSON(JSONObject jo, Totals tot){
		Set<Entry<String,Object>> es = jo.entrySet();
    	Iterator<Entry<String,Object>> it = es.iterator();
    	while (it.hasNext()){
    		Entry<String,Object> ne = it.next();
    		
    		String k = ne.getKey();
    		if (k.equals("sent")){
    			Long v = (Long)ne.getValue();
    			tot.updateSentTotal(v);
    		}else if (k.equals("recv")){
    			Long v = (Long)ne.getValue();
    			tot.updateRecvTotal(v);
    		}else{
    			JSONObject v = (JSONObject)ne.getValue();
    			parseJSON(v, tot);
    		}
    	}
	}

	public class Totals{
		private long totSent;
		private long totRecv;
		
		public Totals() {
			totSent = 0;
			totRecv = 0;
		}
		
		public void updateSentTotal(long v){
			totSent += v;
		}

		public void updateRecvTotal(long v){
			totRecv += v;
		}
		
		public long getTotSent(){
			return totSent;
		}
		
		public long getTotRecv(){
			return totRecv;
		}
	}
	
	private static void establishTrust() throws Exception{
		TrustManager[] trustAllCerts = new TrustManager[] {
				new X509TrustManager() {
					public java.security.cert.X509Certificate[] getAcceptedIssuers() {
						return null;
					}
					public void checkClientTrusted(X509Certificate[] certs, String authType) {  }
					public void checkServerTrusted(X509Certificate[] certs, String authType) {  }
				}
			};

		SSLContext sc = SSLContext.getInstance("SSL");
		sc.init(null, trustAllCerts, new java.security.SecureRandom());
	    HttpsURLConnection.setDefaultSSLSocketFactory(sc.getSocketFactory());

	    // Create all-trusting host name verifier
	    HostnameVerifier allHostsValid = new HostnameVerifier() {
	        public boolean verify(String hostname, SSLSession session) {
	        	return true;
	        }
	    };
		    
	    // Install the all-trusting host verifier
	    HttpsURLConnection.setDefaultHostnameVerifier(allHostsValid);
	}
}