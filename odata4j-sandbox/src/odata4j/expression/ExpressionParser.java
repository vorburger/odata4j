package odata4j.expression;

import java.util.ArrayList;
import java.util.List;

import core4j.Enumerable;
import core4j.Func2;

public class ExpressionParser {

	
	public static CommonExpression parse(String urlEncoded){
		
		 List<Token> tokens =  tokenize(urlEncoded);
		 
		//System.out.println("["+urlEncoded + "] -> " +Enumerable.create(tokens).join("") );
		CommonExpression rt = readExpression( tokens);
		
		System.out.println("["+urlEncoded + "] -> " +Enumerable.create(tokens).join("") + " -> " + dump(rt));
		
		return rt;
		
		
	}
	
	private static String dump(CommonExpression expected){
		if (expected instanceof EqExpression){
			return String.format("eq(%s,%s)",dump(((EqExpression)expected).getLHS()),dump(((EqExpression)expected).getRHS()));
		} else if (expected instanceof AndExpression){
			return String.format("and(%s,%s)",dump(((AndExpression)expected).getLHS()),dump(((AndExpression)expected).getRHS()));
		} else if (expected instanceof OrExpression){
			return String.format("or(%s,%s)",dump(((OrExpression)expected).getLHS()),dump(((OrExpression)expected).getRHS()));
		}else if (expected instanceof NeExpression){
			return String.format("ne(%s,%s)",dump(((NeExpression)expected).getLHS()),dump(((NeExpression)expected).getRHS()));
		}
		else if (expected instanceof LtExpression){
			return String.format("lt(%s,%s)",dump(((LtExpression)expected).getLHS()),dump(((LtExpression)expected).getRHS()));
		} 
		else if (expected instanceof GtExpression){
			return String.format("gt(%s,%s)",dump(((GtExpression)expected).getLHS()),dump(((GtExpression)expected).getRHS()));
		} 
		else if (expected instanceof LeExpression){
			return String.format("le(%s,%s)",dump(((LeExpression)expected).getLHS()),dump(((LeExpression)expected).getRHS()));
		} 
		else if (expected instanceof GeExpression){
			return String.format("ge(%s,%s)",dump(((GeExpression)expected).getLHS()),dump(((GeExpression)expected).getRHS()));
		} 
		else if (expected instanceof AddExpression){
			return String.format("add(%s,%s)",dump(((AddExpression)expected).getLHS()),dump(((AddExpression)expected).getRHS()));
		} 
		else if (expected instanceof SubExpression){
			return String.format("sub(%s,%s)",dump(((SubExpression)expected).getLHS()),dump(((SubExpression)expected).getRHS()));
		} 
		else if (expected instanceof DivExpression){
			return String.format("div(%s,%s)",dump(((DivExpression)expected).getLHS()),dump(((DivExpression)expected).getRHS()));
		} 
		else if (expected instanceof MulExpression){
			return String.format("mul(%s,%s)",dump(((MulExpression)expected).getLHS()),dump(((MulExpression)expected).getRHS()));
		} 
		else if (expected instanceof ModExpression){
			return String.format("mod(%s,%s)",dump(((ModExpression)expected).getLHS()),dump(((ModExpression)expected).getRHS()));
		} 
		else if (expected instanceof StringLiteral){
			return String.format("string(%s)",((StringLiteral)expected).getValue());
		}
		else if (expected instanceof IntegralLiteral){
			return String.format("integral(%s)",((IntegralLiteral)expected).getValue());
		}
		else if (expected instanceof EntitySimpleProperty){
			return String.format("simpleProperty(%s)",((EntitySimpleProperty)expected).getPropertyName());
		}
		else if (expected instanceof NullLiteral){
			return "null";
		}
		else {
			throw new RuntimeException("Unsupported: " + expected);
		}
	}
	
