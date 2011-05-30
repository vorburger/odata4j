package org.odata4j.android.activity;

import java.util.Iterator;

import org.odata4j.android.AndroidLogger;
import org.odata4j.android.InfiniteAdapter;
import org.odata4j.android.model.ServiceVM;
import org.odata4j.consumer.ODataConsumer;
import org.odata4j.core.OEntity;
import org.odata4j.core.ORelatedEntitiesLink;
import org.odata4j.examples.ODataEndpoints;

import android.app.ListActivity;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;

public class EntitiesActivity extends ListActivity {

  private final AndroidLogger log = AndroidLogger.get(getClass());

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);

    final ServiceVM service;
    String entitySet;
    ORelatedEntitiesLink link;
    if (getIntent().getExtras() != null) {
      service = (ServiceVM) getIntent().getExtras().getSerializable("service");
      entitySet = (String) getIntent().getExtras().getString("entitySet");
      link = (ORelatedEntitiesLink) getIntent().getExtras().getSerializable("link");
    } else {
      service = new ServiceVM("netflix", ODataEndpoints.NETFLIX);
      entitySet = "Titles";
      link = null;
    }

    setTitle(link != null ? link.getTitle() : entitySet);

    ODataConsumer c = ODataConsumer.create(service.getUri());
    ODataConsumer.dump.requestHeaders(true);

    Iterator<OEntity> entities = (link != null ? c.getEntities(link) : c.getEntities(entitySet))
            // .top(10)
        .execute().iterator();

    InfiniteAdapter<OEntity> adapter = new InfiniteAdapter<OEntity>(this, entities, 20) {
      public View getView(int pos, View v, ViewGroup p) {
        OEntity entity = (OEntity) getItem(pos);
        return EntityViews.newEntityTable(getContext(), entity, service);
      }
    };

    getListView().setOnItemClickListener(new OnItemClickListener() {
      public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
        log.info("onItemClick");
      }
    });

    setListAdapter(adapter);
    getListView().setOnScrollListener(adapter);
    getListView().setDivider(null);
    getListView().setDividerHeight(0);

  }

}
