package util;

public class PortConstants {
	   
	   public static final int NA_UDP_PORT = 9090;
	   public static final int EU_UDP_PORT = 9091;
	   public static final int AS_UDP_PORT = 9092;
	   
	   public static int getUdpPort(final String serverLocation) {
		   if(LocationConstants.NORTHAMERICA.equalsIgnoreCase(serverLocation)) {
			   return NA_UDP_PORT;
		   } else if(LocationConstants.EUROPE.equalsIgnoreCase(serverLocation)) {
			   return EU_UDP_PORT;
		   } else if(LocationConstants.ASIA.equalsIgnoreCase(serverLocation)) {
			   return AS_UDP_PORT;
		   } 
		   return 0;
	   }
	   

}