	private static CommonExpression processOperator(List<Token> tokens, String op, Func2<CommonExpression,CommonExpression,CommonExpression> fn){
		
		int ts = tokens.size();
		for(int i=0;i<ts;i++){
			Token t = tokens.get(i);
			
			if (i<ts-2){
				if (t.type == TokenType.WHITESPACE 
						&& tokens.get(i+2).type == TokenType.WHITESPACE 
						&& tokens.get(i+1).type == TokenType.WORD){
					
					Token wordToken = tokens.get(i+1);
					if (wordToken.value.equals(op)){
						final CommonExpression lhs = readExpression(tokens.subList(0,i));
						final CommonExpression rhs = readExpression(tokens.subList(i+3,ts));
						return fn.apply(lhs, rhs);
					}
				}
				 
			}
		}
		return null;
	}
	
	private static CommonExpression readExpression(List<Token> tokens){
		
		CommonExpression rt = null;
		
		
		// Conditional OR: or
		rt = processOperator(tokens,"or",new Func2<CommonExpression,CommonExpression,CommonExpression>(){
			public CommonExpression apply(CommonExpression lhs,CommonExpression rhs) {
				return Expression.or(lhs, rhs);
			}});
		if (rt!=null) return rt;
		
		// Conditional AND: and
		rt = processOperator(tokens,"and",new Func2<CommonExpression,CommonExpression,CommonExpression>(){
			public CommonExpression apply(CommonExpression lhs,CommonExpression rhs) {
				return Expression.and(lhs, rhs);
			}});
		if (rt!=null) return rt;
		
		// Equality: eq ne
		rt = processOperator(tokens,"eq",new Func2<CommonExpression,CommonExpression,CommonExpression>(){
			public CommonExpression apply(CommonExpression lhs,CommonExpression rhs) {
				return Expression.eq(lhs, rhs);
			}});
		if (rt!=null) return rt;
		rt = processOperator(tokens,"ne",new Func2<CommonExpression,CommonExpression,CommonExpression>(){
			public CommonExpression apply(CommonExpression lhs,CommonExpression rhs) {
				return Expression.ne(lhs, rhs);
			}});
		if (rt!=null) return rt;
		
		// Relational and type testing: lt, gt, le, ge
		// TODO isof(T) , isof(x,T) 
		rt = processOperator(tokens,"lt",new Func2<CommonExpression,CommonExpression,CommonExpression>(){
			public CommonExpression apply(CommonExpression lhs,CommonExpression rhs) {
				return Expression.lt(lhs, rhs);
			}});
		if (rt!=null) return rt;
		rt = processOperator(tokens,"gt",new Func2<CommonExpression,CommonExpression,CommonExpression>(){
			public CommonExpression apply(CommonExpression lhs,CommonExpression rhs) {
				return Expression.gt(lhs, rhs);
			}});
		if (rt!=null) return rt;
		rt = processOperator(tokens,"le",new Func2<CommonExpression,CommonExpression,CommonExpression>(){
			public CommonExpression apply(CommonExpression lhs,CommonExpression rhs) {
				return Expression.le(lhs, rhs);
			}});
		if (rt!=null) return rt;
		rt = processOperator(tokens,"ge",new Func2<CommonExpression,CommonExpression,CommonExpression>(){
			public CommonExpression apply(CommonExpression lhs,CommonExpression rhs) {
				return Expression.ge(lhs, rhs);
			}});
		if (rt!=null) return rt;
		
		
		// Additive: add, sub
		rt = processOperator(tokens,"add",new Func2<CommonExpression,CommonExpression,CommonExpression>(){
			public CommonExpression apply(CommonExpression lhs,CommonExpression rhs) {
				return Expression.add(lhs, rhs);
			}});
		if (rt!=null) return rt;
		rt = processOperator(tokens,"sub",new Func2<CommonExpression,CommonExpression,CommonExpression>(){
			public CommonExpression apply(CommonExpression lhs,CommonExpression rhs) {
				return Expression.sub(lhs, rhs);
			}});
		if (rt!=null) return rt;
		
		
		// Multiplicative: mul, div, mod
		rt = processOperator(tokens,"mul",new Func2<CommonExpression,CommonExpression,CommonExpression>(){
			public CommonExpression apply(CommonExpression lhs,CommonExpression rhs) {
				return Expression.mul(lhs, rhs);
			}});
		if (rt!=null) return rt;
		rt = processOperator(tokens,"div",new Func2<CommonExpression,CommonExpression,CommonExpression>(){
			public CommonExpression apply(CommonExpression lhs,CommonExpression rhs) {
				return Expression.div(lhs, rhs);
			}});
		if (rt!=null) return rt;
		rt = processOperator(tokens,"mod",new Func2<CommonExpression,CommonExpression,CommonExpression>(){
			public CommonExpression apply(CommonExpression lhs,CommonExpression rhs) {
				return Expression.mod(lhs, rhs);
			}});
		if (rt!=null) return rt;
		
		
		// single token expression
		
		List<Token> single = trimWhitespace(tokens);
		if (single.size() != 1)
			throw new RuntimeException("Unexpected");
		
		final Token token = single.get(0);
		if (token.type == TokenType.QUOTED_STRING) {
			return Expression.string(token.value.substring(1,token.value.length()-1).replace("''","'"));
		} else if (token.type == TokenType.WORD){
			if (token.value.equals("null"))
				return Expression.null_();
			return Expression.simpleProperty(token.value);
		} else if (token.type == TokenType.INTEGRAL){
			Integer value= Integer.parseInt(token.value);
			return Expression.integral(value);
		} else
			throw new RuntimeException("Unexpected");
		
		
	}
	
