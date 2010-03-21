package odata4j.consumer;

import java.util.ArrayList;
import java.util.List;

import javax.ws.rs.core.MediaType;

import odata4j.consumer.ODataClient.DataServicesAtomEntry;
import odata4j.core.OCreate;
import odata4j.core.OEntity;
import odata4j.core.OProperty;
import odata4j.internal.InternalUtil;

public class OCreateImpl<T> implements OCreate<T> {

	private final ODataClient client;
	private final String serviceRootUri;
	private final String entitySetName;
	
	private final List<OProperty<?>> props =new ArrayList<OProperty<?>>();
	
	public OCreateImpl(ODataClient client, String serviceRootUri,String entitySetName){
		this.client = client;
		this.serviceRootUri = serviceRootUri;
		this.entitySetName = entitySetName;
	}
	@Override
	public T execute() {
		
		DataServicesAtomEntry entry = new DataServicesAtomEntry();
		entry.contentType = MediaType.APPLICATION_XML;
		entry.properties =  props;
		ODataClientRequest request = ODataClientRequest.post(serviceRootUri+ entitySetName,entry);
		
		DataServicesAtomEntry dsae = client.createEntity(request);
		OEntity rt = InternalUtil.toEntity(dsae);
		return (T)rt;
	}

	@Override
	public OCreate<T> property(OProperty<?> prop) {
		props.add(prop);
		return this;
	}


}
