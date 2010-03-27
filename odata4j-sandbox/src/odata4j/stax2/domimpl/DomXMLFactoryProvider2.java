package odata4j.stax2.domimpl;

import java.io.Reader;
import java.io.Writer;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import odata4j.internal.PlatformUtil;
import odata4j.stax2.Attribute2;
import odata4j.stax2.EndElement2;
import odata4j.stax2.QName2;
import odata4j.stax2.StartElement2;
import odata4j.stax2.XMLEvent2;
import odata4j.stax2.XMLEventReader2;
import odata4j.stax2.XMLFactoryProvider2;
import odata4j.stax2.XMLInputFactory2;
import odata4j.stax2.XMLOutputFactory2;
import odata4j.stax2.XMLWriter2;
import odata4j.stax2.XMLWriterFactory2;

import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

import core4j.ReadOnlyIterator;

public class DomXMLFactoryProvider2 extends XMLFactoryProvider2 {

	@Override
	public XMLInputFactory2 newXMLInputFactory2() {
		return new DomXMLInputFactory2();
	}

	@Override
	public XMLOutputFactory2 newXMLOutputFactory2() {
		throw new UnsupportedOperationException();
	}

	@Override
	public XMLWriterFactory2 newXMLWriterFactory2() {
		return new DomXMLWriterFactory2();
	}
	
	private static class DomXMLWriterFactory2 implements XMLWriterFactory2{

		@Override
		public XMLWriter2 createXMLWriter(Writer writer) {
			return new ManualXMLWriter2(writer);
		}
		
	}
	
	private static class DomXMLInputFactory2 implements XMLInputFactory2{

		@Override
		public XMLEventReader2 createXMLEventReader(Reader reader) {
			 DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
			 factory.setNamespaceAware(true);
			 try {
				DocumentBuilder builder = factory.newDocumentBuilder();
				Document document = builder.parse(new InputSource(reader));
				return new DomXMLEventReader2(document);
			} catch (Exception e) {
				throw new RuntimeException(e);
			}

			
		}
		
	}

	private static class DomXMLEventReader2 implements XMLEventReader2{
		private final Document document;
		

		private EventIterator iterator;
		
		public DomXMLEventReader2(Document document){
			this.document = document;
			iterator = new EventIterator();
		}
		@Override
		public String getElementText() {
			return iterator.getElementText();
		}
		@Override
		public boolean hasNext() {
			return iterator.hasNext();
		}
		@Override
		public XMLEvent2 nextEvent() {
			return iterator.next();
		}
		
		
		private class EventIterator extends ReadOnlyIterator<XMLEvent2> {

			private Element current;
			private boolean down = true;
			
			public EventIterator(){
				
			}
			
			
			public String getElementText() {
				return PlatformUtil.getTextContent(current);
			}


			private IterationResult<XMLEvent2> startElement2(){
				Object o = new DomStartElement2(current);
				XMLEvent2 event = new DomXMLEvent2(o);
				return IterationResult.next(event);
			}
			private IterationResult<XMLEvent2> endElement2(){
				Object o = new DomEndElement2(current);
				XMLEvent2 event = new DomXMLEvent2(o);
				return IterationResult.next(event);
			}
			
			@Override
			protected IterationResult<XMLEvent2> advance() throws Exception {
				
				if (current==null) {
					current = document.getDocumentElement();
					return startElement2();
					
				} else {
					
					if (down){
						// traverse down
						NodeList children = current.getChildNodes();
						for(int i = 0;i<children.getLength();i++){
							Node childNode = children.item(i);
							if (childNode.getNodeType() == Node.ELEMENT_NODE){
								current = (Element)childNode;
								return startElement2();
							}
						}
						down = false;
						return endElement2();
						
						
					} else {
						// traverse up
						Node parentNode = current.getParentNode();
						if (parentNode.getNodeType() == Node.DOCUMENT_NODE){
							return IterationResult.done();
						}
						Element parentElement = (Element)parentNode;
						NodeList children = parentElement.getChildNodes();
						boolean found = false;
						for(int i = 0;i<children.getLength();i++){
							Node childNode = children.item(i);
							if (childNode.getNodeType() == Node.ELEMENT_NODE){
								
								if (!found ) {
									if (childNode==current)
										found = true;
									continue;
								}
								
								current = (Element)childNode;
								down = true;
								return startElement2();
							}
						}
						current = parentElement;
						return endElement2();
					}
					
					
				}
				
			}
			
		}
	}
	
	
	
	
	private static class DomStartElement2 implements StartElement2 {

		private final Element element;
		public DomStartElement2(Element element){
			this.element = element;
		}
		@Override
		public Attribute2 getAttributeByName(QName2 qName2) {
			
			final Attr attr = element.getAttributeNodeNS(qName2.getNamespaceURI(), qName2.getLocalPart());
			if (attr==null)
				return null;
			
			return new Attribute2() {
				public String getValue() {
					return attr.getValue();
				}
			};
			
		}

		@Override
		public QName2 getName() {
			return new QName2(element.getNamespaceURI(),element.getLocalName());
		}
		
		@Override
		public String toString() {
			return "StartElement " + getName() + " " + PlatformUtil.getTextContent(element);
		}
		
	}
	private static class DomEndElement2 implements EndElement2 {

		private final Element element;
		public DomEndElement2(Element element){
			this.element = element;
		}
		
		@Override
		public QName2 getName() {
			return new QName2(element.getNamespaceURI(),element.getLocalName());
		}
		
		@Override
		public String toString() {
			return "EndElement " + getName() + " " + PlatformUtil.getTextContent(element);
		}
		
	}
	
	private static class DomXMLEvent2 implements XMLEvent2 {

		
		private final Object event;
		public DomXMLEvent2(Object event){
			this.event = event;
		}
		@Override
		public EndElement2 asEndElement() {
			return (EndElement2)event;
		}

		@Override
		public StartElement2 asStartElement() {
			return (StartElement2)event;
		}

		@Override
		public boolean isEndElement() {
			return event instanceof EndElement2;
		}

		@Override
		public boolean isStartElement() {
			return event instanceof StartElement2;
		}
		
		@Override
		public String toString() {
			return event.toString();
		}
		
	}
	
	
	
}
