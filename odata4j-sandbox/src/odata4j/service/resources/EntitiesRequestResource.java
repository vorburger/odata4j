package odata4j.service.resources;

import java.io.StringWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Response;

import odata4j.backend.EntitiesRequest;
import odata4j.backend.EntitiesResponse;
import odata4j.backend.QueryInfo;
import odata4j.backend.QueryInfo.OrderBy;
import odata4j.service.ODataService;
import odata4j.xml.AtomFeedWriter;

@Path("{entityName}")
public class EntitiesRequestResource {

	private static final Logger log = Logger.getLogger(EntitiesRequestResource.class.getName());
	
	@GET
	@Produces(ODataConstants.APPLICATION_ATOM_XML_CHARSET)
	public Response getEntities(
			final @PathParam("entityName") String entityName, 
			@QueryParam("$top") String top, 
			@QueryParam("$skip") String skip,
			@QueryParam("$orderby") String orderBy){
		log.info(String.format("getEntities(%s,%s,%s,%s)",entityName,top,skip,orderBy));
		
		
		final QueryInfo finalQuery = new QueryInfo(parseTop(top),parseSkip(skip),parseOrderBy(orderBy));
		
		
		ODataService service = ODataService.getInstance();
		EntitiesRequest request = new EntitiesRequest(){
			public String getEntityName() {
				return entityName;
			}
			public QueryInfo getQueryInfo() {
				return finalQuery;
			}};
		EntitiesResponse response = service.getBackend().getEntities(request);
		
		StringWriter sw = new StringWriter();
		AtomFeedWriter.generateFeed(service.getBaseUri(),response,sw);
		String entity = sw.toString();
		//log.info("entity: " + entity);
		return Response.ok(entity,ODataConstants.APPLICATION_ATOM_XML_CHARSET).header("DataServiceVersion","1.0").build();
		
	}
	
	private Integer parseTop(String top){
		return top==null?null:Integer.parseInt(top);
	}
	private Integer parseSkip(String skip){
		return skip==null?null:Integer.parseInt(skip);
	}
	private List<OrderBy> parseOrderBy(String orderBy){
		List<OrderBy> rt = new ArrayList<OrderBy>();
		if (orderBy==null)
			return rt;
		for(String token : orderBy.split(",")){
			token = token.trim();
			boolean isAscending = true;
			if (token.toLowerCase().endsWith(" asc"))
				token = token.substring(0,token.length()-4);
			if (token.toLowerCase().endsWith(" desc")) {
				isAscending = false;
				token = token.substring(0,token.length()-5);
			}
			rt.add(new OrderBy(token,isAscending));
			
		}
		return rt;
	}

}
