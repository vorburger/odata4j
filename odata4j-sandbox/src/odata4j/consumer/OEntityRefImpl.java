package odata4j.consumer;

import java.util.ArrayList;
import java.util.List;

import odata4j.consumer.ODataClient.AtomEntry;
import odata4j.consumer.ODataClient.DataServicesAtomEntry;
import odata4j.core.OEntityRef;
import odata4j.internal.EntitySegment;
import odata4j.internal.InternalUtil;
import core4j.Enumerable;

public class OEntityRefImpl<T> implements OEntityRef<T>{

	private final boolean isDelete;
	private final ODataClient client;
	private final String serviceRootUri;
	private final List<EntitySegment> segments = new ArrayList<EntitySegment>();

	
	public OEntityRefImpl(boolean isDelete, ODataClient client, String serviceRootUri,String entitySetName,Object[] key){
		this.isDelete = isDelete;
		this.client = client;
		this.serviceRootUri = serviceRootUri;
		
		segments.add(new EntitySegment(entitySetName,key));
		

	}
	
	@Override
	public OEntityRef<T> nav(String navProperty, Object... key) {
		segments.add(new EntitySegment(navProperty,key));
		return this;
	}
	
	@Override
	public T execute() {
		
	    String path = Enumerable.create(segments).join("/");
		
	    if (isDelete){
	    	ODataClientRequest request = ODataClientRequest.delete(serviceRootUri + path);
	    	boolean rt = client.deleteEntity(request);
	    	return null;
	    	
	    } else {
	    
			ODataClientRequest request = ODataClientRequest.get(serviceRootUri + path);
			
			AtomEntry entry = client.getEntity(request);
			DataServicesAtomEntry dsae = (DataServicesAtomEntry)entry;
			
			return (T) InternalUtil.toEntity(dsae);
	    }
	}
	
	

	
	
}