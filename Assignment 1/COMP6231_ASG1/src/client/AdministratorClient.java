package client;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.text.SimpleDateFormat;
import java.util.Date;

import interfaces.GameServer;
import util.LocationConstants;
import util.PortConstants;
/**
 * 
 * @author <a href="mailto:rajmistry2298@gmail.com">Raj Mistry</a>
 *
 */
public class AdministratorClient {
	private static GameServer server = null;
	public static void main(String[] args) throws Exception {
		while(true) {
			try {
				displayOperations();
                final String userChoice = getUserInput("Your Choice");
                switch (userChoice) {
                    case "1":
	                    String adminUsername = getName("Username");
	                    adminUsername = getName("Password");
	                    String ipAddress = getIpaddress("IpAddress");
                        String location = setServer(ipAddress);
                        String message = server.getPlayerStatus();
                        System.out.println(message);
                        String logmessage = "Administrator " + adminUsername + " Successfully Connected To Server " + location + " And Got Player Status ::\n";
                        if (Files.exists(Paths.get("src/client/logs/admin/" + location +"_"+ adminUsername + "_Log.txt"))) {
            				writeLog(location+"_"+adminUsername+"_", logmessage+message);
            			} else {
            				PrintWriter writer = new PrintWriter("src/client/logs/admin/" + location+"_"+adminUsername+ "_Log.txt", "UTF-8");
            				writer.println(logmessage+message);
            				writer.close();
            			}
                        break;
                    case "2":
                    	System.out.println("\n!.....Program has been terminated.........!");
                    	System.out.println("\n============ Thank You ====================");
                    	System.exit(0);
                    	break;
                    default:
                        displayOperations();

                	}
            	} catch (NotBoundException e) {
            		System.out.println("NotBoundException ::  " + e.getMessage());
            	}catch (Exception e) {
            		System.out.println("ERROR!!! ::  " + e.getMessage());
            	}
		}
	}
		
	/**
	 * Method To Display Client Operation Menu.
	 */
	public static void displayOperations(){
		System.out.println("\n******* Operation Menu *******");
        System.out.println("\n1. Get Player Status.");
        System.out.println("2. Exit.\n");
    }
	
	/**
	 *This Method is for getting Input from User.
	 */
	public static String getUserInput(final String attribute) throws Exception {
        final BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
        System.out.print("Enter " + attribute + ": ");
        return br.readLine();
    }
	
	public static String getName(final String attribute) throws Exception {
        final BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
        System.out.print("Enter " + attribute + ": ");
        String name = br.readLine();
        if(name.equals("Admin")) {
        	return name.trim();
        }else {
        	System.out.println("\n========================================");
        	System.out.println("  "+ attribute + " Is Not Valid!!!");
        	System.out.println("========================================\n");
        	name = getName(attribute);
        }
        return name;
    }
		
	public static String getIpaddress(final String attribute) throws Exception {
        final BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
        System.out.print("Enter " + attribute + ": ");
        String ipAddress = br.readLine();
        //https://mkyong.com/regular-expressions/how-to-validate-ip-address-with-regular-expression/
        String ipPattern = "^([01]?\\d\\d?|2[0-4]\\d|25[0-5])\\." + "([01]?\\d\\d?|2[0-4]\\d|25[0-5])\\." + "([01]?\\d\\d?|2[0-4]\\d|25[0-5])\\." + "([01]?\\d\\d?|2[0-4]\\d|25[0-5])$";
        if(ipAddress != null && ipAddress.matches(ipPattern) && (!ipAddress.contains(" ")) && (ipAddress.split("\\.")[0].equals("132") || ipAddress.split("\\.")[0].equals("93") || ipAddress.split("\\.")[0].equals("182"))) {
        	return ipAddress;
        }else {
        	System.out.println("\n====================================================================================");
        	System.out.println("  "+ attribute + " Should be Valid[0-255] And Without Any Whitespaces & Alphabets!!");
        	System.out.println("  -> It should be from one of the below Ranges:");
        	System.out.println("  -> 132.xxx.xxx.xxx");
        	System.out.println("  -> 93.xxx.xxx.xxx");
        	System.out.println("  -> 182.xxx.xxx.xxx");
        	System.out.println("====================================================================================\n");
        	ipAddress = getIpaddress(attribute);
        }
        return ipAddress;
    }
	
	public static String setServer(String ipAddress) throws RemoteException, NotBoundException {
		String location = null;
		if(ipAddress.split("\\.")[0].equals("132")) {
        	Registry registry = LocateRegistry.getRegistry(PortConstants.NA_REGISTRY_PORT);
    		server = (GameServer) registry.lookup(LocationConstants.NORTHAMERICA);
    		location = LocationConstants.NORTHAMERICA;
        }
        else if(ipAddress.split("\\.")[0].equals("93")) {
        	Registry registry = LocateRegistry.getRegistry(PortConstants.EU_REGISTRY_PORT);
    		server = (GameServer) registry.lookup(LocationConstants.EUROPE);
    		location = LocationConstants.EUROPE;
        }
        else if(ipAddress.split("\\.")[0].equals("182")) {
        	Registry registry = LocateRegistry.getRegistry(PortConstants.AS_REGISTRY_PORT);
    		server = (GameServer) registry.lookup(LocationConstants.ASIA);
    		location = LocationConstants.ASIA;
        }
        else {
        	throw new NotBoundException();
        }
		return location;
	}
	
	public static void writeLog(String adminID,String logData) throws IOException {
		logData = new SimpleDateFormat("yyyy.MM.dd.HH.mm.ss").format(new Date()) + " : " + logData + "\n";
		Files.write(Paths.get("src/client/logs/admin/" + adminID + "Log.txt"), logData.getBytes(), StandardOpenOption.APPEND);
	}
}
