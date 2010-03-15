package odata4j.jpa;

import java.lang.reflect.Field;
import java.lang.reflect.Member;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.metamodel.Attribute;
import javax.persistence.metamodel.EntityType;


import core4j.Enumerable;
import core4j.Func1;
import core4j.Predicate1;

import odata4j.backend.EntitiesRequest;
import odata4j.backend.EntitiesResponse;
import odata4j.backend.EntityRequest;
import odata4j.backend.EntityResponse;
import odata4j.backend.ODataBackend;
import odata4j.backend.OEntity;
import odata4j.backend.OProperty;
import odata4j.edm.EdmDataServices;
import odata4j.edm.EdmEntityContainer;
import odata4j.edm.EdmEntitySet;
import odata4j.edm.EdmEntityType;
import odata4j.edm.EdmProperty;
import odata4j.edm.EdmSchema;
import odata4j.edm.EdmType;

public class JPABackend implements ODataBackend {

	private final EntityManagerFactory emf;
	private final String namespace;
	private final EdmDataServices metadata;
	
	public JPABackend(EntityManagerFactory emf, String namespace){
		this.emf = emf;
		this.namespace = namespace;
		
		this.metadata = EdmGenerator.buildEdm(emf, namespace);
	}
	@Override
	public EdmDataServices getMetadata() {
		 return metadata;
	}
	
	@Override
	public EntityResponse getEntity(EntityRequest request) {
		
		return common(request.getEntityName(),request.getEntityKey(),null,null,new Func1<Context,EntityResponse>(){
			public EntityResponse apply(Context input) {
				return getEntity(input);
			}});
		
	}
	
	
	@Override
	public EntitiesResponse getEntities(EntitiesRequest request) {
		
		return common(request.getEntityName(),null,request.getTop(),request.getSkip(),new Func1<Context,EntitiesResponse>(){
			public EntitiesResponse apply(Context input) {
				return getEntities(input);
			}});
		
	}
	
	private class Context {
		EdmDataServices metadata;
		EdmEntitySet ees;
		EntityType<?> entityType;
		String keyPropertyName;
		EntityManager em;
		Object entityKey;
		Integer top;
		Integer skip;
	}
	
	
	private EntityResponse getEntity(final Context context){
		
		Object jpaEntity = context.em.find(context.entityType.getJavaType(), context.entityKey);
		if (jpaEntity == null)
			throw new RuntimeException(context.entityType.getJavaType()+" not found with key " + context.entityKey);
		
		final OEntity entity = makeEntity(context,jpaEntity);
		return new EntityResponse(){

			@Override
			public OEntity getEntity() {
				return entity;
			}

			@Override
			public EdmEntitySet getEntitySet() {
				return context.ees;
			}};
		
		
	}
	
	private OEntity makeEntity(final Context context, final Object jpaEntity){
		return new OEntity(){
			public List<OProperty<?>> getProperties() {
				return jpaEntityToOProperties(context.ees, context.entityType, jpaEntity);
			}

			@Override
			public List<OProperty<?>> getKeyProperties() {
				return Enumerable.create(getProperties()).where(new Predicate1<OProperty<?>>(){
					public boolean apply(OProperty<?> input) {
						return input.getName().equals(context.keyPropertyName);
					}}).toList();
			}};
	}
	
	
	private EntitiesResponse getEntities(final Context context){
		
		Enumerable<Object> entityObjects =enumDynamicEntities(context.em,context.entityType.getJavaType(),context.top,context.skip);
		final List<OEntity> entities = entityObjects.select(new Func1<Object,OEntity>(){
			public OEntity apply(final Object input) {
				return makeEntity(context,input);
			}}).toList();
		
		
		return new EntitiesResponse(){
			public List<OEntity> getEntities() {
				return entities;
			}

			@Override
			public EdmEntitySet getEntitySet() {
				return context.ees;
			}};
	}
	
