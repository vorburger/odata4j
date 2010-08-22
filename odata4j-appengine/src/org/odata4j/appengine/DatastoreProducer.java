package org.odata4j.appengine;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Set;

import org.joda.time.LocalDateTime;
import org.odata4j.core.ODataConstants;
import org.odata4j.core.OEntities;
import org.odata4j.core.OEntity;
import org.odata4j.core.OLink;
import org.odata4j.core.OProperties;
import org.odata4j.core.OProperty;
import org.odata4j.edm.EdmDataServices;
import org.odata4j.edm.EdmEntityContainer;
import org.odata4j.edm.EdmEntitySet;
import org.odata4j.edm.EdmEntityType;
import org.odata4j.edm.EdmProperty;
import org.odata4j.edm.EdmSchema;
import org.odata4j.edm.EdmType;
import org.odata4j.expression.*;
import org.odata4j.producer.EntitiesResponse;
import org.odata4j.producer.EntityResponse;
import org.odata4j.producer.InlineCount;
import org.odata4j.producer.ODataProducer;
import org.odata4j.producer.QueryInfo;

import com.google.appengine.api.datastore.DataTypeUtils;
import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.EntityNotFoundException;
import com.google.appengine.api.datastore.FetchOptions;
import com.google.appengine.api.datastore.KeyFactory;
import com.google.appengine.api.datastore.PreparedQuery;
import com.google.appengine.api.datastore.Query;
import com.google.appengine.api.datastore.ShortBlob;
import com.google.appengine.api.datastore.Text;
import com.google.appengine.api.datastore.Query.FilterOperator;
import com.google.appengine.api.datastore.Query.SortDirection;
import com.sun.jersey.api.NotFoundException;

import org.core4j.Enumerable;
import org.core4j.Func1;

public class DatastoreProducer implements ODataProducer {

    public static final String ID_PROPNAME = "id";
    private static final String CONTAINER_NAME = "Container";

    private final EdmDataServices metadata;
    private final String namespace;
    private final DatastoreService datastore;

    public DatastoreProducer(String namespace, List<String> kinds) {
        this.namespace = namespace;
        this.metadata = buildMetadata(kinds);
        this.datastore = DatastoreServiceFactory.getDatastoreService();
    }

    @Override
    public EdmDataServices getMetadata() {
        return metadata;
    }

    private EdmDataServices buildMetadata(List<String> kinds) {

        List<EdmSchema> schemas = new ArrayList<EdmSchema>();
        List<EdmEntityContainer> containers = new ArrayList<EdmEntityContainer>();
        List<EdmEntitySet> entitySets = new ArrayList<EdmEntitySet>();
        List<EdmEntityType> entityTypes = new ArrayList<EdmEntityType>();

        List<EdmProperty> properties = new ArrayList<EdmProperty>();
        properties.add(new EdmProperty(ID_PROPNAME, EdmType.INT64, false));

        for(String kind : kinds) {
            EdmEntityType eet = new EdmEntityType(namespace, kind,null,Enumerable.create( ID_PROPNAME).toList(), properties, null);
            EdmEntitySet ees = new EdmEntitySet(kind, eet);
            entitySets.add(ees);
            entityTypes.add(eet);
        }

        EdmEntityContainer container = new EdmEntityContainer(CONTAINER_NAME, true, null, entitySets, null,null);
        containers.add(container);

        EdmSchema schema = new EdmSchema(namespace, entityTypes, null, null, containers);
        schemas.add(schema);
        EdmDataServices rt = new EdmDataServices(ODataConstants.DATA_SERVICE_VERSION, schemas);
        return rt;
    }

    @Override
    public void close() {
        // noop
    }

    @Override
    public EntityResponse getEntity(String entitySetName, Object entityKey) {
        final EdmEntitySet ees = metadata.getEdmEntitySet(entitySetName);
        Entity e = findEntity(entitySetName, entityKey);
        if (e == null)
            throw new NotFoundException();

        final OEntity entity = toOEntity(e);
        return new EntityResponse() {

            @Override
            public EdmEntitySet getEntitySet() {
                return ees;
            }

            @Override
            public OEntity getEntity() {
                return entity;
            }
        };

    }

