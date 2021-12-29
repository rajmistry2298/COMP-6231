package client;

import java.io.BufferedReader;
import java.io.InputStreamReader;

import org.omg.CORBA.ORB;
import org.omg.CosNaming.NamingContextExt;
import org.omg.CosNaming.NamingContextExtHelper;

import FrontEndApp.FrontEnd;
import FrontEndApp.FrontEndHelper;
import service.ActivityLoggerService;
import util.FileConstants;
import util.LocationConstants;
/**
 * 
 * @author <a href="mailto:rajmistry2298@gmail.com">Raj Mistry</a>
 *
 */
public class AdministratorClient {
	private static FrontEnd server = null;
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
                        String location = setServer(args, ipAddress);
                        String message = server.getPlayerStatus(ipAddress);
                        ActivityLoggerService activityLogger = new ActivityLoggerService(FileConstants.CLIENT_LOG_FILE_PATH +"Admin/" + location + "_" + adminUsername +FileConstants.FILE_TYPE);
                        activityLogger.log("INFO", message);
                        break;
                    case "2":
	                    String adminsUsername = getName("Username");
	                    adminsUsername = getName("Password");
	                    String usernameToSuspend = getUsername("Username Of Player You Want TO Suspend");
	                    String sIpAddress = getIpaddress("IpAddress");
                        String slocation = setServer(args, sIpAddress);
                        String smessage = server.suspendAccount(usernameToSuspend, sIpAddress);
                        ActivityLoggerService activityLogger1 = new ActivityLoggerService(FileConstants.CLIENT_LOG_FILE_PATH +"Admin/" + slocation + "_" + adminsUsername +FileConstants.FILE_TYPE);
                        activityLogger1.log("INFO", smessage);
                        break;
                    case "3":
                    	System.out.println("\n!.......Admin Client Has Been Terminated.........!");
                    	System.out.println("\n=============== Thank You ========================");
                    	System.exit(0);
                    	break;
                    default:
                        displayOperations();
                	}
            	}catch (Exception e) {
            		System.out.println("ERROR Occured!!! ::  " + e.getMessage());
           
            	}
		}
	}
		
	/**
	 * Method To Display Client Operation Menu.
	 */
	public static void displayOperations(){
		System.out.println("\n******* Operation Menu *******");
        System.out.println("\n1. Get Player Status.");
        System.out.println("2. Suspend Player Account.");
        System.out.println("3. Exit.\n");
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
	
	public static String getUsername(final String attribute) throws Exception {
        final BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
        System.out.print("Enter " + attribute + ": ");
        String username = br.readLine();
        if(username != null && username.length() > 5 && username.length() <= 15 && (!username.contains(" ")) && Character.isLetter(username.charAt(0))) {
        	return username;
        }else {
        	System.out.println("\n======================================================================================");
        	System.out.println("  "+ attribute + " Should Start With Alphabet Without Any Whitespaces!!");
        	System.out.println("  -> It should have a length minimum of 6 characters and a maximum of 15 characters.");
        	System.out.println("======================================================================================\n");
        	username = getUsername(attribute);
        }
        return username;
    }
	
	public static String setServer(String [] args,String ipAddress) throws Exception {
		String location=null;
		ORB orb = ORB.init(args, null);
	    org.omg.CORBA.Object objRef =   orb.resolve_initial_references("NameService");
	    NamingContextExt ncRef = NamingContextExtHelper.narrow(objRef);
	    server = (FrontEnd) FrontEndHelper.narrow(ncRef.resolve_str(LocationConstants.FRONTEND));
		if(ipAddress.split("\\.")[0].equals("132")) { 	
    		location = LocationConstants.NORTHAMERICA;
        }
        else if(ipAddress.split("\\.")[0].equals("93")) {
    		location = LocationConstants.EUROPE;
        }
        else if(ipAddress.split("\\.")[0].equals("182")) {
    		location = LocationConstants.ASIA;
        }
        else {
        	throw new Exception();
        }
		DisplayServerName(location);
		return location;
	}
	
	public static void DisplayServerName(final String serverName) {
    	System.out.println("\n*************** Welcome to " + serverName + " ***************\n");
    }
}