package odata4j.expression;

public class Expression {

	public static NullLiteral null_(){
		return new NullLiteral(){ };
	}
	public static IntegralLiteral integral(final long value){
		return new IntegralLiteral(){

			@Override
			public long getValue() {
				return value;
			}};
	}
	public static StringLiteral string(final String value){
		return new StringLiteral(){

			@Override
			public String getValue() {
				return value;
			}};
	}
	public static EntitySimpleProperty simpleProperty(final String propertyName){
		return new EntitySimpleProperty(){

			@Override
			public String getPropertyName() {
				return propertyName;
			}};
	}
	
	public static EqExpression eq(final CommonExpression lhs, final CommonExpression rhs){
		return new EqExpression() {
			
			@Override
			public CommonExpression getRHS() {
				return rhs;
			}
			
			@Override
			public CommonExpression getLHS() {
				return lhs;
			}
		};
	}
	public static NeExpression ne(final CommonExpression lhs, final CommonExpression rhs){
		return new NeExpression() {
			
			@Override
			public CommonExpression getRHS() {
				return rhs;
			}
			
			@Override
			public CommonExpression getLHS() {
				return lhs;
			}
		};
	}
	public static AndExpression and(final CommonExpression lhs, final CommonExpression rhs){
		return new AndExpression() {
			
			@Override
			public CommonExpression getRHS() {
				return rhs;
			}
			
			@Override
			public CommonExpression getLHS() {
				return lhs;
			}
		};
	}
	public static OrExpression or(final CommonExpression lhs, final CommonExpression rhs){
		return new OrExpression() {
			
			@Override
			public CommonExpression getRHS() {
				return rhs;
			}
			
			@Override
			public CommonExpression getLHS() {
				return lhs;
			}
		};
	}
	public static LtExpression lt(final CommonExpression lhs, final CommonExpression rhs){
		return new LtExpression() {
			
			@Override
			public CommonExpression getRHS() {
				return rhs;
			}
			
			@Override
			public CommonExpression getLHS() {
				return lhs;
			}
		};
	}
	public static GtExpression gt(final CommonExpression lhs, final CommonExpression rhs){
		return new GtExpression() {
			
			@Override
			public CommonExpression getRHS() {
				return rhs;
			}
			
			@Override
			public CommonExpression getLHS() {
				return lhs;
			}
		};
	}
	public static LeExpression le(final CommonExpression lhs, final CommonExpression rhs){
		return new LeExpression() {
			
			@Override
			public CommonExpression getRHS() {
				return rhs;
			}
			
			@Override
			public CommonExpression getLHS() {
				return lhs;
			}
		};
	}
	public static GeExpression ge(final CommonExpression lhs, final CommonExpression rhs){
		return new GeExpression() {
			
			@Override
			public CommonExpression getRHS() {
				return rhs;
			}
			
			@Override
			public CommonExpression getLHS() {
				return lhs;
			}
		};
	}
	public static AddExpression add(final CommonExpression lhs, final CommonExpression rhs){
		return new AddExpression() {
			
			@Override
			public CommonExpression getRHS() {
				return rhs;
			}
			
			@Override
			public CommonExpression getLHS() {
				return lhs;
			}
		};
	}
	public static SubExpression sub(final CommonExpression lhs, final CommonExpression rhs){
		return new SubExpression() {
			
			@Override
			public CommonExpression getRHS() {
				return rhs;
			}
			
			@Override
			public CommonExpression getLHS() {
				return lhs;
			}
		};
	}
	public static MulExpression mul(final CommonExpression lhs, final CommonExpression rhs){
		return new MulExpression() {
			
			@Override
			public CommonExpression getRHS() {
				return rhs;
			}
			
			@Override
			public CommonExpression getLHS() {
				return lhs;
			}
		};
	}
	public static DivExpression div(final CommonExpression lhs, final CommonExpression rhs){
		return new DivExpression() {
			
			@Override
			public CommonExpression getRHS() {
				return rhs;
			}
			
			@Override
			public CommonExpression getLHS() {
				return lhs;
			}
		};
	}
	public static ModExpression mod(final CommonExpression lhs, final CommonExpression rhs){
		return new ModExpression() {
			
			@Override
			public CommonExpression getRHS() {
				return rhs;
			}
			
			@Override
			public CommonExpression getLHS() {
				return lhs;
			}
		};
	}
	
}
