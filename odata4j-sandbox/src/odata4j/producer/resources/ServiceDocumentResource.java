package odata4j.producer.resources;

import java.io.StringWriter;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;

import odata4j.edm.EdmDataServices;
import odata4j.producer.ODataProducer;
import odata4j.xml.ServiceDocumentWriter;

import com.sun.jersey.api.core.HttpContext;

@Path("")
public class ServiceDocumentResource {


	
	@GET
	@Produces(ODataConstants.APPLICATION_XML_CHARSET)
	public Response getServiceDocument(@Context HttpContext context, @Context ODataProducer producer){
		
		String baseUri = context.getUriInfo().getBaseUri().toString();
		
		EdmDataServices s = producer.getMetadata();
		
		StringWriter w = new StringWriter();
		ServiceDocumentWriter.write(baseUri, s,w);
		

		return Response.ok(w.toString(),ODataConstants.APPLICATION_XML_CHARSET).header("DataServiceVersion","1.0").build();
	}
}