	private <T> T common(final String entityName, Object entityKey, Integer top, Integer skip,Func1<Context,T> fn){
		Context context = new Context();
		
		context.em = emf.createEntityManager();
		try {
			context.metadata = getMetadata();
			context.ees = findEdmEntitySet(metadata, entityName);
			context.entityType = findEntityType(context.em,entityName);
			context.keyPropertyName = context.ees.type.key;
			context.entityKey = entityKey;
			context.top = top;
			context.skip = skip;
			return fn.apply(context);
			
		} finally {
			context.em.close();
		}
	}
	
	private static List<OProperty<?>> jpaEntityToOProperties(EdmEntitySet ees, EntityType<?> entityType, Object jpaEntity){
		List<OProperty<?>> rt = new ArrayList<OProperty<?>>();
		
		try {
			for(EdmProperty ep : ees.type.properties){
				
				Attribute<?,?> att = entityType.getAttribute(ep.name);
				Member member = att.getJavaMember();
				
				if (!(member instanceof Field))
					throw new UnsupportedOperationException("Implement member" + member);
				Field field = (Field)member;
				Object value = field.get(jpaEntity);
				if (ep.type == EdmType.STRING){
					String sValue = (String)value;
					rt.add(new PropertyImpl<String>(ep.name,ep.type,sValue));
				} else if (ep.type == EdmType.INT32){
					Integer iValue = (Integer)value;
					rt.add(new PropertyImpl<Integer>(ep.name,ep.type,iValue));
				}else if (ep.type == EdmType.BOOLEAN){
					Boolean bValue = (Boolean)value;
					rt.add(new PropertyImpl<Boolean>(ep.name,ep.type,bValue));
				}  else if (ep.type == EdmType.INT16){
					Short sValue = (Short)value;
					rt.add(new PropertyImpl<Short>(ep.name,ep.type,sValue));
				}else if (ep.type == EdmType.DECIMAL){
					BigDecimal dValue = (BigDecimal)value;
					rt.add(new PropertyImpl<BigDecimal>(ep.name,ep.type,dValue));
				}else if (ep.type == EdmType.DATETIME){
					Date dValue = (Date)value;
					rt.add(new PropertyImpl<Date>(ep.name,ep.type,dValue));
				}else if (ep.type == EdmType.BINARY){
					byte[] bValue = (byte[])value;
					rt.add(new PropertyImpl<byte[]>(ep.name,ep.type,bValue));
				} else {
					throw new UnsupportedOperationException("Implement " + ep.type);
				}
				
				
			}
		} catch(Exception e){
			throw new RuntimeException(e);
		}
		
		
		return rt;
	}
	
	
	private static class PropertyImpl<T> implements OProperty<T> {

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
		
	}
	
	private static EdmEntitySet findEdmEntitySet(EdmDataServices metadata, String name){
		for(EdmSchema schema : metadata.schemas){
			for(EdmEntityContainer eec : schema.entityContainers){
				for(EdmEntitySet ees : eec.entitySets){
					if (ees.name.equals(name))
						return ees;
				}
			}
		}
		throw new RuntimeException("EdmEntitySet " + name + " not found");
	}
	
	
	
	private static EntityType<?> findEntityType(EntityManager em, String entityName){
		for(EntityType<?> et : em.getMetamodel().getEntities()){
			if (et.getName().equals(entityName))
				return et;
		}
		throw new RuntimeException("Entity type " + entityName + " not found");
	}
	
	
	public static Enumerable<Object> enumDynamicEntities(EntityManager em,Class<?> clazz, Integer top, Integer skip){
		CriteriaQuery<Object> cq = em.getCriteriaBuilder().createQuery();
		
		cq = cq.select(cq.from(em.getMetamodel().entity(clazz)));
		TypedQuery<Object> tq = em.createQuery(cq);
		if (top != null) {
			if (top.equals(0)){
				return Enumerable.empty(Object.class);
			}
			tq = tq.setMaxResults(top);
		}
		if (skip != null){
			tq = tq.setFirstResult(skip);
		}
		
		List<Object> results = tq.getResultList();
		return Enumerable.create(results);
	}
	
	


}
