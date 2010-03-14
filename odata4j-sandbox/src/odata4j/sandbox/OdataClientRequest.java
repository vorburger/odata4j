package odata4j.sandbox;

import java.util.HashMap;
import java.util.Map;

public class OdataClientRequest {

	private final String url;
	private final Map<String,String> headers;
	
	private OdataClientRequest(String url, Map<String,String> headers){
		this.url = url;
		this.headers = headers==null?new HashMap<String,String>():headers;
	}
	public String getUrl(){
		return url;
	}
	public Map<String,String> getHeaders(){
		return headers;
	}
	public static OdataClientRequest create(String url){
		return new OdataClientRequest(url,null);
	}
	public OdataClientRequest header(String name, String value) {
		headers.put(name, value);
		return new OdataClientRequest(url,headers);
	}
	public OdataClientRequest url(String url) {
		return new OdataClientRequest(url,headers);
	}
	
}
