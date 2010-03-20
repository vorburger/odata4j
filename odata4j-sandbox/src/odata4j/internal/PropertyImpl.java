package odata4j.internal;

import odata4j.core.OProperty;
import odata4j.edm.EdmType;

public class PropertyImpl<T> implements OProperty<T> {

	private final String name;
	private final EdmType type;
	private final T value;
	
	public PropertyImpl(String name, EdmType type, T value){
		this.name = name;
		this.type = type;
		this.value = value;
	}
	@Override
	public String getName() {
		return name;
	}

	@Override
	public EdmType getType() {
		return type;
	}

	@Override
	public T getValue() {
		return value;
	}
	
	@Override
	public String toString() {
		return String.format("OProperty[%s,%s,%s]",name,type,value);
	}
	
}