    @Override
    public EntitiesResponse getEntities(String entitySetName, QueryInfo queryInfo) {

        final EdmEntitySet ees = metadata.getEdmEntitySet(entitySetName);
        Query q = new Query(entitySetName);
        if (queryInfo.filter != null)
            applyFilter(q, queryInfo.filter);
        if (queryInfo.orderBy != null && queryInfo.orderBy.size() > 0)
            applySort(q, queryInfo.orderBy);
        PreparedQuery pq = datastore.prepare(q);

        final Integer inlineCount = queryInfo.inlineCount == InlineCount.ALLPAGES ? pq.countEntities() : null;

        FetchOptions options = null;
        if (queryInfo.top != null)
            options = FetchOptions.Builder.withLimit(queryInfo.top);
        if (queryInfo.skip != null)
            options = options == null ? FetchOptions.Builder.withOffset(queryInfo.skip) : options.offset(queryInfo.skip);

        Iterable<Entity> iter = options == null ? pq.asIterable() : pq.asIterable(options);

        final List<OEntity> entities = Enumerable.create(iter).select(new Func1<Entity, OEntity>() {
            public OEntity apply(Entity input) {
                return toOEntity(input);
            }
        }).toList();

        return new EntitiesResponse() {

            @Override
            public List<OEntity> getEntities() {
                return entities;
            }

            @Override
            public EdmEntitySet getEntitySet() {
                return ees;
            }

            @Override
            public Integer getInlineCount() {
                return inlineCount;
            }
            
            @Override
            public String getSkipToken() {
               return null;
            }
        };

    }

    @Override
    public EntityResponse createEntity(String entitySetName, List<OProperty<?>> properties) {
        final EdmEntitySet ees = metadata.getEdmEntitySet(entitySetName);
        String kind = ees.type.name;

        Entity e = new Entity(kind);

        applyProperties(e, properties);

        datastore.put(e);

        final OEntity entity = toOEntity(e);
        return new EntityResponse() {

            @Override
            public OEntity getEntity() {
                return entity;
            }

            @Override
            public EdmEntitySet getEntitySet() {
                return ees;
            }
        };
    }

    @Override
    public void deleteEntity(String entitySetName, Object entityKey) {
        final EdmEntitySet ees = metadata.getEdmEntitySet(entitySetName);
        String kind = ees.type.name;

        long id = Long.parseLong(entityKey.toString());
        datastore.delete(KeyFactory.createKey(kind, id));
    }

    @Override
    public void mergeEntity(String entitySetName, Object entityKey, List<OProperty<?>> properties) {

        Entity e = findEntity(entitySetName, entityKey);
        if (e == null)
            throw new NotFoundException();
        applyProperties(e, properties);
        datastore.put(e);
    }

    @Override
    public void updateEntity(String entitySetName, Object entityKey, List<OProperty<?>> properties) {
        Entity e = findEntity(entitySetName, entityKey);
        if (e == null)
            throw new NotFoundException();

        // clear existing props
        for(String name : e.getProperties().keySet())
            e.removeProperty(name);

        applyProperties(e, properties);
        datastore.put(e);
    }

    private static OEntity toOEntity(Entity entity) {

        final List<OProperty<?>> properties = new ArrayList<OProperty<?>>();
        properties.add(OProperties.int64(ID_PROPNAME, entity.getKey().getId()));

        for(String name : entity.getProperties().keySet()) {
            Object propertyValue = entity.getProperty(name);
            if (propertyValue==null)
                continue;
            if (propertyValue instanceof String) {
                properties.add(OProperties.string(name, (String) propertyValue));
            } else if (propertyValue instanceof Integer) {
                properties.add(OProperties.int32(name, (Integer) propertyValue));
            } else if (propertyValue instanceof Long) {
                properties.add(OProperties.int64(name, (Long) propertyValue));
            } else if (propertyValue instanceof Short) {
                properties.add(OProperties.int16(name, (Short) propertyValue));
            } else if (propertyValue instanceof Boolean) {
                properties.add(OProperties.boolean_(name, (Boolean) propertyValue));
            } else if (propertyValue instanceof Float) {
                properties.add(OProperties.single(name, (Float) propertyValue));
            } else if (propertyValue instanceof Double) {
                properties.add(OProperties.double_(name, (Double) propertyValue));
            } else if (propertyValue instanceof Date) {
                properties.add(OProperties.datetime(name, (Date) propertyValue));
            } else if (propertyValue instanceof Byte) {
                properties.add(OProperties.byte_(name, (Byte) propertyValue));
            }

            // Ordinary Java strings stored as properties in Entity objects are limited to 500 characters (DataTypeUtils.MAX_STRING_PROPERTY_LENGTH)
            // Text are unlimited, but unindexed
            else if (propertyValue instanceof Text) {
                Text text = (Text) propertyValue;
                properties.add(OProperties.string(name, text.getValue()));
            } else if (propertyValue instanceof ShortBlob) {
                ShortBlob sb = (ShortBlob) propertyValue;
                properties.add(OProperties.binary(name, sb.getBytes()));
            } else {
                throw new UnsupportedOperationException(propertyValue.getClass().getName());
            }
        }

        return OEntities.create(properties,new ArrayList<OLink>());
        
    }

