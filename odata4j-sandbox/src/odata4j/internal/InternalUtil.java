package odata4j.internal;

import java.lang.reflect.Field;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import odata4j.consumer.ODataClient.DataServicesAtomEntry;
import odata4j.core.OEntity;
import odata4j.core.OProperty;
import odata4j.edm.EdmType;
import core4j.Enumerable;
import core4j.Func1;
import core4j.Funcs;
import core4j.ThrowingFunc1;

public class InternalUtil {

	
	public static String reflectionToString(final Object obj){
		StringBuilder rt = new StringBuilder();
		Class<?> objClass = obj.getClass();
		rt.append(objClass.getSimpleName());
		rt.append('[');
		
		String content = Enumerable.create(objClass.getFields()).select(Funcs.wrap(new ThrowingFunc1<Field,String>(){
			public String apply(Field f) throws Exception{
				Object fValue = f.get(obj);
				return f.getName() + ":" + fValue;
			}})).join(",");
		
		rt.append(content);
		
		rt.append(']');
		return rt.toString();
	}
	
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
	
	private static Set<Class> integralTypes = Enumerable.create(
			Integer.class,Integer.TYPE,Long.class,Long.TYPE,Short.class,Short.TYPE
			).cast(Class.class).toSet();
	
	private static String keyString(Object key, boolean includePropName){
		if (key instanceof UUID){
			return "guid'" + key + "'";
		}
		else if (key instanceof String){
			return "'" + ((String)key).replace("'","''") + "'";

		}
		else if (integralTypes.contains(key.getClass())){
			return key.toString();
		}
		else if (key instanceof OProperty<?>){
			OProperty<?> oprop = (OProperty<?>)key;
			String value = keyString(oprop.getValue(),false);
			
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
