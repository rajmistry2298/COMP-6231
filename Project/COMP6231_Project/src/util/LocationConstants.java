package util;

import java.net.InetAddress;
import java.net.UnknownHostException;

public class LocationConstants {

	    public static final String FRONTEND = "FrontEnd";
	    public static final String RM1 = "ReplicaManager1";
	    public static final String RM2 = "ReplicaManager2";
	    public static final String RM3 = "ReplicaManager3";

	    public static final String NORTHAMERICA = "NA";
	    public static final String NORTHAMERICA_REPLICA_1 = "NA_Replica_1";
	    public static final String NORTHAMERICA_REPLICA_2 = "NA_Replica_2";
	    public static final String NORTHAMERICA_REPLICA_3 = "NA_Replica_3";
	    
	    public static final String EUROPE = "EU";
	    public static final String EUROPE_REPLICA_1 = "EU_Replica_1";
	    public static final String EUROPE_REPLICA_2 = "EU_Replica_2";
	    public static final String EUROPE_REPLICA_3 = "EU_Replica_3";

	    public static final String ASIA = "AS";
	    public static final String ASIA_REPLICA_1 = "AS_Replica_1";
	    public static final String ASIA_REPLICA_2 = "AS_Replica_2";
	    public static final String ASIA_REPLICA_3 = "AS_Replica_3";

	    public static final String NORTHAMERICA_DESC = "North-America";
	    public static final String EUROPE_DESC = "Europe";
	    public static final String ASIA_DESC = "Asia";

	    public static String getLocation(final String ipAddress) {
	    	if(ipAddress.split("\\.")[0].equals("132")) { 	
				return NORTHAMERICA;
			}
			else if(ipAddress.split("\\.")[0].equals("93")) {
				return EUROPE;
			}
			else if(ipAddress.split("\\.")[0].equals("182")) {
				return ASIA;
			}
    		return null;
 	   }

	   public static String getReplicaName(final int portNo) {
		if (PortConstants.NA_REPLICA_1_UDP_PORT == portNo) {
			return NORTHAMERICA_REPLICA_1;
		} else if (PortConstants.NA_REPLICA_2_UDP_PORT == portNo) {
			return NORTHAMERICA_REPLICA_2;
		} else if (PortConstants.NA_REPLICA_3_UDP_PORT == portNo) {
			return NORTHAMERICA_REPLICA_3;
		} else if (PortConstants.EU_REPLICA_1_UDP_PORT == portNo) {
			return EUROPE_REPLICA_1;
		} else if (PortConstants.EU_REPLICA_2_UDP_PORT == portNo) {
			return EUROPE_REPLICA_2;
		} else if (PortConstants.EU_REPLICA_3_UDP_PORT == portNo) {
			return EUROPE_REPLICA_3;
		} else if (PortConstants.AS_REPLICA_1_UDP_PORT == portNo) {
			return ASIA_REPLICA_1;
		} else if (PortConstants.AS_REPLICA_2_UDP_PORT == portNo) {
			return ASIA_REPLICA_2;
		} else if (PortConstants.AS_REPLICA_3_UDP_PORT == portNo) {
			return ASIA_REPLICA_3;
		}
    	return null;
    }
    
    public static InetAddress getInetAddress(final String serverLocation) throws UnknownHostException {
		return InetAddress.getByName("localhost");
    }
}