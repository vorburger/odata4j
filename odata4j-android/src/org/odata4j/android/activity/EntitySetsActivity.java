package org.odata4j.android.activity;

import java.util.List;

import org.odata4j.android.AndroidLogger;
import org.odata4j.android.R;
import org.odata4j.android.model.ServiceVM;
import org.odata4j.consumer.ODataConsumer;

import android.app.ListActivity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;

public class EntitySetsActivity extends ListActivity {

    private final AndroidLogger log = AndroidLogger.get(getClass());
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        final ServiceVM service = (ServiceVM)getIntent().getExtras().getSerializable("service");
        
        ODataConsumer c = ODataConsumer.create(service.getUri());
        List<String> entitySets = c.getEntitySets().toList();
        
        setListAdapter(new ArrayAdapter<String>(this,R.layout.entityset,entitySets));
        getListView().setTextFilterEnabled(true);
        getListView().setOnItemClickListener(new OnItemClickListener(){
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                log.info("onItemClick(%s,%s)",position,id);
                
                String entitySet = (String)parent.getItemAtPosition(position);
                startActivity(new Intent(EntitySetsActivity.this,EntitiesActivity.class).putExtra("service", service).putExtra("entitySet", entitySet));
            }});
    }
}
