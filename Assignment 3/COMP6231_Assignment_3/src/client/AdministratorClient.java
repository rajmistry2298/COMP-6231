package client;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.text.SimpleDateFormat;
import java.util.Date;
import util.LocationConstants;
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
            			writeLog(location+"_"+adminUsername+"_", logmessage+message);
                        break;
                    case "2":
	                    String adminsUsername = getName("Username");
	                    adminsUsername = getName("Password");
	                    String usernameToSuspend = getUsername("Username Of Player You Want TO Suspend");
	                    String sIpAddress = getIpaddress("IpAddress");
                        String slocation = setServer(sIpAddress);
                        String smessage = server.suspendAccount(usernameToSuspend);
                        System.out.println(smessage);
                        String tlogmessage = adminsUsername + " Successfully Connected To Server " + slocation + " To Suspend Player Account "+usernameToSuspend +":: "+smessage;
            			writeLog(slocation+"_"+adminsUsername+"_", tlogmessage);
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
	
	public static String setServer(String ipAddress) throws Exception {
		String location=null;
		if(ipAddress.split("\\.")[0].equals("132")) { 	
			NAServerService naService = new NAServerService();
			server = naService.getNAServerPort();
    		location = LocationConstants.NORTHAMERICA;
        }
        else if(ipAddress.split("\\.")[0].equals("93")) {
        	EUServerService euService = new EUServerService();
			server = euService.getEUServerPort();
    		location = LocationConstants.EUROPE;
        }
        else if(ipAddress.split("\\.")[0].equals("182")) {
        	ASServerService asService = new ASServerService();
			server = asService.getASServerPort();
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
	
	public static void writeLog(String adminID,String logData) throws IOException {
		try {
			if (!Files.exists(Paths.get("src/client/logs/admin/" + adminID + "Log.txt"))) {
				PrintWriter writer = new PrintWriter("src/client/logs/admin/" + adminID + "Log.txt", "UTF-8");
				writer.close();
			}
			logData = new SimpleDateFormat("yyyy.MM.dd.HH.mm.ss").format(new Date()) + " : " + logData + "\n";
			Files.write(Paths.get("src/client/logs/admin/" + adminID + "Log.txt"), logData.getBytes(), StandardOpenOption.APPEND);
		}catch (Exception e) {
			System.out.println("Error In Writig Logs : "+e.getMessage());
		}		
	}
}