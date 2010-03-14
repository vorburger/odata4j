package odata4j.service;

import odata4j.backend.ODataBackend;

public class ODataService {

	private static ODataService INSTANCE;
	public static void setInstance(ODataService instance){
		INSTANCE = instance;
	}
	
	public static ODataService getInstance(){
		return INSTANCE;
	}
	
	
	private final ODataBackend backend;
	private final String baseUri;
	
	
	public ODataService(String baseUri, ODataBackend backend ) {
		this.baseUri = baseUri;
		this.backend = backend;
	}
	
	public ODataBackend getBackend(){
		return backend;
	}
	
	public String getBaseUri() {
		return baseUri;
	}
	
}
