package server;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;

import server.udp.UdpRequest;
import server.udp.UdpServer;
import util.LocationConstants;
import interfaces.GameServer;
import model.Player;

public class GameServerImpl extends UnicastRemoteObject implements GameServer {

	private static final long serialVersionUID = 1L;
	
	public String serverLocation;
	private HashMap<String, List<Player>> accounts;
	private List<String> locationList = Arrays.asList(LocationConstants.NORTHAMERICA, LocationConstants.EUROPE, LocationConstants.ASIA);
	private UdpServer udpServer;

	public GameServerImpl(String location) throws RemoteException,IOException{
		this.serverLocation = location;
		this.accounts = new HashMap<String, List<Player>>();
		addInitialAccounts();
		this.udpServer = new UdpServer(this);
        this.udpServer.start();
	}
	
	public synchronized String getPlayerCount() {
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

	@Override
	public String createPlayerAccount(String firstName, String lastName, String age, String username, String password,String ipAddress) throws RemoteException {
		String message=null;
        boolean valid = validateUsername(username);
        synchronized (this) {
          	if(valid) {
           		final Player player = new Player(firstName, lastName, age, username, password, ipAddress);
           		this.addRecord(Character.toString(username.charAt(0)), player);
           		message = "Player Account \""+username+"\" Successfully Created!!!";
           		try {
					writeLog(message,this.serverLocation);
				} catch (IOException e) {
					e.printStackTrace();
				}
           	}
           	else {
           		message = "Player Account With Username \""+username+"\" Is Alredy Exist!!!";
           		try {
					writeLog("PlayerClient Tried to Create A "+message,this.serverLocation);
				} catch (IOException e) {
					e.printStackTrace();
				}
           	}
        }
        return message;
	}

	@Override
	public String playerSignIn(String username, String password) throws RemoteException {
		String message=null;	
        boolean valid = validateUsername(username);
        synchronized (this) {
          	if(valid) {
           		message ="Player Acount with Username \""+username+"\" does not exist!!!";
           		try {
					writeLog("PlayerClient Tried to Sign In But "+message,this.serverLocation);
				} catch (IOException e) {
					e.printStackTrace();
				}
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
           		            	try {
           							writeLog(message,this.serverLocation);
           						} catch (IOException e) {
           							e.printStackTrace();
           						}
           		            	break;
           		            }
           		        }
           	        }	        
           	    }
           	}
		}
		return message;
	}

	@Override
	public String playerSignOut(String username) throws RemoteException {
		String message=null;
		boolean valid = validateUsername(username);
           synchronized (this) {
           	if(valid) {
           		message ="Player Acount with Username \""+username+"\" does not exist!!!";
           		try {
					writeLog("PlayerClient Tried to Sign Out But "+message,this.serverLocation);
				} catch (IOException e) {
					e.printStackTrace();
				}
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
           		            		try {
               							writeLog(message,this.serverLocation);
               						} catch (IOException e) {
               							e.printStackTrace();
               						}
           		            		break;
           		            }
           		        }
           		    }
           	    }	        
           	}
           }
           return message;
	}

	@Override
	public String getPlayerStatus() throws RemoteException {
		String playerCount = null;
        final UdpRequest[] requestIssuedReference = new UdpRequest[locationList.size() - 1];
        int count = 0;
        for (final String location : locationList) {
            if (location == serverLocation) {
                playerCount = serverLocation + ": " + this.getPlayerCount();
            } else {
                try {
                	requestIssuedReference[count] = new UdpRequest(location, serverLocation);
                } catch (IOException e) {
                    System.out.println(e.getMessage());
                }
                requestIssuedReference[count].start();
                count++;
            }
        }
        
        for (final UdpRequest request : requestIssuedReference) {
            try {
                request.join();
            } catch (InterruptedException e) {
                System.out.println(e.getMessage());
            }
            playerCount += " , " + request.getRemotePlayerCount().trim();
        }
        try {
				writeLog("Admin Requested To Get Player Status. And Status Of All Player Account Returned After Getting Status from Other 2 Server : "+playerCount,this.serverLocation);
			} catch (IOException e) {
				e.printStackTrace();
			}
		return playerCount;
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
	
	private synchronized void addInitialAccounts() {
		final Player player = new Player("Raj", "Mistry", "22", "rajmistry2298", "R@mistry", "132.1.1.1");
   		this.addRecord("R", player);
   		final Player player1 = new Player("John", "Cena", "54", "johncena54", "J@cena", "132.1.1.2");
   		this.addRecord("J", player1);
   		final Player player2 = new Player("Paul", "Heyman", "59", "paulheymanguy", "P@heyman", "182.2.1.2");
   		this.addRecord("P", player2);
	}
	
	public static void writeLog(String logData, String location) throws IOException {
		logData = new SimpleDateFormat("yyyy.MM.dd.HH.mm.ss").format(new Date()) + " : " + logData + "\n";
		Files.write(Paths.get("src/server/logs/"+location+ "_Server_Log.txt"), logData.getBytes(), StandardOpenOption.APPEND);
	}
}
