package odata4j.core;

import odata4j.consumer.ODataClientRequest;

public interface OClientBehavior {

	public abstract ODataClientRequest transform(ODataClientRequest request);
}
