package odata4j.service.resources;

import java.io.StringWriter;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Response;

import odata4j.edm.EdmDataServices;
import odata4j.service.ODataService;
import odata4j.xml.ServiceDocumentWriter;

@Path("")
public class ServiceDocumentResource {

	@GET
	@Produces(ODataConstants.APPLICATION_XML_CHARSET)
	public Response getServiceDocument(){
		
		ODataService service = ODataService.getInstance();
		 
		EdmDataServices s = service.getBackend().getMetadata();
		
		StringWriter w = new StringWriter();
		ServiceDocumentWriter.write(service.getBaseUri(), s,w);
		

		return Response.ok(w.toString(),ODataConstants.APPLICATION_XML_CHARSET).header("DataServiceVersion","1.0").build();
	}
}
