package odata4j.producer;


public class ODataProducer {

	private static ODataProducer INSTANCE;
	public static void setInstance(ODataProducer instance){
		INSTANCE = instance;
	}
	
	public static ODataProducer getInstance(){
		return INSTANCE;
	}
	
	
	private final ODataBackend backend;
	private final String baseUri;
	
	
	public ODataProducer(String baseUri, ODataBackend backend ) {
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
