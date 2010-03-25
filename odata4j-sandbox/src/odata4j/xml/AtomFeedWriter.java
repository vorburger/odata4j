package odata4j.xml;

import java.io.Writer;
import java.util.List;

import javax.ws.rs.core.MediaType;
import javax.xml.namespace.QName;

import odata4j.consumer.ODataClient.DataServicesAtomEntry;
import odata4j.core.OEntity;
import odata4j.core.OProperty;
import odata4j.edm.EdmEntitySet;
import odata4j.edm.EdmNavigationProperty;
import odata4j.edm.EdmType;
import odata4j.internal.InternalUtil;
import odata4j.producer.EntitiesResponse;
import odata4j.producer.EntityResponse;
import odata4j.stax2.QName2;
import odata4j.stax2.XMLFactoryProvider2;
import odata4j.stax2.XMLWriter2;
import odata4j.stax2.staximpl.StaxXMLWriter2;

import org.apache.commons.codec2.binary.Base64;
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.joda.time.LocalDateTime;

public class AtomFeedWriter extends BaseWriter {

	public static void generateResponseEntry(String baseUri, EntityResponse response, Writer w){
		
		EdmEntitySet ees = response.getEntitySet();
		String entityName = ees.name;
		DateTime utc = new DateTime().withZone(DateTimeZone.UTC); 
		String updated = toString(utc);
		
		XMLWriter2 writer = XMLFactoryProvider2.getInstance().newXMLWriterFactory2().createXMLWriter(w);
		writer.startDocument();
		
		writer.startElement(new QName2("entry"),atom);
		writer.writeNamespace("m", m);
		writer.writeNamespace("d", d);
		writer.writeAttribute("xml:base", baseUri);
		
		
		writeEntry(writer,response.getEntity().getKeyProperties(),response.getEntity().getProperties(),entityName,baseUri,updated,ees);
		writer.endDocument();
	}
	
	public static void generateRequestEntry(DataServicesAtomEntry request, Writer w){
		
		DateTime utc = new DateTime().withZone(DateTimeZone.UTC); 
		String updated = toString(utc);
		
		XMLWriter2 writer = XMLFactoryProvider2.getInstance().newXMLWriterFactory2().createXMLWriter(w);
		writer.startDocument();
		
		writer.startElement(new QName2("entry"),atom);
		writer.writeNamespace("d", d);
		writer.writeNamespace("m", m);		
		
	    writeEntry(writer,null,request.properties,null,null,updated,null);
		writer.endDocument();
			
	}
	
	
	
	
	public static void generateFeed(String baseUri, EntitiesResponse response, Writer w){
		
		EdmEntitySet ees = response.getEntitySet();
		String entityName = ees.name;
		DateTime utc = new DateTime().withZone(DateTimeZone.UTC); 
		String updated = toString(utc);
		
		
		XMLWriter2 writer = XMLFactoryProvider2.getInstance().newXMLWriterFactory2().createXMLWriter(w);
		writer.startDocument();
		
		writer.startElement(new QName2("feed"),atom);
		writer.writeNamespace("m", m);
		writer.writeNamespace("d", d);
		writer.writeAttribute("xml:base", baseUri);
		
		writeElement(writer, "title", entityName,"type","text");
		writeElement(writer,"id",baseUri + entityName);
		
		
		writeElement(writer,"updated",updated);
		
		writeElement(writer,"link",null,"rel","self","title",entityName,"href",entityName);
		
		for(OEntity entity : response.getEntities()){
			writer.startElement("entry");
			writeEntry(writer,entity.getKeyProperties(),entity.getProperties(),entityName,baseUri,updated,ees);
			writer.endElement("entry");
		}
		writer.endDocument();
		
		
	}



