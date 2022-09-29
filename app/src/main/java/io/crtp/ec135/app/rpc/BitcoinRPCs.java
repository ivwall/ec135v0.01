package io.crtp.ec135.app.rpc;

import java.io.IOException;
import java.util.List;
import java.util.UUID;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.ParseException;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import java.util.Collection;
import java.util.LinkedList;

import io.crtp.ec135.app.utilities.EC135Properties;

public class BitcoinRPCs implements IBitcoinRPC {

	private String nodeIP  = "10.10.89.92";
	private String user    = "Anch0rCh@1n";
	private String pw      = "abc1234";
	private String nodeURL = "http://localhost:8332";

    EC135Properties ec135props = new EC135Properties();

    public BitcoinRPCs() {
    }

	
    private JSONObject invokeRPC2(String id, String method, List<Object> params) {
		DefaultHttpClient httpclient = new DefaultHttpClient();

		JSONObject json = new JSONObject();
		json.put("id", id);
		json.put("method", method);
		if (null != params) {
			json.put("params", params);
		}
		JSONObject responseJsonObj = null;
		try {

			httpclient.getCredentialsProvider().setCredentials(new AuthScope("10.10.89.92", 8332),
					new UsernamePasswordCredentials("Anch0rCh@1n", "abc1234"));

			StringEntity myEntity = new StringEntity(json.toJSONString());

			//HttpPost httppost = new HttpPost(nodeURL);

			HttpPost httppost = new HttpPost("http://10.10.89.92:8332");

			httppost.setEntity(myEntity);

			HttpResponse response = httpclient.execute(httppost);
			HttpEntity entity = response.getEntity();

			if (entity != null) {
				//System.out.println("Response content length: " + entity.getContentLength());
			} else {
				System.out.println("Response content length: " + entity.getContentLength());
			}
			JSONParser parser = new JSONParser();
			responseJsonObj = (JSONObject) parser.parse(EntityUtils.toString(entity));
		} catch (ClientProtocolException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (org.json.simple.parser.ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			// When HttpClient instance is no longer needed,
			// shut down the connection manager to ensure
			// immediate deallocation of all system resources
			httpclient.getConnectionManager().shutdown();
		}
		return responseJsonObj;
	}

    
    public Long getBlockCount(){
        System.out.println("BitcoinRPCs.getBlockcont");
		Long blockcount = null;
		try {
			JSONObject json = invokeRPC2( UUID.randomUUID().toString(),
										"getblockcount",	
										null );

 			System.out.println("getblockcount callResult = " + json.toString() );
			blockcount = (Long)json.get("result");
			System.out.println(" blockcount = "+blockcount.toString() );
		} catch( Exception ex) {
			System.out.println(ex.toString());
		}
		return blockcount;
    }
	

    public String getBlockHash(int blockHeight){
		String result = "not set";
		try {
			Collection<Object> list = new LinkedList<Object>();
			list.add(blockHeight);

			JSONObject json = invokeRPC2( UUID.randomUUID().toString(),
										"getblockhash",	
										(LinkedList)list );

			result = (String)json.get("result");
		} catch( Exception ex) {
			System.out.println(ex.toString());
		}
		return result;
    }


    public void getBlock(){
        System.out.println("BitcoinPRCs.getBlock");
		try {
			Collection<Object> list = new LinkedList<Object>();
			list.add("0000000099c744455f58e6c6e98b671e1bf7f37346bfd4cf5d0274ad8ee660cb");

			JSONObject json = invokeRPC2( UUID.randomUUID().toString(),
										"getblock",	
										(LinkedList)list );

 			System.out.println(" callResult = " + json.toString() );
		} catch( Exception ex) {
			System.out.println(ex.toString());
		}
		System.out.println();
		System.out.println();
    }   
	

    /*
     * getblock
	 * TODO describe verbosity
	 * TDOO or write string typing
     */
    public JSONObject getBlock(String hash, int verbosity) {
		JSONObject result = null;
		try {
			Collection<Object> list = new LinkedList<Object>();
			list.add(hash);
			list.add(verbosity);

			result = invokeRPC2( UUID.randomUUID().toString(),
										"getblock",	
										(LinkedList)list );

		} catch( Exception ex) {
			System.out.println(ex.toString());
		}
		return result;
	}
}