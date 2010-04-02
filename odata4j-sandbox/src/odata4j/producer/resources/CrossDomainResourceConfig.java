package odata4j.producer.resources;

import odata4j.producer.resources.ClientAccessPolicyXmlResource;
import odata4j.producer.resources.CrossDomainXmlResource;

import com.sun.jersey.api.core.DefaultResourceConfig;

public class CrossDomainResourceConfig extends DefaultResourceConfig {

	public CrossDomainResourceConfig(){
		super(CrossDomainXmlResource.class,ClientAccessPolicyXmlResource.class);
	}
}
