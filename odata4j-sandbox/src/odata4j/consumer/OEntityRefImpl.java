package odata4j.consumer;

import java.util.ArrayList;
import java.util.List;

import odata4j.consumer.ODataClient.AtomEntry;
import odata4j.consumer.ODataClient.DataServicesAtomEntry;
import odata4j.core.OEntity;
import odata4j.core.OEntityRef;
import odata4j.internal.EntitySegment;
import odata4j.internal.InternalUtil;
import core4j.Enumerable;

public class OEntityRefImpl implements OEntityRef<OEntity>{

	private final ODataClient client;
	private final String serviceRootUri;
	private final List<EntitySegment> segments = new ArrayList<EntitySegment>();

	
	public OEntityRefImpl(ODataClient client, String serviceRootUri,String entitySetName,Object[] key){
		this.client = client;
		this.serviceRootUri = serviceRootUri;
		
		segments.add(new EntitySegment(entitySetName,key));
		

	}
	
	@Override
	public OEntityRef<OEntity> nav(String navProperty, Object... key) {
		segments.add(new EntitySegment(navProperty,key));
		return this;
	}
	
	@Override
	public OEntity execute() {
		
	    String path = Enumerable.create(segments).join("/");
		
		ODataClientRequest request = ODataClientRequest.get(serviceRootUri + path);
		
		AtomEntry entry = client.getEntity(request);
		DataServicesAtomEntry dsae = (DataServicesAtomEntry)entry;
		
		return InternalUtil.toEntity(dsae);
	}
	
	

	
	
}