package odata4j.producer;

import java.util.List;

import odata4j.expression.BoolCommonExpression;
import odata4j.expression.OrderByExpression;

public class QueryInfo {

	public final Integer top;
	public final Integer skip;
	public final BoolCommonExpression filter;
	public final List<OrderByExpression> orderBy;
	
	public QueryInfo(Integer top, Integer skip, BoolCommonExpression filter, List<OrderByExpression> orderBy){
		this.top = top;
		this.skip = skip;
		this.filter = filter;
		this.orderBy = orderBy;
	}
	
}
