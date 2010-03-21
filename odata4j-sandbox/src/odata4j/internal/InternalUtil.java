package odata4j.internal;

import java.util.List;
import java.util.UUID;

import odata4j.consumer.ODataClient.DataServicesAtomEntry;
import odata4j.core.OEntity;
import odata4j.core.OProperty;
import core4j.Enumerable;
import core4j.Func1;

public class InternalUtil {

	public static String keyString(Object[] key){
		String keyValue = Enumerable.create(key).select(new Func1<Object,String>(){
			public String apply(Object input) {
				String rt;
				if (input instanceof UUID){
					return "guid'" + input + "'";
				}
				else if (input instanceof String){
					return "'" + ((String)input).replace("'","''") + "'";

				}
				else{
					return input.toString();
				}
			}}).join(",");
		
		return "(" + keyValue + ")";
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
