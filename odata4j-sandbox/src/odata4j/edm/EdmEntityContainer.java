package odata4j.edm;

import java.util.List;

public class EdmEntityContainer {

	public final String name;
	public final boolean isDefault;
	public final List<EdmEntitySet> entitySets;
	public final List<EdmAssociationSet> associationSets;
	
	public EdmEntityContainer(String name, boolean isDefault, List<EdmEntitySet> entitySets, List<EdmAssociationSet> associationSets){
		this.name = name;
		this.isDefault = isDefault;
		this.entitySets = entitySets;
		this.associationSets = associationSets;
	}
}
