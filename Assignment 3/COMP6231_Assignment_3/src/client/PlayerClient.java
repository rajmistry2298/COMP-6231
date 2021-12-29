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
public class PlayerClient {
	private static GameServer server = null;
   
	public static void main(String[] args) throws Exception {
		while(true) {
			try {
				displayOperations();
                final String userChoice = getUserInput("Your Choice");
                switch (userChoice) {
                    case "1":
                    	String firstName = getName("First Name");
	                    String lastName = getName("Last Name");
	                    String age = getAge("Age");
	                    String username = getUsername("Username");
	                    String password = getPassword("Password");
	                    String ipAddress = getIpaddress("IpAddress");
                        String location = setServer(ipAddress);
                        String message = server.createPlayerAccount(firstName, lastName, age, username, password, ipAddress);
                        System.out.println(message);
                        if(message.equals("Player Account \""+username+"\" Successfully Created!!!")) {
                        	PrintWriter writer = new PrintWriter("src/client/logs/players/" + location + "_" + username + "_" + "Log.txt", "UTF-8");
            				writer.println(new SimpleDateFormat("yyyy.MM.dd.HH.mm.ss").format(new Date()) + " : " + message);
            				writer.close();
                        }
                        break;
                    case "2":
                    	String signinUsername = getUsername("Username To SignIn");
	                    String signinPassword = getPassword("Password To SignIn");
	                    String signinIpAddress = getIpaddress("IpAddress To SignIn");
                        String signinLocation = setServer(signinIpAddress);
                        String signinMessage = server.playerSignIn(signinUsername, signinPassword);
                        System.out.println(signinMessage);
                        if(!signinMessage.equals("Player Acount with Username \""+signinUsername+"\" does not exist!!!")) {
                        	writeLog(signinLocation+"_"+signinUsername+"_", signinMessage);
                        }
                        break;
                    case "3":
                    	String signoutUsername = getUsername("Username To SignOut");
	                    String signoutIpAddress = getIpaddress("IpAddress To SignOut");
                        String signoutLocation = setServer(signoutIpAddress);
                        String signoutMessage = server.playerSignOut(signoutUsername);
                        System.out.println(signoutMessage);
                        if(!signoutMessage.equals("Player Acount with Username \""+signoutUsername+"\" does not exist!!!")) {
                        	writeLog(signoutLocation+"_"+signoutUsername+"_", signoutMessage);
                        }
                    	break;
                    case "4":
                    	String tusername = getUsername("Username");
	                    String tpassword = getPassword("Password");
	                    String oldIpAddress = getIpaddress("Old IpAddress");
	                    String newIpAddress = getNewIpaddress("New IpAddress", oldIpAddress);
	                    String tlocation = setServer(oldIpAddress);
	                    String tmessage = server.transferAccount(tusername, tpassword, newIpAddress);
                        System.out.println(tmessage);
                        if(!tmessage.equals("Player Acount With Username \""+tusername+"\" does not exist!!!")) {
                        	writeLog(tlocation+"_"+tusername+"_", tmessage);
                        }
                    	break;
                    case "5":
                    	System.out.println("\nProgram has been terminated..!");
                    	System.out.println("\n================ Thank You ================");
                    	System.exit(0);
                    	break;
                    default:
                        displayOperations();

                	}
            	}catch (Exception e) {
            		System.out.println("ERROR!!! :: " + e.getMessage());
            	}
		}
	}
		
	/**
	 * Method To Display Client Operation Menu.
	 */
	public static void displayOperations(){
	System.out.println("\n******* Operation Menu *******");
        System.out.println("\n1. Create New Player Account.");
        System.out.println("2. Player Sign In.");
        System.out.println("3. Player Sign Out.");
        System.out.println("4. Transfer Player Account.");
        System.out.println("5. Exit.\n");
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
        if(name != null && name.matches("^[a-zA-Z]*$") && name.length() != 0) {
        	return name;
        }else {
        	System.out.println("\n========================================================================");
        	System.out.println("  "+ attribute + " Should Contain Only Alphabets Without Any Whitespaces!!!");
        	System.out.println("========================================================================\n");
        	name = getName(attribute);
        }
        return name;
    }
	
