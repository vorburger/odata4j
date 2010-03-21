package odata4j.core;

public interface OCreate<T> {

	public abstract OCreate<T> property(OProperty<?> prop);
	public abstract T execute();
	
}
