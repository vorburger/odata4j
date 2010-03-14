package odata4j.xml;

import java.io.Writer;

import javax.xml.namespace.QName;

import odata4j.edm.EdmAssociation;
import odata4j.edm.EdmAssociationSet;
import odata4j.edm.EdmDataServices;
import odata4j.edm.EdmEntityContainer;
import odata4j.edm.EdmEntitySet;
import odata4j.edm.EdmEntityType;
import odata4j.edm.EdmNavigationProperty;
import odata4j.edm.EdmProperty;
import odata4j.edm.EdmSchema;

public class EdmxWriter extends BaseWriter {

	
	public static void write(EdmDataServices services, Writer w){
		
		XmlWriter writer = new XmlWriter(w);
		writer.startDocument();
		
		writer.startElement(new QName(edmx,"Edmx","edmx"));
		writer.writeAttribute("Version", "1.0");
		writer.writeNamespace("edmx", edmx);
		
		writer.startElement(new QName(edmx,"DataServices","edmx"));
		
		// Schema
		for(EdmSchema schema : services.schemas){
			
			
			writer.startElement(new QName("Schema"),edm);
			writer.writeAttribute("Namespace", schema.namespace);
			writer.writeNamespace("d", d);
			writer.writeNamespace("m", m);
		
			// EntityType
			for(EdmEntityType eet : schema.entityTypes){
				writer.startElement(new QName("EntityType"));
				
				writer.writeAttribute("Name", eet.name);
				
				writer.startElement(new QName("Key"));
				writer.startElement(new QName("PropertyRef"));
				writer.writeAttribute("Name", eet.key);
				writer.endElement("PropertyRef");
				writer.endElement("Key");
				
				for(EdmProperty prop : eet.properties){
					
					writer.startElement(new QName("Property"));
					
					writer.writeAttribute("Name", prop.name);
					writer.writeAttribute("Type", prop.type.getTypeString());
					writer.writeAttribute("Nullable", Boolean.toString(prop.nullable));
					if(prop.maxLength != null)
						writer.writeAttribute("MaxLength", Integer.toString(prop.maxLength));
					writer.endElement("Property");
				}
				
				for(EdmNavigationProperty np : eet.navigationProperties){
					
					writer.startElement(new QName("NavigationProperty"));
					writer.writeAttribute("Name", np.name);
					writer.writeAttribute("Relationship", np.relationship.getFQName());
					writer.writeAttribute("FromRole", np.fromRole.role);
					writer.writeAttribute("ToRole", np.toRole.role);
					
					writer.endElement("NavigationProperty");
					
				}
				
				
				writer.endElement("EntityType");
				
				
				
			}
			
			// Associaion
			for(EdmAssociation assoc : schema.associations){
				writer.startElement(new QName("Association"));
				
				writer.writeAttribute("Name", assoc.name);
				
				writer.startElement(new QName("End"));
				writer.writeAttribute("Role", assoc.end1.role);
				writer.writeAttribute("Type", assoc.end1.type.getFQName());
				writer.writeAttribute("Multiplicity",assoc.end1.multiplicity.getSymbolString());
				writer.endElement("End");
				
				writer.startElement(new QName("End"));
				writer.writeAttribute("Role", assoc.end2.role);
				writer.writeAttribute("Type", assoc.end2.type.getFQName());
				writer.writeAttribute("Multiplicity",assoc.end2.multiplicity.getSymbolString());
				writer.endElement("End");
				
				
				writer.endElement("Association");
			}
			
			// EntityContainer
			for(EdmEntityContainer container : schema.entityContainers){
				writer.startElement(new QName("EntityContainer"));
				
				writer.writeAttribute("Name", container.name);
				writer.writeAttribute(new QName(m,"IsDefaultEntityContainer","m"), Boolean.toString(container.isDefault));
				
				for(EdmEntitySet ees : container.entitySets){
					writer.startElement(new QName("EntitySet"));
					writer.writeAttribute("Name", ees.name);
					writer.writeAttribute("EntityType", ees.type.getFQName());
					writer.endElement("EntitySet");
				}
				for(EdmAssociationSet eas : container.associationSets){
					writer.startElement(new QName("AssociationSet"));
					writer.writeAttribute("Name", eas.name);
					writer.writeAttribute("Association", eas.association.getFQName());
					
					writer.startElement(new QName("End"));
					writer.writeAttribute("Role", eas.end1.role.role);
					writer.writeAttribute("EntitySet", eas.end1.entitySet.name);
					writer.endElement("End");
					
					writer.startElement(new QName("End"));
					writer.writeAttribute("Role", eas.end2.role.role);
					writer.writeAttribute("EntitySet", eas.end2.entitySet.name);
					writer.endElement("End");
					
					writer.endElement("AssociationSet");
				}
				
				writer.endElement("EntityContainer");
			}
			
			
			writer.endElement("Schema");
			
		}
		
		
		writer.endDocument();
	}
}
