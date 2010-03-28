package odata4j.producer.resources;

import java.io.StringReader;
import java.util.List;

import odata4j.core.OEntity;
import odata4j.core.OProperty;
import odata4j.internal.InternalUtil;
import odata4j.stax2.XMLEventReader2;
import odata4j.xml.AtomFeedParser;
import odata4j.xml.AtomFeedParser.AtomEntry;
import odata4j.xml.AtomFeedParser.DataServicesAtomEntry;

import com.sun.jersey.api.core.HttpRequestContext;

public abstract class BaseResource {

	protected List<OProperty<?>> getRequestEntityProperties(HttpRequestContext request){
		String requestEntity = request.getEntity(String.class);
		
		XMLEventReader2 reader = InternalUtil.newXMLEventReader(new StringReader(requestEntity));
		AtomEntry entry =  AtomFeedParser.parseFeed(reader).entries.iterator().next();
		DataServicesAtomEntry dsae = (DataServicesAtomEntry)entry;
		OEntity entity = InternalUtil.toEntity(dsae);
		
		final List<OProperty<?>> properties =entity.getProperties();
		return properties;
	}
}
