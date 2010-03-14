package odata4j.jpa.eclipselink;

import java.util.HashMap;
import java.util.Map;

import javax.persistence.metamodel.SingularAttribute;

import odata4j.edm.EdmType;

import org.eclipse.persistence.internal.helper.DatabaseField;
import org.eclipse.persistence.internal.jpa.metamodel.AttributeImpl;
import org.eclipse.persistence.mappings.DatabaseMapping;

import core4j.CoreUtils;

public class EclipseLink {

	public static Map<String,Object> getPropertyInfo(SingularAttribute sa, EdmType type){
		
		Map<String,Object> rt = new HashMap<String,Object>();
		 AttributeImpl ai = (AttributeImpl)sa;
		  DatabaseMapping dm = CoreUtils.getFieldValue(ai,"mapping",DatabaseMapping.class);
		 
		  DatabaseField df = dm.getField();
	
		  if (df!=null && type == EdmType.STRING){
			  rt.put("MaxLength",df.getLength());
		  }
		  return rt;
	}
}
