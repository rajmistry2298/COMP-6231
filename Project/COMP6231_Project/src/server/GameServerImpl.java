package server;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.rmi.RemoteException;
import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CountDownLatch;

import model.Player;
import service.ActivityLoggerService;
import util.ActionConstants;
import util.FieldConstants;
import util.FileConstants;
import util.LocationConstants;
import util.PortConstants;

public class GameServerImpl {
	
	public String serverLocation;
	private int portNo;
	private boolean isLeader;
	private List<String> locationList;
	private ConcurrentHashMap<String, List<Player>> accounts;
	private ConcurrentHashMap<String, String> executedRequests;

	public GameServerImpl(String location, final int portNo, final boolean isLeader) throws RemoteException,IOException{
		this.serverLocation = location;
		this.portNo = portNo;
		this.isLeader=isLeader;
		this.locationList = getLocationList(location);
		this.accounts = new ConcurrentHashMap<String, List<Player>>();
		this.executedRequests = new ConcurrentHashMap<String, String>();
		addInitialAccounts();
	}
	
	private List<String> getLocationList(final String location){
    	final List<String> locationList = new ArrayList<>();
    	locationList.add(LocationConstants.NORTHAMERICA + (location.length() > 2 ? location.substring(2) : ""));
    	locationList.add(LocationConstants.EUROPE + (location.length() > 2 ? location.substring(2) : ""));
    	locationList.add(LocationConstants.ASIA + (location.length() > 2 ? location.substring(2) : ""));
    	return locationList;
    }
	
	public int getPortNo() {
    	return portNo;	 
    }
	    
	public boolean isLeader() {
		return isLeader;
	}
	    
	public String getServerName() {
    	return serverLocation;
    }
	
	public boolean isRequestExecuted(final String requestId){
    	if(executedRequests.containsKey(requestId)) {
    		return true;
    	} else {
    		return false;
    	}
    }
    
	public synchronized String getResponse(final String requestId) {
    	return executedRequests.get(requestId);
    }
	
    public synchronized void addResponse(final String requestId, final String response) {
    	executedRequests.put(requestId, response);
    }
    
	public String createPlayerAccount(String firstName, String lastName, String age, String username, String password,String ipAddress) {
		if(this.getServerName().substring(11).equals("3")) {
			FileConstants.REQUEST_COUNT++;
		}
		
		String message=null;
        boolean valid = validateUsername(username);
        synchronized (this) {
          	if(valid) {
           		final Player player = new Player(firstName, lastName, age, username, password, ipAddress);
           		this.addRecord(Character.toString(username.charAt(0)), player);
           		message = "Player Account \""+username+"\" Successfully Created!!!";
           	}
           	else {
           		message = "Player Account With Username \""+username+"\" Is Alredy Exist!!!";
           	}
        }
 
        if(this.getServerName().substring(11).equals("3") && (FileConstants.REQUEST_COUNT == 4 || FileConstants.REQUEST_COUNT == 5 || FileConstants.REQUEST_COUNT == 6)) {
        	return "SOMETHING WENT WRONG ON THIS REPLICA";
        }
        
        return message;
	}

	public String playerSignIn(String username, String password){
		String message=null;	
		
		if(this.getServerName().substring(11).equals("3")) {
			FileConstants.REQUEST_COUNT++;
		}
		
		boolean valid = validateUsername(username);
        synchronized (this) {
          	if(valid) {
           		message ="Player Acount with Username \""+username+"\" does not exist!!!";
           	}
           	else {
           		for (Entry<String, List<Player>> entry : accounts.entrySet()) {   
           			if(Character.compare(username.toUpperCase().charAt(0),entry.getKey().charAt(0)) == 0) {
           	        	for(Player player : entry.getValue()){
           		            if(player.getUsername().equals(username)) {
           		            	if(player.getPassword().equals(password)) {
           		            		if(player.getStatus().equalsIgnoreCase("ONLINE")) {
           		            			message="Player Account \""+username+"\" Already Signed In!!!";
           		            		}
           		            		else {
           		            			player.setStatus("ONLINE");
           		            			message="Player Account \""+username+"\" Successfully Signed In!!!";
           		            		}
           		            	}
           		            	else {
           		            		message="You have Entered Invalid Password For Sigin In into Player Account \""+username+"\" !!!";
           		            	}
           		            	break;
           		            }
           		        }
           	        }	        
           	    }
           	}
		}
        
        if(this.getServerName().substring(11).equals("3") && (FileConstants.REQUEST_COUNT == 4 || FileConstants.REQUEST_COUNT == 5 || FileConstants.REQUEST_COUNT == 6)) {
        	return "SOMETHING WENT WRONG ON THIS REPLICA";
        }
        
		return message;
	}

