package odata4j.xml;

import java.io.Writer;
import java.util.Iterator;

import javax.xml.namespace.QName;
import javax.xml.stream.XMLEventFactory;
import javax.xml.stream.XMLEventWriter;
import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.events.XMLEvent;

import core4j.Enumerable;

public class XmlWriter {

	//private final XMLStreamWriter writer;
	private final XMLEventFactory eventFactory;
    private final XMLEventWriter eventWriter;
	
	public XmlWriter(Writer stream){
		XMLOutputFactory f = XMLOutputFactory.newInstance();
		
		try {
			//writer = f.createXMLStreamWriter(stream);
			eventFactory = XMLEventFactory.newInstance();
			eventWriter =  f.createXMLEventWriter(stream);
			eventWriter.setDefaultNamespace("");
		
			
		} catch (XMLStreamException e) {
			throw new RuntimeException(e);
		}
		
	}

	
	public void setPrefix(String prefix, String namespaceUri){
		try {
			eventWriter.setPrefix(prefix, namespaceUri);
		} catch (XMLStreamException e) {
			throw new RuntimeException(e);
		}
	}
	public void setDefaultNamespace( String namespaceUri){
		try {
			eventWriter.setDefaultNamespace(namespaceUri);
		} catch (XMLStreamException e) {
			throw new RuntimeException(e);
		}
	}
	public void startElement(String name) {
		startElement(new QName(name));
	}
	public void startElement(QName qname) {
		startElement(qname,null);
	}
	public void startElement(QName qname, String xmlns) {
		//writer.setDefaultNamespace("http://www.example.com/ns1");
	    try {
	    	Iterator nsIterator = null;
	    	if (xmlns != null){
	    		nsIterator = Enumerable.create( eventFactory.createNamespace(xmlns)).iterator();
	    	}
	    
			//writer.writeStartElement(prefix,localName,namespaceURI);
			XMLEvent event = eventFactory.createStartElement(qname, null,nsIterator);
			eventWriter.add(event);
			
		} catch (XMLStreamException e) {
			throw new RuntimeException(e);
		}
		
	}
	
	public void writeAttribute(String localName, String value){
		 try {
			//writer.writeAttribute(localName, value);
			 XMLEvent event = eventFactory.createAttribute(localName,value);
			 eventWriter.add(event);
			 
			
		} catch (XMLStreamException e) {
			throw new RuntimeException(e);
		}
	}
	public void writeAttribute(QName qname, String value){
		 try {
			//writer.writeAttribute(localName, value);
			 XMLEvent event = eventFactory.createAttribute(qname,value);
			 eventWriter.add(event);
			 
			
		} catch (XMLStreamException e) {
			throw new RuntimeException(e);
		}
	}
	public void writeText(String content){
		 try {
			//writer.writeAttribute(localName, value);
			 XMLEvent event = eventFactory.createCharacters(content);
			 eventWriter.add(event);
			 
			
		} catch (XMLStreamException e) {
			throw new RuntimeException(e);
		}
	}

	public void writeNamespace(String prefix, String namespaceUri){
		 try {
				//writer.writeNamespace(prefix, namespaceURI);
			 XMLEvent event = eventFactory.createNamespace(prefix,namespaceUri);
			 eventWriter.add(event);
			} catch (XMLStreamException e) {
				throw new RuntimeException(e);
			}
		
	}
	
	public void startDocument() {
		try {
			
			 eventWriter.add(eventFactory.createStartDocument("utf-8","1.0",true));
		} catch (XMLStreamException e) {
			throw new RuntimeException(e);
		}
		
	}
	public void endElement(String localName) {
		try {
			//writer.writeEndElement();
			 XMLEvent event = eventFactory.createEndElement(new QName(localName), null);
			 eventWriter.add(event);
		} catch (XMLStreamException e) {
			throw new RuntimeException(e);
		}
		
	}
	
	public void endDocument(){
		 try {
		//	writer.writeEndDocument();
			// writer.flush();
			 XMLEvent event = eventFactory.createEndDocument();
			 eventWriter.add(event);
			 eventWriter.flush();
		} catch (XMLStreamException e) {
			throw new RuntimeException(e);
		}
		
	}
}
