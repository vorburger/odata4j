package odata4j.core;

public interface OModify<T> {


	public abstract OModify<T> property(OProperty<?> prop);
	public abstract boolean execute();
	public abstract OModify<T> nav(String navProperty, Object... key);

	
}
