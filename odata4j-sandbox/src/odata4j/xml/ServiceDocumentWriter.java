package odata4j.xml;

import java.io.Writer;

import javax.xml.namespace.QName;

import odata4j.edm.EdmDataServices;
import odata4j.edm.EdmEntityContainer;
import odata4j.edm.EdmEntitySet;
import odata4j.edm.EdmSchema;
import core4j.Enumerable;
import core4j.Funcs;

public class ServiceDocumentWriter extends BaseWriter {
	
	public static void write(String base, EdmDataServices services, Writer w){
		XmlWriter writer = new XmlWriter(w);
		writer.startDocument();
		
		String xmlns = app;
		
		writer.startElement(new QName("service"), xmlns);
		writer.writeAttribute(new QName("xml:base"), base);
		writer.writeNamespace("atom", atom);
		writer.writeNamespace("app", app);
		
		writer.startElement(new QName("workspace"));
		writeAtomTitle(writer,atom,"Default");
		
		for(EdmSchema es : services.schemas){
			for(EdmEntityContainer eec : es.entityContainers){
				for(EdmEntitySet ees : Enumerable.create(eec.entitySets).orderBy(Funcs.field(EdmEntitySet.class, String.class, "name"))){
					writer.startElement("collection");
					writer.writeAttribute("href", ees.name);
					writeAtomTitle(writer,atom,ees.name);
					
					writer.endElement("collection");
				}
			}
		}
		
		
		writer.endElement("workspace");
		writer.endDocument();
	}
	
	private static void writeAtomTitle(XmlWriter writer, String atom, String title){
		writer.startElement(new QName(atom,"title","atom"));
		writer.writeText(title);
		writer.endElement("title");
	}
}
