package odata4j.edm;

public enum EdmType {

	BINARY("Edm.Binary"),
	BOOLEAN("Edm.Boolean"),
	DATETIME("Edm.DateTime"),
	DATETIMEOFFSET("Edm.DateTimeOffset"),
	TIME("Edm.Time"),
	DECIMAL("Edm.Decimal"),
	SINGLE("Edm.Single"),
	DOUBLE("Edm.Double"),
	GUID("Edm.Guid"),
	INT16("Edm.Int16"),
	INT32("Edm.Int32"),
	INT64("Edm.Int64"),
	BYTE("Edm.Byte"),
	STRING("Edm.String"),
	FACETS("Edm.Facets"),
	;
	
	private final String typeString;

	private EdmType(String typeString){
		this.typeString = typeString;
	}
	
	public String getTypeString(){
		return typeString;
	}
	
}