	private static List<Token> trimWhitespace(List<Token> tokens){
		int start = 0;
		while (tokens.get(start).type==TokenType.WHITESPACE)
			start++;
		int end = tokens.size()-1;
		while (tokens.get(end).type==TokenType.WHITESPACE)
			end--;
		return tokens.subList(start,end+1);
		
	}
	

	

	
	private static List<Token> tokenize(String urlEncoded){
		 List<Token> rt = new ArrayList<Token>();
		int current = 0;
		int end = 0;
		
		while(true) {
			if (current == urlEncoded.length())
				return rt;
			char c = urlEncoded.charAt(current);
			if (Character.isWhitespace(c)) {
				end = readWhitespace(urlEncoded,current);
				rt.add(new Token(TokenType.WHITESPACE,urlEncoded.substring(current,end)));
				current = end;
			} else if (c == '\''){
				end = readQuotedString(urlEncoded,current+1);
				rt.add(new Token(TokenType.QUOTED_STRING,urlEncoded.substring(current,end)));
				current = end;
			} else if (Character.isLetter(c)) {
				end = readWord(urlEncoded,current+1);
				rt.add(new Token(TokenType.WORD,urlEncoded.substring(current,end)));
				current = end;
			} else if (Character.isDigit(c)) {
				end = readDigits(urlEncoded,current+1);
				rt.add(new Token(TokenType.INTEGRAL,urlEncoded.substring(current,end)));
				current = end;
			} else {
				throw new RuntimeException("Unable to tokenize: " + urlEncoded);
			}
		}
		
		
	}
	
	
	private static int readDigits(String urlEncoded, int start){
		int rt = start;
		while(rt<urlEncoded.length() && Character.isDigit(urlEncoded.charAt(rt)))
			rt++;
		return rt;
	}
	private static int readWord(String urlEncoded, int start){
		int rt = start;
		while(rt<urlEncoded.length() && Character.isLetterOrDigit(urlEncoded.charAt(rt)))
			rt++;
		return rt;
	}
	
	private static int readQuotedString(String urlEncoded, int start){
		int rt = start;
		
		while (urlEncoded.charAt(rt) != '\'' || (rt<urlEncoded.length()-1 && urlEncoded.charAt(rt+1) == '\'')) {
			if (urlEncoded.charAt(rt) != '\'')
				rt++;
			else
				rt += 2;
		}
		rt++;
		return rt;
	}
	
	private static int readWhitespace(String urlEncoded, int start){
		int rt = start;
		while(rt<urlEncoded.length() && Character.isWhitespace(urlEncoded.charAt(rt)))
			rt++;
		return rt;
	}
	
	private static enum TokenType {
		UNKNOWN,
		WHITESPACE,
		QUOTED_STRING,
		WORD,
		INTEGRAL,
		;
	}
	
	private static class Token {
		public final TokenType type;
		public final String value;
		public Token(TokenType type, String value) {
			this.type = type;
			this.value = value;
		}
		@Override
		public String toString() {
			return "["+value + "]";
		}
	}
}
