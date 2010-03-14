package odata4j.xml;

import java.io.Writer;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.ws.rs.core.MediaType;
import javax.xml.namespace.QName;

import org.apache.commons.codec.binary.Base64;
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;

import odata4j.backend.EntityResponse;
import odata4j.backend.OEntity;
import odata4j.backend.OProperty;
import odata4j.edm.EdmEntitySet;
import odata4j.edm.EdmNavigationProperty;
import odata4j.edm.EdmType;

public class AtomFeedWriter extends BaseWriter {

	
	
	public static void generate(String baseUri, EntityResponse response, Writer w){
		
		EdmEntitySet ees = response.getEntitySet();
		String entityName = ees.name;
		
		XmlWriter writer = new XmlWriter(w);
		writer.startDocument();
		
		writer.startElement(new QName("feed"),atom);
		writer.writeNamespace("m", m);
		writer.writeNamespace("d", d);
		writer.writeNamespace("base", baseUri);
		
		writeElement(writer, "title", entityName,"type","text");
		writeElement(writer,"id",baseUri + entityName);
		
		DateTime utc = new DateTime().withZone(DateTimeZone.UTC); 
		String updated = utc.toString("yyyy-MM-dd'T'HH:mm:ss'Z'");
		writeElement(writer,"updated",updated);
		
		writeElement(writer,"link",null,"rel","self","title",entityName,"href",entityName);
		
		for(OEntity entity : response.getEntities()){
			writer.startElement("entry");
			String key = null;
			if (entity.getKeyProperties().size() ==1){
				key = entity.getKeyProperties().get(0).getValue().toString();
			}
			String relid = entityName + "(" + key + ")";
			String absid = baseUri + relid;
			writeElement(writer,"id",absid);
			
			writeElement(writer,"title",null,"type","text");
			writeElement(writer,"updated",updated);
			
			writer.startElement("author");
			writeElement(writer,"name",null);
			writer.endElement("author");
			
			writeElement(writer,"link",null,"rel","edit","title",entityName,"href",relid);
			
			

			for(EdmNavigationProperty np : ees.type.navigationProperties){
				// TODO
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
			
			writer.startElement("content");
			writer.writeAttribute("type", MediaType.APPLICATION_XML);
			
			writer.startElement(new QName(m,"properties","m"));
			
			for(OProperty<?> prop : entity.getProperties()){
				String name = prop.getName();
				EdmType type = prop.getType();
				Object value = prop.getValue();
				
				writer.startElement(new QName(d,name,"d"));
				
				String sValue = null;
				
				if (type == EdmType.INT32){
					writer.writeAttribute(new QName(m,"type","m"), type.getTypeString());
					if (value != null) sValue = value.toString();
				} else if (type == EdmType.INT16){
					writer.writeAttribute(new QName(m,"type","m"), type.getTypeString());
					if (value != null) sValue = value.toString();
				}else if (type == EdmType.BOOLEAN){
					writer.writeAttribute(new QName(m,"type","m"), type.getTypeString());
					if (value != null) sValue = value.toString();
				}else if (type == EdmType.DECIMAL){
					writer.writeAttribute(new QName(m,"type","m"), type.getTypeString());
					if (value != null) sValue = value.toString();
				} else if (type == EdmType.STRING){
					if (value != null) sValue = value.toString();
				}  else if (type == EdmType.BINARY){
					writer.writeAttribute(new QName(m,"type","m"), type.getTypeString());
					byte[] bValue = (byte[]) value;
					if (value != null) sValue = Base64.encodeBase64String(bValue);
				} else {
					throw new UnsupportedOperationException("Implement " + type);
				}
				if (value == null) {
					writer.writeAttribute(new QName(m,"null","m"), "true");
				} else {
					writer.writeText(sValue);
				}
				writer.endElement(name);
				
			}
			
			
			writer.endElement("properties");
			writer.endElement("content");
			
			writer.endElement("entry");
		}
		
		writer.endDocument();
		
	}
	
	private static void writeElement(XmlWriter writer, String elementName, String elementText, String... attributes){
		writer.startElement(elementName);
		for(int i =0;i<attributes.length;i+=2){
			writer.writeAttribute(attributes[i], attributes[i+1]);
		}
		if (elementText != null)
			writer.writeText(elementText);
		writer.endElement(elementName);
	}
}
