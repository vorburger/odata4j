package odata4j.edm;

import java.util.List;

public class EdmDataServices {

	public final List<EdmSchema> schemas;
	
	public EdmDataServices(List<EdmSchema> schemas){
		this.schemas = schemas;
	}
	
	public EdmEntitySet getEdmEntitySet(String entitySetName) {
		for (EdmSchema schema : this.schemas) {
			for (EdmEntityContainer eec : schema.entityContainers) {
				for (EdmEntitySet ees : eec.entitySets) {
					if (ees.name.equals(entitySetName))
						return ees;
				}
			}
		}
		throw new RuntimeException("EdmEntitySet " + entitySetName + " not found");
	}
	
}
