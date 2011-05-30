package org.odata4j.appengine;

import java.util.ArrayList;
import java.util.List;

import com.google.apphosting.api.ApiProxy;
import com.google.apphosting.api.DatastorePb.GetSchemaRequest;
import com.google.apphosting.api.DatastorePb.Schema;
import com.google.storage.onestore.v3.OnestoreEntity.EntityProto;
import com.google.storage.onestore.v3.OnestoreEntity.Reference;
import com.google.storage.onestore.v3.OnestoreEntity.Path.Element;

public class DatastoreUtil {
  public static Schema getSchema() {

    GetSchemaRequest req = new GetSchemaRequest();
    req.setApp(ApiProxy.getCurrentEnvironment().getAppId());
    byte[] resBuf = ApiProxy.makeSyncCall("datastore_v3", "GetSchema", req.toByteArray());
    Schema schema = new Schema();
    schema.mergeFrom(resBuf);
    return schema;
  }

  public static List<String> getKinds() {

    Schema schema = getSchema();
    List<EntityProto> entityProtoList = schema.kinds();
    List<String> kindList = new ArrayList<String>(entityProtoList.size());
    for (EntityProto entityProto : entityProtoList) {
      kindList.add(getKind(entityProto.getKey()));
    }
    return kindList;
  }

  public static String getKind(Reference key) {
    List<Element> elements = key.getPath().elements();
    return elements.get(elements.size() - 1).getType();
  }
}
