package odata4j.consumer;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import odata4j.consumer.ODataClient.AtomEntry;
import odata4j.consumer.ODataClient.DataServicesAtomEntry;
import odata4j.core.OEntity;
import odata4j.core.OEntityRef;
import odata4j.core.OQuery;
import odata4j.internal.InternalUtil;
import core4j.Enumerable;
import core4j.Func1;

public class ODataConsumer {

	private final String serviceRootUri;
	private final ODataClient client;
	
	public ODataConsumer(String serviceRootUri){
		this.serviceRootUri = serviceRootUri;
		this.client = new ODataClient();
	}
	
	
	public OQuery<OEntity> getEntities(String entitySetName){
		
		return new OQueryImpl<OEntity>(client,OEntity.class, serviceRootUri, entitySetName);

	}


	public OEntityRef<OEntity> getEntity(String entitySetName, Object... key) {
		return new OEntityRefImpl(client,serviceRootUri,entitySetName,key);
		
	}
	
	private static class OEntityRefImpl implements OEntityRef<OEntity>{

		private static class EntitySegment{
			public final String segment;
			public final Object[] key;
			public EntitySegment(String segment, Object[] key){
				this.segment = segment;
				this.key = key;
			}
			
			@Override
			public String toString(){
				String keyValue = Enumerable.create(this.key).select(new Func1<Object,String>(){
					public String apply(Object input) {
						String rt;
						if (input instanceof UUID){
							return "guid'" + input + "'";
						}
						else if (input instanceof String){
							return "'" + ((String)input).replace("'","''") + "'";

						}
						else{
							return input.toString();
						}
					}}).join(",");
				
				return this.segment + "(" + keyValue +  ")";
			}
		}
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
		public OEntity get() {
			
		    String path = Enumerable.create(segments).join("/");
			
			ODataClientRequest request = ODataClientRequest.create(serviceRootUri + path);
			
			AtomEntry entry = client.getEntity(request);
			DataServicesAtomEntry dsae = (DataServicesAtomEntry)entry;
			
			return InternalUtil.toEntity(dsae);
		}
		
		

		
		
	}
	
	
	
}
