package odata4j.producer;

import odata4j.edm.EdmDataServices;

public interface ODataProducer {

	public abstract EdmDataServices getMetadata();
	
	public abstract EntitiesResponse getEntities(EntitiesRequest request);
	
	public abstract EntityResponse getEntity(EntityRequest request);
	
	public abstract void close();
	
}
