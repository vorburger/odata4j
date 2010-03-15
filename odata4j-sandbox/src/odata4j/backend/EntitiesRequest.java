package odata4j.backend;

public interface EntitiesRequest {

	public abstract String getEntityName();
	public abstract Integer getTop();
	public abstract Integer getSkip();
}
