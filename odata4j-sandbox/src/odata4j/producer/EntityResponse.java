package odata4j.producer;

import odata4j.edm.EdmEntitySet;

public interface EntityResponse {

	public EdmEntitySet getEntitySet();
	public OEntity getEntity();
}
