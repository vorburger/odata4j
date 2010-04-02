package odata4j.producer;

import java.util.List;

import odata4j.core.OEntity;
import odata4j.edm.EdmEntitySet;

public interface EntitiesResponse {

	public EdmEntitySet getEntitySet();
	public List<OEntity> getEntities();
	public Integer getInlineCount();
}
