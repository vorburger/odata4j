package org.odata4j.appengine.test;

import java.util.Set;

import org.core4j.Enumerable;
import org.core4j.Predicate1;
import org.junit.Test;
import org.odata4j.consumer.ODataConsumer;
import org.odata4j.core.OEntity;
import org.odata4j.core.OProperty;

public class AppEngineNorthwindLoader {

  @Test
  public void load() {
    ODataConsumer northwindJpa = ODataConsumer.create("http://localhost:8886/JPAProducerExample.svc/");
    ODataConsumer northwindAppengine =
        ODataConsumer.create("http://localhost:8888/datastore.svc/");
    //ODataConsumer.create("http://odata4j-sample.appspot.com/datastore.svc/");

    nuke(northwindAppengine, "Categories");
    nuke(northwindAppengine, "Suppliers");
    nuke(northwindAppengine, "Products");
    nuke(northwindAppengine, "Customers");
    nuke(northwindAppengine, "Employees");
    nuke(northwindAppengine, "Orders");
    nuke(northwindAppengine, "Order_Details");

    load(northwindJpa, northwindAppengine, "Categories", "Picture");
    load(northwindJpa, northwindAppengine, "Suppliers");
    load(northwindJpa, northwindAppengine, "Products", "UnitPrice");
    load(northwindJpa, northwindAppengine, "Customers");
    load(northwindJpa, northwindAppengine, "Employees", "Photo");
    load(northwindJpa, northwindAppengine, "Orders", "Freight");
    load(northwindJpa, northwindAppengine, "Order_Details", "UnitPrice");

  }

  private void load(ODataConsumer northwindJpa, ODataConsumer northwindAppengine, String entitySetName, String... ignoreProperties) {
    final Set<String> ignorePropertySet = Enumerable.create(ignoreProperties).toSet();
    for (OEntity jpaEntity : northwindJpa.getEntities(entitySetName)) {
      //System.out.println("jpa: " +jpaEntity);

      OEntity aeEntity = northwindAppengine.createEntity(entitySetName).properties(Enumerable.create(jpaEntity.getProperties()).where(new Predicate1<OProperty<?>>() {
        public boolean apply(OProperty<?> input) {
          return !ignorePropertySet.contains(input.getName());
        }
      })).execute();
      System.out.println("ae:  " + aeEntity);
    }
  }

  private void nuke(ODataConsumer consumer, String entitySetName) {
    for (OEntity entity : consumer.getEntities(entitySetName).execute().toList())
      consumer.deleteEntity(entity).execute();

  }
}
