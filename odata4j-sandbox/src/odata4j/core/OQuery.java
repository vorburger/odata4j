package odata4j.core;

import core4j.Enumerable;

public interface OQuery<T> {

	public abstract Enumerable<T> get();
	public abstract OQuery<T> top(int top);
	public abstract OQuery<T> skip(int skip);
	public abstract OQuery<T> orderBy(String orderBy);
	public abstract OQuery<T> filter(String filter);
	public abstract OQuery<T> select(String select);
}
