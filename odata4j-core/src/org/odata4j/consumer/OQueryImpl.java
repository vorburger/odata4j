package org.odata4j.consumer;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.odata4j.core.OEntity;
import org.odata4j.core.OQuery;
import org.odata4j.internal.EntitySegment;
import org.odata4j.internal.InternalUtil;
import org.odata4j.xml.AtomFeedParser.AtomEntry;
import org.odata4j.xml.AtomFeedParser.AtomFeed;
import org.odata4j.xml.AtomFeedParser.DataServicesAtomEntry;

import core4j.Enumerable;
import core4j.Func;
import core4j.Func1;
import core4j.ReadOnlyIterator;

public class OQueryImpl<T> implements OQuery<T> {

	private final ODataClient client;
	private final Class<T> entityType;
	private final String serviceRootUri;
	private final List<EntitySegment> segments = new ArrayList<EntitySegment>();
	private final Map<String,String> customs = new HashMap<String,String>();
	
	private Integer top;
	private Integer skip;
	private String orderBy;
	private String filter;
	private String select;
	private String lastSegment;
	
	public OQueryImpl(ODataClient client, Class<T> entityType, String serviceRootUri, String entitySetName){
		this.client = client;
		this.entityType = entityType;
		this.serviceRootUri = serviceRootUri;
		this.lastSegment = entitySetName;
	}
	
	@Override
	public OQuery<T> top(int top) {
		this.top = top;
		return this;
	}
	
	@Override
	public OQuery<T> skip(int skip) {
		this.skip = skip;
		return this;
	}
	
	@Override
	public OQuery<T> orderBy(String orderBy) {
		this.orderBy = orderBy;
		return this;
	}
	
	@Override
	public OQuery<T> filter(String filter) {
		this.filter = filter;
		return this;
	}
	
	@Override
	public OQuery<T> select(String select) {
		this.select = select;
		return this;
	}
	
	@Override
	public OQuery<T> custom(String name, String value) {
		customs.put(name, value);
		return this;
	}
	
	@Override
	public OQuery<T> nav(Object key, String navProperty) {
		return nav(new Object[]{key},navProperty);
	}
	
	
	
	@Override
	public OQuery<T> nav(Object[] key, String navProperty) {
		segments.add(new EntitySegment(lastSegment,key));
		lastSegment = navProperty;
		return this;
	}
	
	@Override
	public Enumerable<T> execute() {
		
		String path = Enumerable.create(segments).join("/");
		path += (path.length()==0?"":"/") + lastSegment;
	
		ODataClientRequest request = ODataClientRequest.get(serviceRootUri + path);
		
		if (top != null){
			request = request.queryParam("$top",Integer.toString(top));
		}
		if (skip != null){
			request = request.queryParam("$skip",Integer.toString(skip));
		}
		if (orderBy != null){
			request = request.queryParam("$orderby",orderBy);
		}
		if (filter != null){
			request = request.queryParam("$filter",filter);
		}
		if (select != null){
			request = request.queryParam("$select",select);
		}
		for(String name : customs.keySet()){
			request = request.queryParam(name, customs.get(name));
		}
		
		
		Enumerable<AtomEntry> entries = getEntries(request);
		
		if (entityType.equals(OEntity.class)){
			
			return entries.select(new Func1<AtomEntry,OEntity>(){
				public OEntity apply(AtomEntry input) {
					DataServicesAtomEntry dsae = (DataServicesAtomEntry)input;
					return InternalUtil.toEntity(dsae);
				}}).cast(entityType);
			
		}
			
		throw new UnsupportedOperationException("Entity type " + entityType);
	}
	
	private Enumerable<AtomEntry> getEntries(final ODataClientRequest request){
		
		return Enumerable.createFromIterator(new Func<Iterator<AtomEntry>>(){
			public Iterator<AtomEntry> apply() {
				return new AtomEntryIterator(request);
			}});
		
		
	}
	
	private class AtomEntryIterator extends ReadOnlyIterator<AtomEntry> {

		private ODataClientRequest request;
		private AtomFeed feed;
		private Iterator<AtomEntry> feedEntries;
		private int feedEntryCount;
		
		public AtomEntryIterator(ODataClientRequest request){
			this.request = request;
		}
		@Override
		protected IterationResult<AtomEntry> advance() throws Exception {
			
			if (feed == null) {
				feed = client.getEntities(request);
				feedEntries = feed.entries.iterator();
				feedEntryCount = 0;
			}
			
			if (feedEntries.hasNext()) {
				feedEntryCount++;
				return IterationResult.next(feedEntries.next());
			}
			
			
			// old-style paging: $page and $itemsPerPage
			if (request.getQueryParams().containsKey("$page") && request.getQueryParams().containsKey("$itemsPerPage")){
				
				if (feedEntryCount==0)
					return IterationResult.done();
				
				
				int page = Integer.parseInt(request.getQueryParams().get("$page"));
				//int itemsPerPage = Integer.parseInt(request.getQueryParams().get("$itemsPerPage"));
				
				request = request.queryParam("$page", Integer.toString(page+1));
				
			} 
			// new-style paging: $skiptoken
			else {
				
				if (feed.next == null)
					return IterationResult.done();
			
				String skiptoken = feed.next.substring(feed.next.indexOf("$skiptoken=")+"$skiptoken=".length());
				request = request.queryParam("$skiptoken", skiptoken);
			}
			
			
			
			feed = null;
			
			return advance();  // TODO stackoverflow possible here
		}
		
	}
	
	
	
	
	
	
	
	
	
}