package odata4j.core;

public interface OEntityRef<T> {

	T get();
	OEntityRef<T> nav(String navProperty,Object... key);
}
