package odata4j.internal;

import java.io.IOException;
import java.lang.reflect.Field;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Set;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLSession;
import javax.ws.rs.ext.RuntimeDelegate;

import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.config.DefaultClientConfig;
import com.sun.jersey.client.urlconnection.HttpURLConnectionFactory;
import com.sun.jersey.client.urlconnection.URLConnectionClientHandler;
import com.sun.jersey.core.impl.provider.entity.StringProvider;
import com.sun.jersey.core.impl.provider.header.MediaTypeProvider;
import com.sun.jersey.core.spi.factory.AbstractRuntimeDelegate;
import com.sun.jersey.spi.HeaderDelegateProvider;

public class PlatformUtil {

	private static boolean RUNNING_ON_ANDROID;
	static {
		try {Class.forName("android.app.Activity"); RUNNING_ON_ANDROID = true;} catch (Exception e) { RUNNING_ON_ANDROID = false;}
	
		if (runningOnAndroid()){
			androidInit();
		}
	}
	private static void androidInit(){
		
		 
        try  {
	        RuntimeDelegate rd = RuntimeDelegate.getInstance();
	        Field f = AbstractRuntimeDelegate.class.getDeclaredField("hps");
	        f.setAccessible(true);
	        Set<HeaderDelegateProvider> hps =    (Set<HeaderDelegateProvider>) f.get(rd);
	        hps.clear();
	        hps.add(new MediaTypeProvider());
	    } catch(Exception e){
	    	throw new RuntimeException(e);
	    }
	}
	public static Client newClient(){
		
		
		
		DefaultClientConfig cc = new DefaultClientConfig();
		cc.getSingletons().add(new StringProvider2());
		Client client = Client.create(cc);
		return client;
	}

	
	public static boolean runningOnAndroid(){
		return RUNNING_ON_ANDROID;
	}
	
	
	public static String getTextContent(Element element){
		// FOR ANDROID
		StringBuilder buffer = new StringBuilder();
		NodeList childList = element.getChildNodes();
		for (int i = 0; i < childList.getLength(); i++) {
		    Node child = childList.item(i);
		    if (child.getNodeType() == Node.TEXT_NODE)
		    	 buffer.append(child.getNodeValue());
		}

		return buffer.toString(); 
	}
	
}
