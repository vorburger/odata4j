package org.odata4j.edm;

import java.util.ArrayList;
import java.util.List;

public class EdmDataServices {

    public final String version;
    public final List<EdmSchema> schemas;

    public EdmDataServices(String version, List<EdmSchema> schemas) {
        this.version = version;
        this.schemas = schemas;
    }

    public EdmEntitySet getEdmEntitySet(String entitySetName) {
        for(EdmSchema schema : this.schemas) {
            for(EdmEntityContainer eec : schema.entityContainers) {
                for(EdmEntitySet ees : eec.entitySets) {
                    if (ees.name.equals(entitySetName))
                        return ees;
                }
            }
        }
        throw new RuntimeException("EdmEntitySet " + entitySetName + " not found");
    }

    public Iterable<EdmEntityType> getEntityTypes() {
        List<EdmEntityType> rt = new ArrayList<EdmEntityType>();
        for(EdmSchema schema : this.schemas) {
            rt.addAll(schema.entityTypes);
        }
        return rt;
    }
    public Iterable<EdmAssociation> getAssociations() {
        List<EdmAssociation> rt = new ArrayList<EdmAssociation>();
        for(EdmSchema schema : this.schemas) {
            rt.addAll(schema.associations);
        }
        return rt;
    }
    public Iterable<EdmEntitySet> getEntitySets(){
        List<EdmEntitySet> rt = new ArrayList<EdmEntitySet>();
        for(EdmSchema schema : this.schemas) {
            for(EdmEntityContainer eec : schema.entityContainers)
                rt.addAll(eec.entitySets);
        }
        return rt;
    }

}
