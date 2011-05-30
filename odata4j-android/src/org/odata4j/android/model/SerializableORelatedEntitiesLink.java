package org.odata4j.android.model;

import java.io.Serializable;

import org.odata4j.core.ORelatedEntitiesLink;

public class SerializableORelatedEntitiesLink implements ORelatedEntitiesLink, Serializable {

  private static final long serialVersionUID = 2362730336435399569L;

  private final String href;
  private final String relation;
  private final String title;

  public SerializableORelatedEntitiesLink(ORelatedEntitiesLink link) {
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