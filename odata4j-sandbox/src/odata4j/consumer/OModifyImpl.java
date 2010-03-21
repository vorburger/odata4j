package odata4j.consumer;

import java.util.ArrayList;
import java.util.List;

import javax.ws.rs.core.MediaType;

import odata4j.consumer.ODataClient.DataServicesAtomEntry;
import odata4j.core.OEntity;
import odata4j.core.OModify;
import odata4j.core.OProperty;
import odata4j.internal.EntitySegment;
import core4j.Enumerable;
import core4j.Predicate1;

public class OModifyImpl<T> implements OModify<T> {

	private final T updateRoot;
	private final ODataClient client;
	private final String serviceRootUri;
	private final List<EntitySegment> segments = new ArrayList<EntitySegment>();
	
	private final List<OProperty<?>> props =new ArrayList<OProperty<?>>();
	
	public OModifyImpl(T updateRoot, ODataClient client, String serviceRootUri,String entitySetName, Object[] key){
		this.updateRoot = updateRoot;
		this.client = client;
		this.serviceRootUri = serviceRootUri;
		
		segments.add(new EntitySegment(entitySetName,key));
	}
	
	@Override
	public OModify<T> nav(String navProperty, Object... key) {
		segments.add(new EntitySegment(navProperty,key));
		return this;
	}
	
	@Override
	public boolean execute() {
		
		List<OProperty<?>> requestProps = props;
		if (updateRoot!=null){
			OEntity updateRootEntity = (OEntity)updateRoot;
			requestProps = Enumerable.create(updateRootEntity.getProperties()).toList();
			for(final OProperty<?> prop : props){
				OProperty<?> requestProp = Enumerable.create(requestProps).firstOrNull(new Predicate1<OProperty<?>>(){
					public boolean apply(OProperty<?> input) {
						return input.getName().equals(prop.getName());
					}});
				requestProps.remove(requestProp);
				requestProps.add(prop);
			}
		}
		
		
		DataServicesAtomEntry entry = new DataServicesAtomEntry();
		entry.contentType = MediaType.APPLICATION_XML;
		entry.properties =  requestProps;
		
		String path = Enumerable.create(segments).join("/");
			
		ODataClientRequest request = updateRoot!=null?ODataClientRequest.put(serviceRootUri + path,entry):ODataClientRequest.merge(serviceRootUri + path,entry);
		
		boolean rt = client.updateEntity(request);
		return rt;
	}
	


	@Override
	public OModify<T> property(OProperty<?> prop) {
		props.add(prop);
		return this;
	}


}
