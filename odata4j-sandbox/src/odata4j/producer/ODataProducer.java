package odata4j.producer;

import java.util.List;

import odata4j.core.OProperty;
import odata4j.edm.EdmDataServices;

public interface ODataProducer {

	public abstract EdmDataServices getMetadata();
	
	public abstract EntitiesResponse getEntities(String entitySetName, QueryInfo queryInfo);
	
	public abstract EntityResponse getEntity(String entitySetName, Object entityKey);
	
	public abstract void close();
	
	public abstract EntityResponse createEntity(String entitySetName, List<OProperty<?>> properties);
	
	public abstract void deleteEntity(String entitySetName, Object entityKey);

	public abstract void mergeEntity(String entitySetName, Object entityKey, List<OProperty<?>> properties);
	
	public abstract void updateEntity(String entitySetName, Object entityKey, List<OProperty<?>> properties);
	
}
