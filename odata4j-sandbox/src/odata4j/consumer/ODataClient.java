package odata4j.consumer;

import java.io.StringReader;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.ws.rs.core.MediaType;
import javax.xml.namespace.QName;
import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLEventWriter;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.events.Attribute;
import javax.xml.stream.events.StartElement;
import javax.xml.stream.events.XMLEvent;

import odata4j.edm.EdmType;

import org.apache.commons.lang.builder.ReflectionToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;

import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.WebResource;

import core4j.Enumerable;

public class ODataClient {

	public static class CollectionInfo {
		public String url;
		public String title;
		public String accept;

		@Override
		public String toString() {
			return ReflectionToStringBuilder.toString(this,ToStringStyle.SHORT_PREFIX_STYLE);
		}
	}
	
	
	public static class AtomFeed {
		public String next;
		public Iterable<AtomEntry>  entries;
	}
	
	public abstract static class AtomEntry {
		public String id;
		public String title;
		public String updated;
		public String categoryTerm;
		public String categoryScheme;
		public String contentType;
		
	}
	
	public static class BasicAtomEntry extends AtomEntry {
		public String content;
		
		@Override
		public String toString() {
			return ReflectionToStringBuilder.toString(this,ToStringStyle.SHORT_PREFIX_STYLE);
		}
	}
	
	public static class DataServicesAtomEntry extends AtomEntry {
		public String etag;
		public List<EntityProperty> properties;
		
		@Override
		public String toString() {
			return ReflectionToStringBuilder.toString(this,ToStringStyle.SHORT_PREFIX_STYLE);
		}
	}
	
	
	public abstract static class EntityProperty {
		public String type;
		public String name;
		
		
		@Override
		public String toString() {
			return ReflectionToStringBuilder.toString(this,ToStringStyle.SHORT_PREFIX_STYLE);
		}
	}
	
	public static class StringEntityProperty extends EntityProperty {
		public String value;
	}
	public static class ComplexEntityProperty extends EntityProperty {
		public Iterable<EntityProperty> properties;
	}
	

	private static final String NS_APP = "http://www.w3.org/2007/app";
	private static final String NS_XML = "http://www.w3.org/XML/1998/namespace";
	private static final String NS_ATOM = "http://www.w3.org/2005/Atom";
	
	public static final String NS_METADATA = "http://schemas.microsoft.com/ado/2007/08/dataservices/metadata";
	public static final String NS_DATASERVICES = "http://schemas.microsoft.com/ado/2007/08/dataservices";
	
	public static final QName ATOM_ENTRY = new QName(NS_ATOM,"entry");
	public static final QName ATOM_ID = new QName(NS_ATOM,"id");
	public static final QName ATOM_TITLE = new QName(NS_ATOM,"title");
	public static final QName ATOM_UPDATED = new QName(NS_ATOM,"updated");
	public static final QName ATOM_CATEGORY = new QName(NS_ATOM,"category");
	public static final QName ATOM_CONTENT = new QName(NS_ATOM,"content");
	public static final QName ATOM_LINK = new QName(NS_ATOM,"link");
	
	public static final QName APP_WORKSPACE = new QName(NS_APP,"workspace");
	public static final QName APP_SERVICE = new QName(NS_APP,"service");
	public static final QName APP_COLLECTION = new QName(NS_APP,"collection");
	public static final QName APP_ACCEPT = new QName(NS_APP,"accept");
	
	public static final QName M_ETAG = new QName(NS_METADATA,"etag");
	public static final QName M_PROPERTIES = new QName(NS_METADATA,"properties");
	public static final QName M_TYPE = new QName(NS_METADATA,"type");
	public static final QName M_NULL = new QName(NS_METADATA,"null");
	
	public static final QName XML_BASE = new QName(NS_XML,"base");
	

	private final Map<String,String> headers;
	
	public static boolean DUMP_REQUEST_HEADERS;
	public static boolean DUMP_RESPONSE_HEADERS;
	public static boolean DUMP_RESPONSE_BODY;
	
	public ODataClient() {
		this(null);
	}
	

	public ODataClient(Map<String,String> headers) {
		this.headers = headers==null?new HashMap<String,String>():Collections.unmodifiableMap(headers);

	}
	
	
	