    private static final Set<EdmType> supportedTypes = Enumerable.create(EdmType.BOOLEAN, EdmType.BYTE, EdmType.STRING, EdmType.INT16, EdmType.INT32, EdmType.INT64, EdmType.SINGLE, EdmType.DOUBLE, EdmType.DATETIME, EdmType.BINARY // only up to 500 bytes MAX_SHORT_BLOB_PROPERTY_LENGTH

            ).toSet();

    private void applyProperties(Entity e, List<OProperty<?>> properties) {
        for(OProperty<?> prop : properties) {
            EdmType type = prop.getType();
            if (!supportedTypes.contains(type))
                throw new UnsupportedOperationException("EdmType not supported: " + type);

            Object value = prop.getValue();
            if (type.equals(EdmType.STRING)) {
                String sValue = (String) value;
                if (sValue != null && sValue.length() > DataTypeUtils.MAX_STRING_PROPERTY_LENGTH)
                    value = new Text(sValue);
            } else if (type.equals(EdmType.BINARY)) {
                byte[] bValue = (byte[]) value;
                if (bValue != null) {
                    if (bValue.length > DataTypeUtils.MAX_SHORT_BLOB_PROPERTY_LENGTH)
                        throw new RuntimeException("Bytes " + bValue.length + " exceeds the max supported length " + DataTypeUtils.MAX_SHORT_BLOB_PROPERTY_LENGTH);
                    value = new ShortBlob(bValue);
                }
            } else if (type.equals(EdmType.DATETIME)) {
                LocalDateTime dValue = (LocalDateTime) value;
                value = dValue.toDateTime().toDate(); // TODO review
            }
            e.setProperty(prop.getName(), value);
        }
    }

    private Entity findEntity(String entitySetName, Object entityKey) {
        final EdmEntitySet ees = metadata.getEdmEntitySet(entitySetName);
        String kind = ees.type.name;

        long id = Long.parseLong(entityKey.toString());
        try {
            return datastore.get(KeyFactory.createKey(kind, id));
        } catch (EntityNotFoundException e1) {
            return null;
        }

    }

    private void applySort(Query q, List<OrderByExpression> orderBy) {
        for(OrderByExpression ob : orderBy) {
            if (!(ob.getExpression() instanceof EntitySimpleProperty))
                throw new UnsupportedOperationException("Appengine only supports simple property expressions");
            String propName = ((EntitySimpleProperty) ob.getExpression()).getPropertyName();
            if (propName.equals("id"))
                propName = "__key__";
            q.addSort(propName, ob.isAscending() ? SortDirection.ASCENDING : SortDirection.DESCENDING);
        }
    }

    private void applyFilter(Query q, BoolCommonExpression filter) {

        // appengine supports simple filterpredicates (name op value)

        // one filter
        if (filter instanceof EqExpression)
            applyFilter(q, (EqExpression) filter, FilterOperator.EQUAL);
        else if (filter instanceof NeExpression)
            applyFilter(q, (NeExpression) filter, FilterOperator.NOT_EQUAL);
        else if (filter instanceof GtExpression)
            applyFilter(q, (GtExpression) filter, FilterOperator.GREATER_THAN);
        else if (filter instanceof GeExpression)
            applyFilter(q, (GeExpression) filter, FilterOperator.GREATER_THAN_OR_EQUAL);
        else if (filter instanceof LtExpression)
            applyFilter(q, (LtExpression) filter, FilterOperator.LESS_THAN);
        else if (filter instanceof LeExpression)
            applyFilter(q, (LeExpression) filter, FilterOperator.LESS_THAN_OR_EQUAL);

        // and filter
        else if (filter instanceof AndExpression) {
            AndExpression e = (AndExpression) filter;
            applyFilter(q, e.getLHS());
            applyFilter(q, e.getRHS());
        }

        else
            throw new UnsupportedOperationException("Appengine only supports simple property expressions");

    }

    private void applyFilter(Query q, BinaryCommonExpression e, FilterOperator op) {

        if (!(e.getLHS() instanceof EntitySimpleProperty))
            throw new UnsupportedOperationException("Appengine only supports simple property expressions");
        if (!(e.getRHS() instanceof LiteralExpression))
            throw new UnsupportedOperationException("Appengine only supports simple property expressions");

        EntitySimpleProperty lhs = (EntitySimpleProperty) e.getLHS();
        LiteralExpression rhs = (LiteralExpression) e.getRHS();

        String propName = lhs.getPropertyName();
        Object propValue = Expression.literalValue(rhs);
        if (propName.equals("id")) {
            propName = "__key__";
            propValue = KeyFactory.createKey(q.getKind(), (Long) propValue);
        }

        q.addFilter(propName, op, propValue);
    }

}
