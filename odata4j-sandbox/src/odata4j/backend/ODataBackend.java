package odata4j.backend;

import odata4j.edm.EdmDataServices;

public interface ODataBackend {

	public abstract EdmDataServices getMetadata();
	
	public abstract EntityResponse getResponse(EntityRequest request);
	
}
