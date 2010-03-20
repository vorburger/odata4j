package odata4j.edm;

public class EdmEntitySet {

	public final String name;
	public final EdmEntityType type;
	
	public EdmEntitySet(String name, EdmEntityType type){
		this.name = name;
		this.type = type;
	}
}