package org.odata4j.android.model;

import java.io.Serializable;

public class ServiceVM implements Serializable{
    private static final long serialVersionUID = 2399696657883102674L;
    private final String uri;
    private final String caption;
    public ServiceVM(String caption, String uri){
        this.caption = caption;
        this.uri = uri;
    }
    public String getCaption() {
        return caption;
    }
    public String getUri() {
        return uri;
    }
    @Override
    public String toString() {
       return caption;
    }
}