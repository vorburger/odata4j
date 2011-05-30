package org.odata4j.appengine;

public class AppEngineUtil {
  public static final String ENVIRONMENT_PROPNAME = "com.google.appengine.runtime.environment";
  public static final String VERSION_PROPNAME = "com.google.appengine.runtime.version";

  public static final String PRODUCTION_VALUE = "Production";
  public static final String DEVELOPMENT_VALUE = "Development";

  public static boolean isServer() {
    return System.getProperty(ENVIRONMENT_PROPNAME) != null;
  }

  public static boolean isDevelopment() {
    return DEVELOPMENT_VALUE.equals(System.getProperty(ENVIRONMENT_PROPNAME));
  }

  public static boolean isProduction() {
    return PRODUCTION_VALUE.equals(System.getProperty(ENVIRONMENT_PROPNAME));
  }

  public static String getVersion() {
    return System.getProperty(VERSION_PROPNAME);
  }

}