	public Iterable<CollectionInfo> getCollections(ODataClientRequest request) {

		try {
			XMLEventReader reader = doXmlRequest(request);
			return parseCollections(reader);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	
	
	public AtomEntry getEntity(ODataClientRequest request){
		try {
		
			XMLEventReader reader = doXmlRequest(request);
			return parseFeed(reader).entries.iterator().next();
		} catch (Exception e) {
			
			throw new RuntimeException(e);
		}
		
	}
	
	
	
	public AtomFeed getEntities(ODataClientRequest request){
		
		try {
			XMLEventReader reader = doXmlRequest(request);
			return parseFeed(reader);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	
	}
	
	
	private XMLEventReader doXmlRequest(ODataClientRequest request) throws Exception {
		Client client = Client.create();
		WebResource webResource = client.resource(request.getUrl());
		for(String qpn : request.getQueryParams().keySet()){
			webResource = webResource.queryParam(qpn, request.getQueryParams().get(qpn));
		}
		
		WebResource.Builder b = webResource.getRequestBuilder();
		
		
		b = b.accept(MediaType.APPLICATION_XML, MediaType.APPLICATION_ATOM_XML);
		
		for(String header : headers.keySet()){
			b.header(header, headers.get(header));
		}
		for(String header : request.getHeaders().keySet()){
			b.header(header,request.getHeaders().get(header));
		}

		if(DUMP_REQUEST_HEADERS)
			log("GET " + webResource.toString());
		
		ClientResponse response = b.get(ClientResponse.class);
	
		
		if (DUMP_RESPONSE_HEADERS)
			dumpHeaders(response);
		int status = response.getStatus();
		String textEntity = response.getEntity(String.class);
		if (DUMP_RESPONSE_BODY)
			log(textEntity);
		
		XMLInputFactory f = XMLInputFactory.newInstance();
		
		XMLEventReader reader = f.createXMLEventReader(new StringReader(textEntity));
		return reader;
	}
	
	
	private AtomFeed parseFeed(XMLEventReader reader) throws Exception{
		
		AtomFeed feed = new AtomFeed();
		List<AtomEntry> rt = new ArrayList<AtomEntry>();
		
		while (reader.hasNext()) {
			XMLEvent event = reader.nextEvent();
			
			if (isStartElement(event, ATOM_ENTRY)){
				
				rt.add(parseEntry(reader,event.asStartElement()));
			}
			else if (isStartElement(event, ATOM_LINK)){
				if ("next".equals(event.asStartElement().getAttributeByName(new QName("rel")).getValue())){
					feed.next = event.asStartElement().getAttributeByName(new QName("href")).getValue();
				}
			}
			
		}
		feed.entries= rt;
		
		return feed;
		
	}
	
	private String getAttributeValueIfExists(StartElement element, String localName){
		return getAttributeValueIfExists(element,new QName(null,localName));
	}
	private String getAttributeValueIfExists(StartElement element, QName attName){
		Attribute rt = element.getAttributeByName(attName);
		return rt==null?null:rt.getValue();
	}
	private AtomEntry parseEntry(XMLEventReader reader, StartElement entryElement) throws Exception {
		
		String id = null;
		String categoryTerm = null;
		String categoryScheme = null;
		String title = null;
		String updated = null;
		String contentType = null;
		
		String etag = getAttributeValueIfExists(entryElement,M_ETAG);
		
		AtomEntry rt = null;
		
		while (reader.hasNext()) {
			XMLEvent event = reader.nextEvent();
			
			if (event.isEndElement() && event.asEndElement().getName().equals(entryElement.getName())){
				rt.id = id;
				rt.title = title;
				rt.updated = updated;
				rt.categoryScheme = categoryScheme;
				rt.categoryTerm = categoryTerm;
				rt.contentType = contentType;
				return rt;
			}
			
			if (isStartElement(event, ATOM_ID)){
				id = reader.getElementText();
			}
			else if (isStartElement(event, ATOM_TITLE)){
				title = reader.getElementText();
			}
			else if (isStartElement(event, ATOM_UPDATED)){
				updated = reader.getElementText();
			}
			else if (isStartElement(event, ATOM_CATEGORY)){
				categoryTerm = getAttributeValueIfExists(event.asStartElement(), "term");
				categoryScheme = getAttributeValueIfExists(event.asStartElement(), "scheme");
				
			}
			else if (isStartElement(event, M_PROPERTIES)){
				rt = parseDSAtomEntry(etag,reader,event);
			}
			else if (isStartElement(event, ATOM_CONTENT)){
				contentType = getAttributeValueIfExists(event.asStartElement(), "type");
			
				if ( contentType.equals(MediaType.APPLICATION_XML)){
					
					StartElement contentElement = event.asStartElement();
					StartElement valueElement = null;
					while (reader.hasNext()) {
						
						XMLEvent event2 = reader.nextEvent();
						
						if (valueElement==null && event2.isStartElement()){
							valueElement = event2.asStartElement();
							
							if (isStartElement(event2, M_PROPERTIES)){
								rt = parseDSAtomEntry(etag,reader,event2);
							} else {
								BasicAtomEntry bae = new BasicAtomEntry();
								bae.content = innerText(reader,event2.asStartElement());
								rt = bae;
							}
							
							
							
						}
						if (event2.isEndElement() && event2.asEndElement().getName().equals(contentElement.getName())){
							
							break;
						}
						
					}
					
				}
				else {
					BasicAtomEntry bae = new BasicAtomEntry();
					bae.content = innerText(reader,event.asStartElement());
					rt = bae;
				}
				
			}
			
	
			
		}
		
		throw new RuntimeException();
	}
	
	private DataServicesAtomEntry parseDSAtomEntry(String etag, XMLEventReader reader, XMLEvent event2) throws Exception {
		DataServicesAtomEntry dsae = new DataServicesAtomEntry();
		dsae.etag = etag;
		List<EntityProperty> properties = new ArrayList<EntityProperty>();
		for(EntityProperty ep : parseProperties(reader,event2.asStartElement())){
			properties.add(ep);
		}
		dsae.properties= properties;
		return dsae;
	}
	
	private String innerText(XMLEventReader reader, StartElement element) throws Exception {
		StringWriter sw = new StringWriter();
		XMLEventWriter writer = XMLOutputFactory.newInstance().createXMLEventWriter(sw);
		while (reader.hasNext()) {
			
			XMLEvent event = reader.nextEvent();
			if (event.isEndElement() && event.asEndElement().getName().equals(element.getName())){
				
				return sw.toString();
			} else {
				writer.add(event);
			}
			
		}
		throw new RuntimeException();
	}
	
	
	
	private Iterable<EntityProperty> parseProperties(XMLEventReader reader, StartElement propertiesElement) throws Exception {
		List<EntityProperty> rt = new ArrayList<EntityProperty>();
		
		while (reader.hasNext()) {
			XMLEvent event = reader.nextEvent();
			
			if (event.isEndElement() && event.asEndElement().getName().equals(propertiesElement.getName())){
				return rt;
			}
			
			if(event.isStartElement() && event.asStartElement().getName().getNamespaceURI().equals(NS_DATASERVICES)){
				
				
				String name = event.asStartElement().getName().getLocalPart();
				Attribute typeAttribute = event.asStartElement().getAttributeByName(M_TYPE);
				Attribute nullAttribute = event.asStartElement().getAttributeByName(M_NULL);
				boolean isNull = nullAttribute != null && nullAttribute.getValue().equals("true");
				
				String type = null;
				boolean isComplexType = false;
				if (typeAttribute!=null) {
					type = typeAttribute.getValue();
					EdmType et = EdmType.fromTypeString(type);
					isComplexType  = et==null;
				}
				EntityProperty ep = null;
				if (isComplexType){
					ComplexEntityProperty cep = new ComplexEntityProperty();
					if (!isNull)
						cep.properties = Enumerable.create( parseProperties(reader,event.asStartElement())).toList();
					ep = cep;
				} else {
					StringEntityProperty sep = new StringEntityProperty();
					if (!isNull)
						sep.value = reader.getElementText();
					ep = sep;
				}
				ep.name = name;
				ep.type = type;
				rt.add(ep);
				
			}
			
			
			
		}
		
		throw new RuntimeException();
	}
	
	
	private void dumpHeaders(ClientResponse response){
		for(String key : response.getHeaders().keySet()){
			log(key+ ": " + response.getHeaders().getFirst(key));
		}
	}
	
	

	
	
	
	private Iterable<CollectionInfo> parseCollections(XMLEventReader reader) throws Exception {

		
		String baseUrl = null;
		while (reader.hasNext()) {
			XMLEvent event = reader.nextEvent();
		
			if (isStartElement(event,APP_SERVICE)){
				baseUrl = event.asStartElement().getAttributeByName(XML_BASE).getValue();
			}
			if (isStartElement(event,APP_WORKSPACE)){
				return parseWorkspace(baseUrl,reader,event.asStartElement());
			}
			if (event.isStartElement()){
				//log(event.toString());
			}
		

		}
		throw new RuntimeException();
	}
	
	
	private Iterable<CollectionInfo> parseWorkspace(String baseUrl, XMLEventReader reader, StartElement startElement) throws Exception {
		List<CollectionInfo> rt = new ArrayList<CollectionInfo>();
	
		while (reader.hasNext()) {
			XMLEvent event = reader.nextEvent();
			
			if (event.isEndElement() && event.asEndElement().getName().equals(startElement.getName())){
				return rt;
			}
			
			if (isStartElement(event, APP_COLLECTION)){
				rt.add(parseCollection(baseUrl,reader,event.asStartElement()));
			}
			
			
			
			
		}
		return rt;
	}
	
	private CollectionInfo parseCollection(String baseUrl, XMLEventReader reader, StartElement startElement) throws Exception {
		CollectionInfo rt = new CollectionInfo();
		
		
		String href = getAttributeValueIfExists(startElement, "href");
		rt.url = urlCombine(baseUrl,href);
		
		while (reader.hasNext()) {
			XMLEvent event = reader.nextEvent();
			
			if (event.isEndElement() && event.asEndElement().getName().equals(startElement.getName())){
				return rt;
			}
			
			
			if(isStartElement(event, ATOM_TITLE)){

				rt.title = reader.getElementText();
			}
			
			if(isStartElement(event, APP_ACCEPT)){
				
				rt.accept = reader.getElementText();
			}
			
			
		}
		
		return rt;
	}
	
	private boolean isStartElement(XMLEvent event, QName qname) {
		return event.isStartElement() && event.asStartElement().getName().equals(qname);
	}

	

	private String urlCombine(String base, String rel) {
		if (!base.endsWith("/") && !rel.startsWith("/"))
			base = base + "/";
		return base + rel;
	}

	private static void log(String message) {
		System.out.println(message);
	}
}
