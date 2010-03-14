package odata4j.service.resources;

import java.io.StringWriter;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import core4j.Enumerable;
import core4j.Func1;

import odata4j.backend.EntityRequest;
import odata4j.backend.EntityResponse;
import odata4j.backend.OEntity;
import odata4j.backend.OProperty;
import odata4j.edm.EdmDataServices;
import odata4j.service.ODataService;
import odata4j.xml.AtomFeedWriter;

@Path("{entityName}")
public class EntityRequestResource {

	@GET
	@Produces(MediaType.APPLICATION_XML)
	public Response getEntities(final @PathParam("entityName") String entityName){
		
		ODataService service = ODataService.getInstance();
		EntityRequest request = new EntityRequest(){
			public String getEntityName() {
				return entityName;
			}};
		EntityResponse response = service.getBackend().getResponse(request);
		
		StringWriter sw = new StringWriter();
		AtomFeedWriter.generate(service.getBaseUri(),response,sw);
		String entity = sw.toString();
		
		return Response.ok(entity,MediaType.APPLICATION_XML).header("DataServiceVersion","1.0").build();
		
	}
	

}
