package odata4j.backend;

import odata4j.edm.EdmEntitySet;

public interface EntityResponse {

	public EdmEntitySet getEntitySet();
	public OEntity getEntity();
}