	public String playerSignOut(String username){
		String message=null;
		
		if(this.getServerName().substring(11).equals("3")) {
			FileConstants.REQUEST_COUNT++;
		}
		
		boolean valid = validateUsername(username);
           synchronized (this) {
           	if(valid) {
           		message ="Player Acount with Username \""+username+"\" does not exist!!!";
           	}
           	else {
           		for (Entry<String, List<Player>> entry : accounts.entrySet()) {   
           			if(Character.compare(username.toUpperCase().charAt(0),entry.getKey().charAt(0)) == 0) {
           	        	for(Player player : entry.getValue()){
           		            if(player.getUsername().equals(username)) {
           		            		if(player.getStatus().equalsIgnoreCase("OFFLINE")) {
           		            			message="Player Account \""+username+"\" Already Signed Out!!!";
           		            		}
           		            		else {
           		            			player.setStatus("OFFLINE");
           		            			message="Player Account \""+username+"\" Successfully Signed Out!!!";
           		            		}
           		            		break;
           		            }
           		        }
           		    }
           	    }	        
           	}
           }
           
           if(this.getServerName().substring(11).equals("3") && (FileConstants.REQUEST_COUNT == 4 || FileConstants.REQUEST_COUNT == 5 || FileConstants.REQUEST_COUNT == 6)) {
           		return "SOMETHING WENT WRONG ON THIS REPLICA";
           }
           
           return message;
	}

	public synchronized String getPlayerStatusCount() {
		int online = 0;
		int offline = 0;
		for (Entry<String, List<Player>> entry : accounts.entrySet()) {   
   			for(Player player : entry.getValue()){
   		           	if(player.getStatus().equalsIgnoreCase("ONLINE")) {
   		           			online++;
   		            }
   		            else {
   		            	offline++;
   		            }
   		    }
   		}
		return "ONLINE : "+online+", OFFLINE : "+offline+". ";
    }
	
