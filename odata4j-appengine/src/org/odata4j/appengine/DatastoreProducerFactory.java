package org.odata4j.appengine;

import java.util.List;
import java.util.Properties;

import org.odata4j.producer.ODataProducer;
import org.odata4j.producer.ODataProducerFactory;

import org.core4j.Enumerable;

public class DatastoreProducerFactory implements ODataProducerFactory {

  public static final String DEVKINDS_PROPNAME = "odata4j.datastore.devkinds";
  public static final String PRODKINDS_PROPNAME = "odata4j.datastore.prodkinds";
  public static final String NAMESPACE_PROPNAME = "odata4j.datastore.namespace";
  public static final String NAMESPACE_PROPDEFAULT = "Datastore";

  @Override
  public ODataProducer create(Properties properties) {

    if (!AppEngineUtil.isServer())
      throw new RuntimeException("Must be running on AppEngine (dev or prod)");

    List<String> kinds = buildKinds();

    String namespace = NAMESPACE_PROPDEFAULT;
    String namespaceProp = System.getProperty(NAMESPACE_PROPNAME);
    if (namespaceProp != null && namespaceProp.trim().length() > 0)
      namespace = namespaceProp.trim();
    return new DatastoreProducer(namespace, kinds);
  }

  private static List<String> buildKinds() {

    if (AppEngineUtil.isDevelopment()) {
      String devkindsProp = System.getProperty(DEVKINDS_PROPNAME);
      if (devkindsProp == null || devkindsProp.trim().length() == 0 || devkindsProp.trim().equals("*"))
        return DatastoreUtil.getKinds();

      return Enumerable.create(devkindsProp.split(",")).toList();

    } else {
      String prodkindsProp = System.getProperty(PRODKINDS_PROPNAME);
      if (prodkindsProp == null || prodkindsProp.trim().length() == 0 || prodkindsProp.trim().equals("*"))
        throw new RuntimeException("Must provide an explicit list of kinds to expose in prod");

      return Enumerable.create(prodkindsProp.split(",")).toList();
    }

  }

}
