package util;

public class LocationConstants {

	    public static final String NORTHAMERICA = "NA";
	    public static final String EUROPE = "EU";
	    public static final String ASIA = "AS";
	    public static final String NORTHAMERICA_DESC = "North-America";
	    public static final String EUROPE_DESC = "Europe";
	    public static final String ASIA_DESC = "Asia";
	    public static final String SERVER = "Server";
	    
	    //Address for 3 Servers
	    public static final String NORTHAMERICA_ADDRESS = "http://localhost:8080/DPSS/" + LocationConstants.NORTHAMERICA;
	    public static final String EUROPE_ADDRESS = "http://localhost:8081/DPSS/" + LocationConstants.EUROPE;
	    public static final String ASIA_ADDRESS = "http://localhost:8082/DPSS/" + LocationConstants.ASIA;
	    
	    //WSDL URLS
	    public static final String NORTHAMERICA_WSDL_URL = LocationConstants.NORTHAMERICA_ADDRESS + "?wsdl";
	    public static final String EUROPE_WSDL_URL = LocationConstants.EUROPE_ADDRESS + "?wsdl";
	    public static final String ASIA_WSDL_URL = LocationConstants.ASIA_ADDRESS + "?wsdl";

}