	public static String getAge(final String attribute) throws Exception {
        final BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
        System.out.print("Enter " + attribute + ": ");
        String age = br.readLine();
        if(age != null && age.matches("\\d+") && age.length() != 0) {
        	return age;
        }else {
        	System.out.println("\n================================================================================");
        	System.out.println("  "+ attribute + " Should Contains Only Positvie Integer Without Any Whitespaces!!");
        	System.out.println("================================================================================\n");
        	age = getAge(attribute);
        }
        return age;
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
	
	public static String getPassword(final String attribute) throws Exception {
        final BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
        System.out.print("Enter " + attribute + ": ");
        String password = br.readLine();
        if(password != null && password.length() > 5 && (!password.contains(" ")) && password.matches("^[a-zA-Z0-9!@#$%^&]+$")) {
        	return password;
        }else {
        	System.out.println("\n====================================================================================");
        	System.out.println("  "+ attribute + " Should Have Minimum 6 Characters And Without Any Whitespaces!!");
        	System.out.println("  -> It Can Contain Alphabets, Numbers OR Special Characers[! @ # $ % ^ &].");
        	System.out.println("====================================================================================\n");
        	password = getPassword(attribute);
        }
        return password;
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
	
	public static String getNewIpaddress(final String attribute,final String oldIpaddress) throws Exception {
        final BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
        System.out.print("Enter " + attribute + ": ");
        String ipAddress = br.readLine();
        //https://mkyong.com/regular-expressions/how-to-validate-ip-address-with-regular-expression/
        String ipPattern = "^([01]?\\d\\d?|2[0-4]\\d|25[0-5])\\." + "([01]?\\d\\d?|2[0-4]\\d|25[0-5])\\." + "([01]?\\d\\d?|2[0-4]\\d|25[0-5])\\." + "([01]?\\d\\d?|2[0-4]\\d|25[0-5])$";
        if(ipAddress != null && ipAddress.matches(ipPattern) && (!ipAddress.contains(" ")) && (ipAddress.split("\\.")[0].equals("132") || ipAddress.split("\\.")[0].equals("93") || ipAddress.split("\\.")[0].equals("182")) && (!oldIpaddress.split("\\.")[0].equals(ipAddress.split("\\.")[0]))) {
        	return ipAddress;
        }else {
        	System.out.println("\n====================================================================================");
        	System.out.println("  "+ attribute + " Should be Valid[0-255] And Without Any Whitespaces & Alphabets!!");
        	System.out.println("  -> It should be from one of the below Ranges:");
        	System.out.println("  -> 132.xxx.xxx.xxx");
        	System.out.println("  -> 93.xxx.xxx.xxx");
        	System.out.println("  -> 182.xxx.xxx.xxx");
        	System.out.println("New IpAddress For Player Account To Transfer Must Not Be Same As Its Old IpAddress!!");
        	System.out.println("Because You Can't Transfer Player Account To The Same Server!!");
        	System.out.println("====================================================================================\n");
        	ipAddress = getNewIpaddress(attribute, oldIpaddress);
        }
        return ipAddress;
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
	
	public static void writeLog(String playerID,String logData) throws IOException {
		try {
			if (!Files.exists(Paths.get("src/client/logs/players/" + playerID + "Log.txt"))) {
				PrintWriter writer = new PrintWriter("src/client/logs/players/" + playerID + "Log.txt", "UTF-8");
				writer.close();
			}
			logData = new SimpleDateFormat("yyyy.MM.dd.HH.mm.ss").format(new Date()) + " : " + logData + "\n";
			Files.write(Paths.get("src/client/logs/players/" + playerID + "Log.txt"), logData.getBytes(), StandardOpenOption.APPEND);
		}catch (Exception e) {
			System.out.println("Error In Writig Logs : "+e.getMessage());
		}
	}
}