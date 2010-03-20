package odata4j.consumer;

import java.util.HashMap;
import java.util.Map;

public class ODataClientRequest {

	private final String url;
	private final Map<String,String> headers;
	private final Map<String,String> queryParams;
	
	private ODataClientRequest(String url, Map<String,String> headers, Map<String,String> queryParams){
		this.url = url;
		this.headers = headers==null?new HashMap<String,String>():headers;
		this.queryParams = queryParams==null?new HashMap<String,String>():queryParams;
	}
	public String getUrl(){
		return url;
	}
	public Map<String,String> getHeaders(){
		return headers;
	}
	public Map<String,String> getQueryParams(){
		return queryParams;
	}
	public static ODataClientRequest create(String url){
		return new ODataClientRequest(url,null,null);
	}
	public ODataClientRequest header(String name, String value) {
		headers.put(name, value);
		return new ODataClientRequest(url,headers,queryParams);
	}
	public ODataClientRequest queryParam(String name, String value) {
		queryParams.put(name, value);
		return new ODataClientRequest(url,headers,queryParams);
	}
	public ODataClientRequest url(String url) {
		return new ODataClientRequest(url,headers,queryParams);
	}
	
}
