package odata4j.consumer;

import odata4j.consumer.behaviors.AzureTableBehavior;
import odata4j.consumer.behaviors.DallasAuthenticationBehavior;
import odata4j.consumer.behaviors.OldStylePagingBehavior;
import odata4j.core.OClientBehavior;

public class ODataConsumers {

	private ODataConsumers(){}
	
	public static ODataConsumer azureTables(String account, String key){
		String url= "http://"+account+".table.core.windows.net/";
		 
	    return  ODataConsumer.create(url,new AzureTableBehavior(account,key));
		 

	}
	public static ODataConsumer dallas(String serviceRootUri, String accountKey, String uniqueUserId){
		OClientBehavior dallasAuth = new DallasAuthenticationBehavior(accountKey,uniqueUserId);
		
		OClientBehavior paging = new OldStylePagingBehavior(50,20);
		
		return ODataConsumer.create(serviceRootUri,dallasAuth,paging);
	}
}
