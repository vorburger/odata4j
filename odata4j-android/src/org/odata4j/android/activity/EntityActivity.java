package org.odata4j.android.activity;

import org.odata4j.android.AndroidLogger;
import org.odata4j.android.model.ServiceVM;
import org.odata4j.consumer.ODataConsumer;
import org.odata4j.core.OEntity;
import org.odata4j.core.OLinks;
import org.odata4j.core.ORelatedEntityLink;
import org.odata4j.examples.ODataEndpoints;

import android.app.Activity;
import android.os.Bundle;
import android.widget.TableLayout;
import android.widget.TextView;

public class EntityActivity extends Activity {

  private final AndroidLogger log = AndroidLogger.get(getClass());

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);

    ServiceVM service;
    ORelatedEntityLink link;
    if (getIntent().getExtras() != null) {
      service = (ServiceVM) getIntent().getExtras().getSerializable("service");
      link = (ORelatedEntityLink) getIntent().getExtras().getSerializable("link");
    } else {
      service = new ServiceVM("msteched", ODataEndpoints.TECH_ED);
      link = OLinks.relatedEntity("http://schemas.microsoft.com/ado/2007/08/dataservices/related/Tag1", "Tag1", "Tags(1)/Tag2");
    }

    if (link == null)
      return;

    if (link != null)
      log.info("link " + link.getHref()); //  Titles('13kZA')/Season

    setTitle("Entity");
    ODataConsumer c = ODataConsumer.create(service.getUri());
    ODataConsumer.dump.requestHeaders(true);
    //ODataConsumer.DUMP_RESPONSE_BODY = true;
    OEntity entity = c.getEntity(link).execute();
    log.info("entity:" + entity);
    if (entity == null) {
      TextView tv = new TextView(this);
      tv.setText("no content");
      setContentView(tv);
      return;
    }
    TableLayout table = EntityViews.newEntityTable(this, entity, service);
    setContentView(table);

  }

}
