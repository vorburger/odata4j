package odata4j.sandbox;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

import odata4j.sandbox.OdataClient.AtomEntry;
import odata4j.sandbox.OdataClient.CollectionInfo;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.lang.StringUtils;
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;


public class Sandbox {


	private static void main(String[] args) throws Exception {
	
		
		// ogdi: odata v1 with dataservice
		 String url = "http://ogdi.cloudapp.net/v1";
		 url = "http://ogdi.cloudapp.net/v1/dc/Hotels?$top=5";   
		// url = "http://ogdi.cloudapp.net/v1/dc/Hotels(PartitionKey='WOODLEYPARKGUESTHOUSE',RowKey='6e379965-d717-45ba-bfed-a1060d32c9fb')";
		// url = "http://ogdi.cloudapp.net/v1/dc/Hotels(PartitionKey='WYNDHAMWASHINGTONDC',RowKey='5a7fe5cf-43e0-429e-bfb2-2c2b47778551')";
		
	
		 // dallas datasets, not odata, but close
		// url = "https://api.sqlazureservices.com/UnService.svc/UNESCO/C_F_220054?$page=1&$itemsPerPage=5&ref_area=usa";  // for unesco: dsentries
		// url = "https://api.sqlazureservices.com/UnService.svc/UNESCO/C_F_220054&$page=5&$itemsperpage=1";
		 //url = "https://api.sqlazureservices.com/UnService.svc/UNESCO/$service";
		// url = "https://api.sqlazureservices.com/APService.svc/Categories?$format=atom10";  // for AP: $format=atom10 triggers dsentries (content = properties).  leaving it out returns basic atom
		// url = "https://api.sqlazureservices.com/APService.svc/Search?SearchTerms=chile";
		 url = "https://api.sqlazureservices.com/APService.svc/Search?SearchTerms=chile&MediaSize=preview&$page=1&$itemsperpage=1&$format=atom10";
		 
		 String[] dallas = System.getenv("DALLAS").split(":");
		 
		 OdataClientRequest request = OdataClientRequest.create(url)
		 	.header("$uniqueUserID", dallas[0])
		 	.header("$accountKey",dallas[1])
		 	.header("DataServiceVersion", "2.0");
		 
		 
		 
		 // azure, not odata, but closer
		
		 
		 request = azureTableRequest("GET","Tables",null,null,null);
		 request = azureTableRequest("GET","Tables('table1')",null,null,null);
		 request = azureTableRequest("GET","table1","$top=5",null,null);
		 
		 
		 // visitmix
		 // http://api.visitmix.com/OData.svc/
		 url = "http://api.visitmix.com/OData.svc/";
		 request = OdataClientRequest.create(url);
		 
		 OdataClient client = new OdataClient(true);
		 
		 //client.insertEntity()
		 
		 
		 dumpCollections(client,url);  if (true) return;
		 
		 AtomEntry last = null;
		 for(AtomEntry ei : client.getEntities(request)){
			 log(ei.toString());
			 last = ei;
		 }
		 
//		 if (last.id.startsWith("http")) {
//			 AtomEntry i = client.getEntity(request.url(last.id));
//			 log(i.toString());
//		 }
		 //dumpCollections(client,url);
		
		
	}
	
	
	private static OdataClientRequest azureTableRequest(String method, String path, String queryParams, String contentType, String requestBody) throws Exception {
		
		 String[] azureStorage = System.getenv("AZURESTORAGE").split(":");
		 String account = azureStorage[0];
		 String key = azureStorage[1];
		 
		 String url= "http://"+account+".table.core.windows.net/" + path;
		 if (!StringUtils.isBlank(queryParams))
			 url = url + "?" + queryParams;
		 OdataClientRequest rt = OdataClientRequest.create(url);
		
		 
		 String date = new DateTime(DateTimeZone.UTC).toString("EEE, dd MMM yyyy HH:mm:ss zzz").replace("UTC", "GMT");
		
		 
//		 VERB + "\n" + 
//         Content-MD5 + "\n" + 
//         Content-Type + "\n" +
//         Date + "\n" +
//         CanonicalizedResource;
		 
		 String canonicalizedResource = "/"+account+"/" + path;
		 String stringToSign = method+"\n\n"+contentType+"\n"+date+"\n"+canonicalizedResource;
		 
		 Mac mac = Mac.getInstance("HmacSHA256");
		 mac.init(new SecretKeySpec(base64Decode(key), mac.getAlgorithm()));
		 mac.update(stringToSign.getBytes("utf8"));
		 byte[] sigBytes = mac.doFinal();
		 
		// log(stringToSign);
		 
		 String sig = base64Encode(sigBytes);
		 String auth = "SharedKey "+account+":"+sig;
		// log(auth);
		 
		 return rt
		 		.header("x-ms-version", "2009-09-19")
		 		.header("x-ms-date", date)
		 		.header("Authorization",auth)
				.header("DataServiceVersion", "1.0;NetFx")
				.header("MaxDataServiceVersion", "1.0;NetFx");
		 
	}
	
	private static String base64Encode(byte[] value){
		return Base64.encodeBase64String(value).trim();
	}
	private static byte[] base64Decode(String value) {
		return Base64.decodeBase64(value);
	}
	
	private static void dumpCollections(OdataClient client, String url){
		 for(CollectionInfo si : client.getCollections(OdataClientRequest.create(url))){
			 log(si.toString());
			 if ("application/atomsvc+xml".equals(  si.accept))
				for(CollectionInfo sii : client.getCollections(OdataClientRequest.create(si.url))) {
					 log("  " + sii);
				
			}
			 
		 }
		
	}
	
	private static void log(String message){
		System.out.println(message);
	}
			
	private static void log(String message, Object... args){
		System.out.println(String.format(message,args));
	}

}
