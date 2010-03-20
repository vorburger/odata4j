package odata4j.consumer;

import java.util.HashMap;
import java.util.Map;

public class ODataClientRequest {

	private final String url;
	private final Map<String,String> headers;
	
	private ODataClientRequest(String url, Map<String,String> headers){
		this.url = url;
		this.headers = headers==null?new HashMap<String,String>():headers;
	}
	public String getUrl(){
		return url;
	}
	public Map<String,String> getHeaders(){
		return headers;
	}
	public static ODataClientRequest create(String url){
		return new ODataClientRequest(url,null);
	}
	public ODataClientRequest header(String name, String value) {
		headers.put(name, value);
		return new ODataClientRequest(url,headers);
	}
	public ODataClientRequest url(String url) {
		return new ODataClientRequest(url,headers);
	}
	
}
