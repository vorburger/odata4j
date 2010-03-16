package odata4j.expression;

import junit.framework.Assert;

import org.junit.Test;

public class ExpressionTest {

	@Test
	public void testExpressionParsing(){
		
		t(Expression.null_(),"null");
		
		t(Expression.string(""),"''");
		t(Expression.string(""),"  ''    ");
		t(Expression.string("foo"),"'foo'");
		t(Expression.string("foo"),"   'foo' \n");
		t(Expression.string(" foo "),"' foo '");
		t(Expression.string("fo'o"),"'fo''o'");
		
		t(Expression.integral(2),"2");
		t(Expression.integral(222222222),"222222222");
		
		t(Expression.simpleProperty("LastName"),"LastName");
		t(Expression.simpleProperty("LastName2"),"   LastName2  ");
		
		t(Expression.eq(Expression.simpleProperty("LastName"), Expression.string("foo")),"LastName eq 'foo'");
		t(Expression.eq(Expression.simpleProperty("LastName"), Expression.string("foo")),"    LastName    eq     'foo'   ");
		t(Expression.eq(Expression.string("foo"),Expression.simpleProperty("LastName") ),"'foo' eq LastName");
		
		t(Expression.ne(Expression.simpleProperty("LastName"), Expression.string("foo")),"LastName ne 'foo'");
		
		CommonExpression exp = Expression.eq(Expression.simpleProperty("a"), Expression.integral(1));
		t(Expression.and(exp,exp),"a eq 1 and a eq 1");
		t(Expression.or(exp,exp),"a eq 1 or a eq 1");
		t(Expression.or(exp,Expression.and(exp,exp)),"a eq 1 or a eq 1 and a eq 1");
		t(Expression.or(Expression.and(exp,exp),exp),"a eq 1 and a eq 1 or a eq 1");
		
		t(Expression.lt(Expression.simpleProperty("a"), Expression.integral(1)),"a lt 1");
		t(Expression.gt(Expression.simpleProperty("a"), Expression.integral(1)),"a gt 1");
		t(Expression.le(Expression.simpleProperty("a"), Expression.integral(1)),"a le 1");
		t(Expression.ge(Expression.simpleProperty("a"), Expression.integral(1)),"a ge 1");
		
		t(Expression.add(Expression.integral(1), Expression.integral(2)),"1 add 2");
		t(Expression.sub(Expression.integral(1), Expression.integral(2)),"1 sub 2");
		t(Expression.mul(Expression.integral(1), Expression.integral(2)),"1 mul 2");
		t(Expression.div(Expression.integral(1), Expression.integral(2)),"1 div 2");
		t(Expression.mod(Expression.integral(1), Expression.integral(2)),"1 mod 2");
		
	}
	
	
	
