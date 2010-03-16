package odata4j.backend;

import java.util.List;

public class QueryInfo {

	public static class OrderBy{
		public final String field;
		public final boolean ascending;
		public OrderBy(String field, boolean ascending){
			this.field = field;
			this.ascending = ascending;
		}
	}
	public final Integer top;
	public final Integer skip;
	public final List<OrderBy> orderBy;
	
	public QueryInfo(Integer top, Integer skip, List<OrderBy> orderBy){
		this.top = top;
		this.skip = skip;
		this.orderBy = orderBy;
	}
	
}