	private static void writeEntry(XMLWriter2 writer, List<OProperty<?>> keyProperties, List<OProperty<?>> entityProperties, String entityName, String baseUri, String updated, EdmEntitySet ees){
		
		String key = null;
		if (keyProperties != null) {
			
			key = InternalUtil.keyString(keyProperties.toArray());
//			
//			if (keyProperties.size() ==1){
//				Object keyValue = keyProperties.get(0).getValue();
//				key = keyValue.toString();
//				if (keyValue instanceof String)
//					key= "'"+ key + "'";
//			} else {
//				throw new RuntimeException("Implement multiple keys");
//			}
		}
		
		String relid = null;
		if (entityName != null) {
			relid = entityName + "(" + key + ")";
			String absid = baseUri + relid;
			writeElement(writer,"id",absid);
		}
		
		writeElement(writer,"title",null,"type","text");
		writeElement(writer,"updated",updated);
		
		writer.startElement("author");
		writeElement(writer,"name",null);
		writer.endElement("author");
		
		if (entityName != null) 
			writeElement(writer,"link",null,"rel","edit","title",entityName,"href",relid);
		
		
		if (ees != null) {
			for(EdmNavigationProperty np : ees.type.navigationProperties){
				
				//  <link rel="http://schemas.microsoft.com/ado/2007/08/dataservices/related/Products" type="application/atom+xml;type=feed" title="Products" 
				// href="Suppliers(1)/Products" /> 
			
				String otherEntity = np.toRole.type.name;
				String rel = related + otherEntity;
				String type ="application/atom+xml;type=feed";
				String title = otherEntity;
				String href = relid + "/" + otherEntity;
				
				writeElement(writer, "link",null,"rel",rel,"type",type,"title",title,"href",href);
			
			}
			
			writeElement(writer,"category",null,"term",ees.type.getFQName(),"scheme",scheme);
		}
		
		
		
		
		writer.startElement("content");
		writer.writeAttribute("type", MediaType.APPLICATION_XML);
		
		writer.startElement(new QName2(m,"properties","m"));
		
		for(OProperty<?> prop : entityProperties){
			String name = prop.getName();
			EdmType type = prop.getType();
			Object value = prop.getValue();
			
			writer.startElement(new QName2(d,name,"d"));
			
			String sValue = null;
			
			if (type == EdmType.INT32){
				writer.writeAttribute(new QName2(m,"type","m"), type.toTypeString());
				if (value != null) sValue = value.toString();
			} else if (type == EdmType.INT16){
				writer.writeAttribute(new QName2(m,"type","m"), type.toTypeString());
				if (value != null) sValue = value.toString();
			}else if (type == EdmType.BOOLEAN){
				writer.writeAttribute(new QName2(m,"type","m"), type.toTypeString());
				if (value != null) sValue = value.toString();
			}else if (type == EdmType.DECIMAL){
				writer.writeAttribute(new QName2(m,"type","m"), type.toTypeString());
				if (value != null) sValue = value.toString();
			} else if (type == EdmType.STRING){
				if (value != null) sValue = value.toString();
			}else if (type == EdmType.DATETIME){
				writer.writeAttribute(new QName2(m,"type","m"), type.toTypeString());
				LocalDateTime ldt = (LocalDateTime)value;
				DateTime dt = ldt.toDateTime(DateTimeZone.UTC);
				if (value != null) sValue = toString(dt);
			}  else if (type == EdmType.BINARY){
				writer.writeAttribute(new QName2(m,"type","m"), type.toTypeString());
				byte[] bValue = (byte[]) value;
				if (value != null) sValue = Base64.encodeBase64String(bValue);
			} else {
				throw new UnsupportedOperationException("Implement " + type);
			}
			if (value == null) {
				writer.writeAttribute(new QName2(m,"null","m"), "true");
			} else {
				writer.writeText(sValue);
			}
			writer.endElement(name);
			
		}
		
		
		writer.endElement("properties");
		writer.endElement("content");
		
		
	}
	
	
	
	
	private static void writeElement(XMLWriter2 writer, String elementName, String elementText, String... attributes){
		writer.startElement(elementName);
		for(int i =0;i<attributes.length;i+=2){
			writer.writeAttribute(attributes[i], attributes[i+1]);
		}
		if (elementText != null)
			writer.writeText(elementText);
		writer.endElement(elementName);
	}
}
