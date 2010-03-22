package odata4j.consumer.behaviors;

import odata4j.consumer.ODataClientRequest;
import odata4j.core.OClientBehavior;

public class MethodTunnelingBehavior implements OClientBehavior {

	private final String[] methodsToTunnel;
	public MethodTunnelingBehavior(String... methodsToTunnel){
		this.methodsToTunnel= methodsToTunnel;
	}
	@Override
	public ODataClientRequest transform(ODataClientRequest request) {
		String method = request.getMethod();
		for(String methodToTunnel : methodsToTunnel){
			if (method.equals(methodToTunnel)){
				return request.header("X-HTTP-METHOD",method)
							.method("POST");
			}
		}
		return request;
	}

}