	private void t(CommonExpression expected, String urlEncoded){
		CommonExpression actual = ExpressionParser.parse(urlEncoded);
		assertSame(expected,actual);
		
	}
	private void assertSame(CommonExpression expected, CommonExpression actual ){
		
		if (expected instanceof EqExpression){
			assertInstanceOf(EqExpression.class,actual);
			assertSame(((EqExpression)actual).getLHS(),((EqExpression)expected).getLHS());
			assertSame(((EqExpression)actual).getRHS(),((EqExpression)expected).getRHS());
		} else if (expected instanceof AndExpression){
			assertInstanceOf(AndExpression.class,actual);
			assertSame(((AndExpression)actual).getLHS(),((AndExpression)expected).getLHS());
			assertSame(((AndExpression)actual).getRHS(),((AndExpression)expected).getRHS());
		} else if (expected instanceof OrExpression){
			assertInstanceOf(OrExpression.class,actual);
			assertSame(((OrExpression)actual).getLHS(),((OrExpression)expected).getLHS());
			assertSame(((OrExpression)actual).getRHS(),((OrExpression)expected).getRHS());
		} else if (expected instanceof NeExpression){
			assertInstanceOf(NeExpression.class,actual);
			assertSame(((NeExpression)actual).getLHS(),((NeExpression)expected).getLHS());
			assertSame(((NeExpression)actual).getRHS(),((NeExpression)expected).getRHS());
		}
		else if (expected instanceof LtExpression){
			assertInstanceOf(LtExpression.class,actual);
			assertSame(((LtExpression)actual).getLHS(),((LtExpression)expected).getLHS());
			assertSame(((LtExpression)actual).getRHS(),((LtExpression)expected).getRHS());
		}
		else if (expected instanceof GtExpression){
			assertInstanceOf(GtExpression.class,actual);
			assertSame(((GtExpression)actual).getLHS(),((GtExpression)expected).getLHS());
			assertSame(((GtExpression)actual).getRHS(),((GtExpression)expected).getRHS());
		}
		else if (expected instanceof LeExpression){
			assertInstanceOf(LeExpression.class,actual);
			assertSame(((LeExpression)actual).getLHS(),((LeExpression)expected).getLHS());
			assertSame(((LeExpression)actual).getRHS(),((LeExpression)expected).getRHS());
		}
		else if (expected instanceof GeExpression){
			assertInstanceOf(GeExpression.class,actual);
			assertSame(((GeExpression)actual).getLHS(),((GeExpression)expected).getLHS());
			assertSame(((GeExpression)actual).getRHS(),((GeExpression)expected).getRHS());
		}
		else if (expected instanceof AddExpression){
			assertInstanceOf(AddExpression.class,actual);
			assertSame(((AddExpression)actual).getLHS(),((AddExpression)expected).getLHS());
			assertSame(((AddExpression)actual).getRHS(),((AddExpression)expected).getRHS());
		}
		else if (expected instanceof SubExpression){
			assertInstanceOf(SubExpression.class,actual);
			assertSame(((SubExpression)actual).getLHS(),((SubExpression)expected).getLHS());
			assertSame(((SubExpression)actual).getRHS(),((SubExpression)expected).getRHS());
		}
		else if (expected instanceof MulExpression){
			assertInstanceOf(MulExpression.class,actual);
			assertSame(((MulExpression)actual).getLHS(),((MulExpression)expected).getLHS());
			assertSame(((MulExpression)actual).getRHS(),((MulExpression)expected).getRHS());
		}
		else if (expected instanceof DivExpression){
			assertInstanceOf(DivExpression.class,actual);
			assertSame(((DivExpression)actual).getLHS(),((DivExpression)expected).getLHS());
			assertSame(((DivExpression)actual).getRHS(),((DivExpression)expected).getRHS());
		}
		else if (expected instanceof ModExpression){
			assertInstanceOf(ModExpression.class,actual);
			assertSame(((ModExpression)actual).getLHS(),((ModExpression)expected).getLHS());
			assertSame(((ModExpression)actual).getRHS(),((ModExpression)expected).getRHS());
		}
		else if (expected instanceof StringLiteral){
			assertInstanceOf(StringLiteral.class,actual);
			Assert.assertEquals(((StringLiteral)actual).getValue(), ((StringLiteral)expected).getValue());
		}
		else if (expected instanceof IntegralLiteral){
			assertInstanceOf(IntegralLiteral.class,actual);
			Assert.assertEquals(((IntegralLiteral)actual).getValue(), ((IntegralLiteral)expected).getValue());
		}
		else if (expected instanceof EntitySimpleProperty){
			assertInstanceOf(EntitySimpleProperty.class,actual);
			Assert.assertEquals(((EntitySimpleProperty)actual).getPropertyName(), ((EntitySimpleProperty)expected).getPropertyName());
		}
		else if (expected instanceof NullLiteral){
			assertInstanceOf(NullLiteral.class,actual);
		}
		else {
			Assert.fail("Unsupported: " + expected);
		}
	}
	
    private <T> void assertInstanceOf(Class<T> expected, Object actual){
    	Assert.assertTrue("e:"+expected.getSimpleName()+" a:" + actual.getClass().getSimpleName(),expected.isAssignableFrom(actual.getClass()));
    }
	

}