	public synchronized String getPlayerStatus(){
		String playerStatus = null;
		
		if(this.getServerName().substring(11).equals("3")) {
			FileConstants.REQUEST_COUNT++;
		}
		
		final ArrayList<String> otherServerPlayerStatus = new ArrayList<>();
    	final CountDownLatch latch = new CountDownLatch(locationList.size()-1);
    	
    	for (final String location : locationList) {
            if (location.equalsIgnoreCase(serverLocation)) {
            	playerStatus = serverLocation.substring(0,2) + ": " + this.getPlayerStatusCount();
            } else {
            	new Thread(() -> {
            		final String status = sendPlayerStatusRequest(location);
            		if(status != null)
            			otherServerPlayerStatus.add(status);
        			latch.countDown();
            	}).start();
            }
        }
        try {
			latch.await();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
   
        for(final String pStatus : otherServerPlayerStatus) {
        	if(!playerStatus.isEmpty()) {
        		playerStatus += ", ";
        	} 
        	playerStatus += pStatus.trim();
        }
        if(playerStatus.contains("NA")&&playerStatus.contains("EU")&&playerStatus.contains("AS")) {
        	 if(this.getServerName().substring(11).equals("3") && (FileConstants.REQUEST_COUNT == 4 || FileConstants.REQUEST_COUNT == 5 || FileConstants.REQUEST_COUNT == 6)) {
             	return "SOMETHING WENT WRONG ON THIS REPLICA";
             }
        	return playerStatus;
        }
        else {
        	FileConstants.REQUEST_COUNT--;
        	playerStatus = getPlayerStatus(); 
        }
        
        if(this.getServerName().substring(11).equals("3") && (FileConstants.REQUEST_COUNT == 4 || FileConstants.REQUEST_COUNT == 5 || FileConstants.REQUEST_COUNT == 6)) {
        	return "SOMETHING WENT WRONG ON THIS REPLICA";
        }
        return playerStatus;
	}
	
	private String sendPlayerStatusRequest(final String location) {
    	String playerStatus = null;
    	try {
			final InetAddress inetAddress = InetAddress.getByName("localhost");
			final int udpPort = PortConstants.getUdpPort(location);
			
			final DatagramSocket socket = new DatagramSocket();
			byte[] data = (serverLocation+ FieldConstants.SEPARATOR_ARROW +ActionConstants.GET_STATUS).toString().getBytes();
			final DatagramPacket dp = new DatagramPacket(data, data.length, inetAddress, udpPort);
			socket.send(dp);
			
			data = new byte[1000];
			socket.receive(new DatagramPacket(data, data.length));
			playerStatus = location.substring(0,2) + ": " + new String(data);
			socket.close();
			
		} catch (Exception e) {
			System.out.println(e.getMessage());
		}
    	return playerStatus;
    }

	private synchronized Player getPlayerFromMap(final String username) {
		for (Entry<String, List<Player>> entry : accounts.entrySet()) {   
   			if(Character.compare(username.toUpperCase().charAt(0),entry.getKey().charAt(0)) == 0) {
   	        	for(Player player : entry.getValue()){
   		            if(player.getUsername().equals(username)) {
						return player;
					}
				}
			}
        }
		return null;
    }
	
	public String getRemoteServer(String newIpAddress) {
		String remoteServer=null;
		String finalremoteServer=null;
		if(newIpAddress.split("\\.")[0].equals("132")) {
    		remoteServer = LocationConstants.NORTHAMERICA;
    	}
    	else if(newIpAddress.split("\\.")[0].equals("93")) {
    		remoteServer = LocationConstants.EUROPE;
    	}
    	else if(newIpAddress.split("\\.")[0].equals("182")) {
    		remoteServer = LocationConstants.ASIA;
    	}
		for (final String location : locationList) {
			if(location.startsWith(remoteServer)){
				finalremoteServer = location;
			}
		}
		return finalremoteServer;
    }
	
	public String transferAccount(String username, String password, String newIpAddress) {
		String message=null;
		
		if(this.getServerName().substring(11).equals("3")) {
			FileConstants.REQUEST_COUNT++;
		}
		
        boolean valid = validateUsername(username);
        boolean validPass = false;
        synchronized (this) {
          	try{
          		if(valid) {
           		message ="Player Acount With Username \""+username+"\" does not exist!!!";
           	}
           	else {
           		for (Entry<String, List<Player>> entry : accounts.entrySet()) {   
           			if(Character.compare(username.toUpperCase().charAt(0),entry.getKey().charAt(0)) == 0) {
           	        	for(Player player : entry.getValue()){
           		            if(player.getUsername().equals(username)) {
           		            	if(player.getPassword().equals(password)) {
           		            		validPass = true;
           		            	}
           		            	else {
           		            		message="You have Entered Invalid Password For Transfer Player Account \""+username+"\" !!!";
           		            	}	
           		            }
           		          }
           	        	}
           			}
           		}
          	if(validPass) {
          		final Player player = this.getPlayerFromMap(username);
          		final String playerValues = player.getValues();
				final String requestData = serverLocation + FieldConstants.SEPARATOR_ARROW + ActionConstants.TRANSFER_ACCOUNT + FieldConstants.SEPARATOR_ARROW + playerValues;
            	final ArrayList<String> transferAccountResults = new ArrayList<>();
            	final CountDownLatch latch = new CountDownLatch(1);
            	final String remoteServerLocation = getRemoteServer(newIpAddress);
            	
                new Thread(() -> {
                	final String result = new String(sendTransferAccountRequest(remoteServerLocation, requestData));
                	transferAccountResults.add(result);
        			latch.countDown();	// Release await() in the thread.
                }).start();
                
                try {
        			latch.await();
        		} catch (InterruptedException e) {
        			e.printStackTrace();
        		}
                
                message = transferAccountResults.get(0).trim();
                int Lenth = message.length();
                if(Lenth == 15) {
                	deletePlayerAccount(username);
                	message = "Player Account " + username + " Transfered Succesfully To "+ remoteServerLocation.substring(0,2) +" !!!";
                }
          	}
            }catch (Exception e) {
             	message = "Account Transfer Failed : "+ e.getMessage();
             }
          	
        }
        
        if(this.getServerName().substring(11).equals("3") && (FileConstants.REQUEST_COUNT == 4 || FileConstants.REQUEST_COUNT == 5 || FileConstants.REQUEST_COUNT == 6)) {
        	return "SOMETHING WENT WRONG ON THIS REPLICA";
        }

		return message;
	}

	private String sendTransferAccountRequest(final String location, final String data) {
		try {
			final InetAddress inetAddress = InetAddress.getByName("localhost");
			final int udpPort = PortConstants.getUdpPort(location);
			
			final DatagramSocket socket = new DatagramSocket();
			final DatagramPacket dp = new DatagramPacket(data.toString().getBytes(), data.toString().getBytes().length, inetAddress, udpPort);
			socket.send(dp);
			
			byte[] response = new byte[1000];
			socket.receive(new DatagramPacket(response, response.length));
			socket.close();
			return new String(response);

		} catch (Exception e) {
			return "Error In Transfering : "+ e.getMessage();
		}
	}
	
	public String transferPlayerAccount(final String playerData) {
		final String[] playerDetails = playerData.split(":");
		final String firstName = playerDetails[0];
		final String lastName = playerDetails[1];
		final String age = playerDetails[2];
		final String username = playerDetails[3];
		final String password = playerDetails[4];
		final String ipAddress = playerDetails[5];
		String message=null;
        boolean valid = validateUsername(username);
        synchronized (this) {
          	if(valid) {
           		final Player player = new Player(firstName, lastName, age, username, password, ipAddress);
           		this.addRecord(Character.toString(username.charAt(0)), player);
           		message = "TransferSuccess";
           		try {
           			ActivityLoggerService activityLogger1 = new ActivityLoggerService(FileConstants.CLIENT_LOG_FILE_PATH + this.serverLocation.substring(0,2) + "_" + username +FileConstants.FILE_TYPE);
                    activityLogger1.log("INFO", message+": Player Account "+username+" Successfully Created At "+ this.serverLocation +"!!!");
					
				} catch (IOException e) {
					System.out.println("Error In Writig Logs : "+e.getMessage());
				}
           	}
           	else {
           		message = "Transfere Unsuccessful Because Player Account With Username \""+username+"\" Is Alredy Exist On "+ this.serverLocation.substring(0,2) + "!!!";
           	}
        }
        return message;
	}
	
	private synchronized void deletePlayerAccount(final String username) {
        final String key = Character.toString(username.charAt(0));
        final List<Player> playerList = this.accounts.get(key.toUpperCase());
        if (playerList != null) {
            if(playerList.size() > 0) {
            	for(Player player : playerList) {
            		if(player.getUsername().equals(username)) {
            			playerList.remove(player);
                        if(playerList.size() == 0) {
                            this.accounts.remove(key);
                        }
                    break;
            		}
            	}
            }
        }
    }
	
	public String suspendAccount(String usernameToSuspend) {
		
		if(this.getServerName().substring(11).equals("3")) {
			FileConstants.REQUEST_COUNT++;
		}
		
		boolean userNotExist = validateUsername(usernameToSuspend);
		if(userNotExist) {
			
			if(this.getServerName().substring(11).equals("3") && (FileConstants.REQUEST_COUNT == 4 || FileConstants.REQUEST_COUNT == 5 || FileConstants.REQUEST_COUNT == 6)) {
	        	return "SOMETHING WENT WRONG ON THIS REPLICA";
			}
			return "Sorry!! Account "+ usernameToSuspend +" You Want To Suspend Does Not Exist!!";
		}
		deletePlayerAccount(usernameToSuspend);
		
		if(this.getServerName().substring(11).equals("3") && (FileConstants.REQUEST_COUNT == 4 || FileConstants.REQUEST_COUNT == 5 || FileConstants.REQUEST_COUNT == 6)) {
        	return "SOMETHING WENT WRONG ON THIS REPLICA";
		}
		
		return "Player Account "+usernameToSuspend+" Successfully Suspended!!!";
	}
	
	private synchronized void addInitialAccounts() {
		final Player player = new Player("Raj", "Mistry", "22", "rajmistry2298", "R@mistry", "132.1.1.1");
   		this.addRecord("R", player);
   		final Player player1 = new Player("John", "Cena", "54", "johncena54", "J@cena54", "93.1.1.2");
   		this.addRecord("J", player1);
   		final Player player2 = new Player("Paul", "Heyman", "59", "paulheymanguy", "P@heyman", "182.2.1.2");
   		this.addRecord("P", player2);
   		final Player player3 = new Player("User", "Name", "65", "username", "password", "182.2.2.2");
   		this.addRecord("U", player3);
   		final Player player4 = new Player("Lana", "Belitchka", "24", "lanabelitchka", "L@belitchka", "132.2.1.2");
   		this.addRecord("L", player4);
	}
	
	private synchronized boolean validateUsername(final String username) {
		boolean uservalid = true;
		for (Entry<String, List<Player>> entry : accounts.entrySet()) {   
			if(Character.compare(username.toUpperCase().charAt(0),entry.getKey().charAt(0)) == 0) {
	        	for(Player player : entry.getValue()){
		            if(player.getUsername().equals(username)) {
		            	uservalid=false;
		            	break;
		            }
		        }
	        }	        
	    }
        return uservalid;
    }
	
	private synchronized void addRecord(final String key, final Player account) {
		List<Player> recordList = this.accounts.get(key.toUpperCase());
        if (recordList != null) {
            recordList.add(account);
        } else {
            recordList = new ArrayList<>();
            recordList.add(account);
            this.accounts.put(key.toUpperCase(), recordList);
        }
    }

	private synchronized String getPlayerData() {
		String Data="";
		for (Entry<String, List<Player>> entry : accounts.entrySet()) {   
   			for(Player player : entry.getValue()){
   		           	Data += player.getValues()+FieldConstants.SEPARATOR_PIPE;
   		    }
   		}
		return Data.substring(0,Data.length()-3);
	}
	
	public synchronized String sendDataToReplica3(final String serverName) {
		DatagramSocket socket = null;
		try {
			socket = new DatagramSocket();
			final InetAddress inetAddress = LocationConstants.getInetAddress(LocationConstants.RM3);
			final int udpPort = PortConstants.getUdpPort(LocationConstants.RM3);
		
			String playerData=getPlayerData();
			String data = this.getServerName() + FieldConstants.SEPARATOR_ARROW + "Sent Recovery Data" + FieldConstants.SEPARATOR_ARROW + playerData;
			System.out.println(DateFormat.getDateTimeInstance().format(new Date())+FieldConstants.SEPARATOR_ARROW+"INFO : ====="
					+ serverName+" has Sent Correct Data TO RM3=====");
			DatagramPacket dp = new DatagramPacket(data.getBytes(), data.getBytes().length, inetAddress, udpPort);
			socket.send(dp);
			

		} catch (Exception e) {
			return "Error In Transfering : "+ e.getMessage();
		}finally {
			if(socket!=null) {
				socket.close();
			}
		}

		return "Recovery Data Successfully Sent To "+ this.getServerName().substring(0,2)+" 3";
	}

	public synchronized String recoverDataFromOtherReplica(String data) {
		this.accounts.clear();
		String[] data1 = data.trim().split(FieldConstants.SEPARATOR_PIPE);
		for(String data2: data1) {
			String[] playerDetails = data2.split(":");
			Player player1 = new Player(playerDetails[0],playerDetails[1],playerDetails[2],playerDetails[3],playerDetails[4],playerDetails[5]);
			player1.setStatus(playerDetails[6]);
			addRecord(Character.toString(playerDetails[3].charAt(0)), player1);
		}
		System.out.println(DateFormat.getDateTimeInstance().format(new Date())+FieldConstants.SEPARATOR_ARROW+"INFO : ====="
				+ this.getServerName()+" Recovered Data Successfully.=====");
		return this.getServerName()+" Data Recovered Successfully.";
	}
	
}