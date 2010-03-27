package odata4j.producer;

import java.util.List;

import odata4j.core.OProperty;

public interface CreateEntityRequest {

	public abstract String getEntityName();
	List<OProperty<?>> getProperties();

}
