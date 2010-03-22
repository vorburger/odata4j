package odata4j.internal;

import java.util.List;
import java.util.UUID;

import odata4j.consumer.ODataClient.DataServicesAtomEntry;
import odata4j.core.OEntity;
import odata4j.core.OProperty;
import odata4j.edm.EdmType;
import core4j.Enumerable;
import core4j.Func1;

public class InternalUtil {

	public static String keyString(Object[] key){
		
		String keyValue;
		if (key.length==1){
			keyValue = keyString(key[0],false);
		} else {
			keyValue = Enumerable.create(key).select(new Func1<Object,String>(){
				public String apply(Object input) {
					return keyString(input,true);
				}}).join(",");
		}
		
		return "(" + keyValue + ")";
	}
	
	private static String keyString(Object key, boolean includePropName){
		if (key instanceof UUID){
			return "guid'" + key + "'";
		}
		else if (key instanceof String){
			return "'" + ((String)key).replace("'","''") + "'";

		}
		else if (key instanceof OProperty<?>){
			OProperty<?> oprop = (OProperty<?>)key;
			String value = oprop.getValue().toString();
			if (oprop.getType().equals(EdmType.STRING)){
				value = "'" + value.replace("'", "''") +  "'";
			}
			if (includePropName)
				return oprop.getName() + "="+ value;
			else
				return value;
		}
		else{
			return key.toString();
		}
	}
	

	
	public static OEntity toEntity(DataServicesAtomEntry dsae){
		
		final List<OProperty<?>> properties = dsae.properties;
		
		return new OEntity(){

			
			@Override
			public String toString() {
				return "OEntity[" + Enumerable.create(getProperties()).join(",") + "]";
			}
			@Override
			public List<OProperty<?>> getKeyProperties() {
				throw new UnsupportedOperationException();
			}

			@Override
			public List<OProperty<?>> getProperties() {
				return properties;
			}};
			
	}
	

}
