package odata4j.service.resources;

import java.io.StringWriter;
import java.util.logging.Logger;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.apache.commons.lang.StringUtils;

import core4j.Enumerable;
import core4j.Func1;

import odata4j.backend.EntitiesRequest;
import odata4j.backend.EntitiesResponse;
import odata4j.backend.OEntity;
import odata4j.backend.OProperty;
import odata4j.edm.EdmDataServices;
import odata4j.service.ODataService;
import odata4j.xml.AtomFeedWriter;

@Path("{entityName}")
public class EntitiesRequestResource {

	private static final Logger log = Logger.getLogger(EntitiesRequestResource.class.getName());
	
	@GET
	@Produces(ODataConstants.APPLICATION_ATOM_XML_CHARSET)
	public Response getEntities(final @PathParam("entityName") String entityName, @QueryParam("$top") String top, @QueryParam("$skip") String skip){
		log.info(String.format("getEntities(%s,%s,%s)",entityName,top,skip));
		
		final Integer finalTop = top==null?null:Integer.parseInt(top);
		final Integer finalSkip = skip==null?null:Integer.parseInt(skip);
		
		ODataService service = ODataService.getInstance();
		EntitiesRequest request = new EntitiesRequest(){
			public String getEntityName() {
				return entityName;
			}
			public Integer getTop() {
				return finalTop;
			}
			@Override
			public Integer getSkip() {
				return finalSkip;
			}};
		EntitiesResponse response = service.getBackend().getEntities(request);
		
		StringWriter sw = new StringWriter();
		AtomFeedWriter.generateFeed(service.getBaseUri(),response,sw);
		String entity = sw.toString();
		//log.info("entity: " + entity);
		return Response.ok(entity,ODataConstants.APPLICATION_ATOM_XML_CHARSET).header("DataServiceVersion","1.0").build();
		
	}
	

}
