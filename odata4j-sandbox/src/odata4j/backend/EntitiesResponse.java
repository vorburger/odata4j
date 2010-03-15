package odata4j.backend;

import java.util.List;

import odata4j.edm.EdmEntitySet;

public interface EntitiesResponse {

	public EdmEntitySet getEntitySet();
	public List<OEntity> getEntities();
}
