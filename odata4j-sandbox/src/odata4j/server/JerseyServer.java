package odata4j.server;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Logger;

import com.sun.jersey.api.container.filter.LoggingFilter;
import com.sun.jersey.api.container.httpserver.HttpServerFactory;
import com.sun.jersey.api.core.DefaultResourceConfig;
import com.sun.jersey.spi.container.ContainerRequestFilter;
import com.sun.jersey.spi.container.ContainerResponseFilter;
import com.sun.jersey.spi.container.ResourceFilter;
import com.sun.jersey.spi.container.ResourceFilterFactory;
import com.sun.net.httpserver.Filter;
import com.sun.net.httpserver.HttpContext;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpServer;

import core4j.CoreUtils;
import core4j.Enumerable;

public class JerseyServer {

	private static final Logger log = Logger.getLogger(JerseyServer.class.getName());
	
	private HttpServer server;
	private final String baseUri;
	private final List<Filter> sunFilters = new ArrayList<Filter>();
	private final List<String> jerseyRequestFilters = new ArrayList<String>();
	private final List<String> jerseyResponseFilters = new ArrayList<String>();
	private final List<String> jerseyResourceFilters = new ArrayList<String>();
	private final List<Class> resourceClasses = new ArrayList<Class>();
	
	
	public JerseyServer(String baseUri){
		this.baseUri = baseUri;
	}
	
	public void addResourceClass(Class<?> clazz){
		resourceClasses.add(clazz);
	}
	public  <T extends ContainerRequestFilter> void addJerseyRequestFilter(Class<T> filter){
		jerseyRequestFilters.add(filter.getName());
	}
	public  <T extends ContainerResponseFilter> void addJerseyResponseFilter(Class<T> filter){
		jerseyResponseFilters.add(filter.getName());
	}
	public  <T extends ResourceFilterFactory> void addJerseyResourceFilter(Class<T> filter){
		jerseyResourceFilters.add(filter.getName());
	}
	public void addFilter(Filter filter){
		sunFilters.add(filter);
		
	}


	public void stop(){
		server.stop(0);
	}
	
	public void start(){
		
		try {
		
			DefaultResourceConfig c = new DefaultResourceConfig( Enumerable.create(this.resourceClasses).toArray(Class.class));
			
			
			Map<String,Object> paf = new HashMap<String,Object>();
			paf.put("com.sun.jersey.spi.container.ContainerRequestFilters",Enumerable.create(jerseyRequestFilters).toArray(String.class));
			paf.put("com.sun.jersey.spi.container.ContainerResponseFilters",Enumerable.create(jerseyResponseFilters).toArray(String.class));
			paf.put("com.sun.jersey.spi.container.ResourceFilters",Enumerable.create(jerseyResourceFilters).toArray(String.class));
			c.setPropertiesAndFeatures(paf);
			server = HttpServerFactory.create(baseUri,c);
			
			 Object tmp = CoreUtils.getFieldValue(server,"server",Object.class);
			 tmp = CoreUtils.getFieldValue(tmp,"contexts",Object.class);
			 tmp = CoreUtils.getFieldValue(tmp,"list",Object.class);
			 HttpContext context = ((List<HttpContext>)tmp).get(0);
			 context.getFilters().addAll(sunFilters);
			
			
			server.start();
			
			log.info(String.format("Jersey app started with WADL available at %sapplication.wadl\n", baseUri));
			
			//System.in.read();
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	
		//threadSelector.stopEndpoint();
		//System.exit(0);
	}
}
