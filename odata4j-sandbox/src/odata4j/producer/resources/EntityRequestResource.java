package odata4j.producer.resources;

import java.io.StringWriter;
import java.util.logging.Logger;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;

import odata4j.producer.EntityRequest;
import odata4j.producer.EntityResponse;
import odata4j.producer.ODataProducer;
import odata4j.xml.AtomFeedWriter;

import org.apache.commons.lang.StringUtils;

import com.sun.jersey.api.core.HttpContext;
import com.sun.jersey.spi.inject.Inject;

@Path("{entityName}{id: (\\(.*\\))}")
public class EntityRequestResource {

	private static final Logger log = Logger.getLogger(EntityRequestResource.class.getName());
	
	
	@GET
	@Produces(ODataConstants.APPLICATION_ATOM_XML_CHARSET)
	public Response getEntities(
			@Context HttpContext context,
			@Context ODataProducer producer,
			final @PathParam("entityName") String entityName, 
			@PathParam("id") String id){
		
		log.info(String.format("getEntities(%s,%s)",entityName,id));
		
		String baseUri = context.getUriInfo().getBaseUri().toString();
		
		String cleanid = null;
		if (!StringUtils.isBlank(id)){
			if (id.startsWith("(") && id.endsWith(")")){
				cleanid = id.substring(1,id.length()-1);
				log.info("cleanid!: " + cleanid);
			}
		}
		if (cleanid==null)
			throw new RuntimeException("unable to parse id");
		
		Object idObject;
		if (cleanid.startsWith("'") && cleanid.endsWith("'")){
			idObject = cleanid.substring(1,cleanid.length()-1);
		} else {
			idObject = Integer.parseInt(cleanid);
		}
		final Object idObjectFinal = idObject;
		

		EntityRequest request = new EntityRequest(){
			public String getEntityName() {
				return entityName;
			}
			public Object getEntityKey() {
				return idObjectFinal;
			}};
			
		EntityResponse response = producer.getEntity(request);
		
		StringWriter sw = new StringWriter();
		AtomFeedWriter.generateResponseEntry(baseUri,response,sw);
		String entity = sw.toString();
		
		return Response.ok(entity,ODataConstants.APPLICATION_ATOM_XML_CHARSET).header("DataServiceVersion","1.0").build();
		
	}
	

}
