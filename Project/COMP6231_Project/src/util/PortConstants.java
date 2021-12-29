package util;

import java.util.ArrayList;
import java.util.Collections;

public class PortConstants {
	   //RM port
	   public static final int RM1_UDP_PORT = 9060;
	   public static final int RM2_UDP_PORT = 9070;
	   public static final int RM3_UDP_PORT = 9080;
	
	   //NA Replica's Ports
   	   public static final int NA_REPLICA_1_UDP_PORT = 9061;
	   public static final int NA_REPLICA_2_UDP_PORT = 9062;
	   public static final int NA_REPLICA_3_UDP_PORT = 9063;
	   
   
       //EU Replica's Ports
	   public static final int EU_REPLICA_1_UDP_PORT = 9071;
	   public static final int EU_REPLICA_2_UDP_PORT = 9072;
	   public static final int EU_REPLICA_3_UDP_PORT = 9073;
   
	   //AS Replica's Ports
	   public static final int AS_REPLICA_1_UDP_PORT = 9081;
	   public static final int AS_REPLICA_2_UDP_PORT = 9082;
	   public static final int AS_REPLICA_3_UDP_PORT = 9083;
	   
	   public static int getUdpPort(final String serverLocation) {
	   if(LocationConstants.NORTHAMERICA_REPLICA_1.equalsIgnoreCase(serverLocation)) {
		   return NA_REPLICA_1_UDP_PORT;
	   } else if(LocationConstants.NORTHAMERICA_REPLICA_2.equalsIgnoreCase(serverLocation)) {
		   return NA_REPLICA_2_UDP_PORT;
	   } else if(LocationConstants.NORTHAMERICA_REPLICA_3.equalsIgnoreCase(serverLocation)) {
		   return NA_REPLICA_3_UDP_PORT;
	   } else if(LocationConstants.EUROPE_REPLICA_1.equalsIgnoreCase(serverLocation)) {
		   return EU_REPLICA_1_UDP_PORT;
	   } else if(LocationConstants.EUROPE_REPLICA_2.equalsIgnoreCase(serverLocation)) {
		   return EU_REPLICA_2_UDP_PORT;
	   } else if(LocationConstants.EUROPE_REPLICA_3.equalsIgnoreCase(serverLocation)) {
		   return EU_REPLICA_3_UDP_PORT;
	   } else if(LocationConstants.ASIA_REPLICA_1.equalsIgnoreCase(serverLocation)) {
		   return AS_REPLICA_1_UDP_PORT;
	   } else if(LocationConstants.ASIA_REPLICA_2.equalsIgnoreCase(serverLocation)) {
		   return AS_REPLICA_2_UDP_PORT;
	   } else if(LocationConstants.ASIA_REPLICA_3.equalsIgnoreCase(serverLocation)) {
		   return AS_REPLICA_3_UDP_PORT;
	   } else if(LocationConstants.RM1.equalsIgnoreCase(serverLocation)) {
		   return RM1_UDP_PORT;
	   } else if(LocationConstants.RM2.equalsIgnoreCase(serverLocation)) {
		   return RM2_UDP_PORT;
	   } else if(LocationConstants.RM3.equalsIgnoreCase(serverLocation)) {
		   return RM3_UDP_PORT;
	   } 
	   return 0;
	}
	
	public static ArrayList<Integer> getPortList(final String serverLocation){
		final ArrayList<Integer> portList = new ArrayList<>();
		if(serverLocation.startsWith(LocationConstants.NORTHAMERICA)) {
			portList.add(NA_REPLICA_1_UDP_PORT);
			portList.add(NA_REPLICA_2_UDP_PORT);
			portList.add(NA_REPLICA_3_UDP_PORT);
		} else if(serverLocation.startsWith(LocationConstants.EUROPE)) {
			portList.add(EU_REPLICA_1_UDP_PORT);
			portList.add(EU_REPLICA_2_UDP_PORT);
			portList.add(EU_REPLICA_3_UDP_PORT);
		} else if(serverLocation.startsWith(LocationConstants.ASIA)) {
			portList.add(AS_REPLICA_1_UDP_PORT);
			portList.add(AS_REPLICA_2_UDP_PORT);
			portList.add(AS_REPLICA_3_UDP_PORT);
		}
		Collections.sort(portList);
		return portList;
	}
	   

}