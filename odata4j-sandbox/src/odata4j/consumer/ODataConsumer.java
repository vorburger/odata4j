package odata4j.consumer;

import odata4j.consumer.ODataClient.CollectionInfo;
import odata4j.core.OClientBehavior;
import odata4j.core.OCreate;
import odata4j.core.OEntity;
import odata4j.core.OEntityRef;
import odata4j.core.OModify;
import odata4j.core.OQuery;
import core4j.Enumerable;
import core4j.Func1;

public class ODataConsumer {

	private final String serviceRootUri;
	private final ODataClient client;
	
	private ODataConsumer(String serviceRootUri, OClientBehavior... behaviors){
		this.serviceRootUri = serviceRootUri;
		this.client = new ODataClient(behaviors);
	}
	public String getServiceRootUri(){
		return serviceRootUri;
	}
	
	public static ODataConsumer create(String serviceRootUri){
		return new ODataConsumer(serviceRootUri);
	}
	public static ODataConsumer create(String serviceRootUri,OClientBehavior... behaviors){
		return new ODataConsumer(serviceRootUri,behaviors);
	}
	
	
	
	public Enumerable<String> getEntitySets() {
		
		ODataClientRequest request = ODataClientRequest.get(serviceRootUri);
		return Enumerable.create(client.getCollections(request)).select(new Func1<CollectionInfo,String>(){
			public String apply(CollectionInfo input) {
				return input.title;
			}});
	}
	
	public OQuery<OEntity> getEntities(String entitySetName){
		return new OQueryImpl<OEntity>(client,OEntity.class, serviceRootUri, entitySetName);
	}


	public OEntityRef<OEntity> getEntity(String entitySetName, Object... key) {
		return new OEntityRefImpl<OEntity>(false,client,serviceRootUri,entitySetName,key);
	}


	
	public OCreate<OEntity> createEntity(String entitySetName) {
		return new OCreateImpl<OEntity>(client,serviceRootUri,entitySetName);
	}
	public OModify<OEntity> updateEntity(OEntity entity, String entitySetName, Object... key) {
		return new OModifyImpl<OEntity>(entity, client,serviceRootUri,entitySetName, key);
	}
	public OModify<OEntity> mergeEntity(String entitySetName, Object... key) {
		return new OModifyImpl<OEntity>(null,client,serviceRootUri,entitySetName, key);
	}
	public OEntityRef<Void> deleteEntity(String entitySetName, Object... key) {
		return new OEntityRefImpl<Void>(true,client,serviceRootUri,entitySetName,key);
	}
	
	
	
	
}
