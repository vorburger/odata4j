package odata4j.stax2;

import odata4j.internal.InternalUtil;

public abstract class XMLFactoryProvider2 {

	private static final XMLFactoryProvider2 STAX;
	static {
		try {
			String clazz =  InternalUtil.runningOnAndroid()?
					"odata4j.stax2.domimpl.DomXMLFactoryProvider2":
					"odata4j.stax2.staximpl.StaxXMLFactoryProvider2";
			
			STAX = (XMLFactoryProvider2)Class.forName(clazz).newInstance();

		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}
	public static XMLFactoryProvider2 getInstance(){
		return STAX;
	}
	
	
	public abstract XMLOutputFactory2 newXMLOutputFactory2();
	
	public abstract XMLInputFactory2 newXMLInputFactory2();
}
