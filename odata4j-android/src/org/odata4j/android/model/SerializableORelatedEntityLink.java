package org.odata4j.android.model;

import java.io.Serializable;

import org.odata4j.core.ORelatedEntityLink;

public class SerializableORelatedEntityLink implements ORelatedEntityLink, Serializable{

    private static final long serialVersionUID = 7644060473357216990L;
    
    private final String href;
    private final String relation;
    private final String title;
    
    public SerializableORelatedEntityLink(ORelatedEntityLink link){
        this.href = link.getHref();
        this.relation = link.getRelation();
        this.title = link.getTitle();
    }
    
    @Override
    public String getHref() {
       return href;
    }

    @Override
    public String getRelation() {
        return relation;
    }

    @Override
    public String getTitle() {
        return title;
    }
    
}