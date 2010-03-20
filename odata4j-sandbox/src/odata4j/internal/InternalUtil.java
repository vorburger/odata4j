package odata4j.internal;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import odata4j.consumer.ODataClient.ComplexEntityProperty;
import odata4j.consumer.ODataClient.DataServicesAtomEntry;
import odata4j.consumer.ODataClient.EntityProperty;
import odata4j.consumer.ODataClient.StringEntityProperty;
import odata4j.core.OEntity;
import odata4j.core.OProperty;
import odata4j.edm.EdmType;
import odata4j.expression.ExpressionParser;

import org.joda.time.LocalDateTime;

import core4j.Enumerable;

public class InternalUtil {

	
	private static List<OProperty<?>> toOProperties(Iterable<EntityProperty> props){
		final List<OProperty<?>> properties = new ArrayList<OProperty<?>>();
		
		for(EntityProperty ep : props){
			
			if(ep instanceof StringEntityProperty) {
				String value = ((StringEntityProperty)ep).value;
				
				if (EdmType.GUID.toTypeString().equals(ep.type)){
					UUID uValue = UUID.fromString(value);
					properties.add(new PropertyImpl<UUID>(ep.name,EdmType.GUID,uValue));
				} else if (EdmType.BOOLEAN.toTypeString().equals(ep.type)){
					boolean bValue = Boolean.parseBoolean(value);
					properties.add(new PropertyImpl<Boolean>(ep.name,EdmType.BOOLEAN,bValue));
				} else if (EdmType.INT32.toTypeString().equals(ep.type)){
					int iValue = Integer.parseInt(value);
					properties.add(new PropertyImpl<Integer>(ep.name,EdmType.INT32,iValue));
				} else if (EdmType.DOUBLE.toTypeString().equals(ep.type)){
					double dValue = Double.parseDouble(value);
					properties.add(new PropertyImpl<Double>(ep.name,EdmType.DOUBLE,dValue));
				} else if (EdmType.DATETIME.toTypeString().equals(ep.type)){
					LocalDateTime dValue = value==null?null:new LocalDateTime(ExpressionParser.DATETIME_FORMATTER.parseDateTime(value));
					properties.add(new PropertyImpl<LocalDateTime>(ep.name,EdmType.DATETIME,dValue));
				} else {
					properties.add(new PropertyImpl<String>(ep.name,EdmType.STRING,value));
				}
			} else if (ep instanceof ComplexEntityProperty) {
				
				List<OProperty<?>> subprops = toOProperties( ((ComplexEntityProperty)ep).properties);
				properties.add(new PropertyImpl<List<OProperty<?>>>(ep.name,null,subprops));
			}
		}
		return properties;
	}
	
	public static OEntity toEntity(DataServicesAtomEntry dsae){
		
		final List<OProperty<?>> properties = toOProperties(dsae.properties);
		
		
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
