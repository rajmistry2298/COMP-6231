package server;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.rmi.RemoteException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CountDownLatch;

import javax.jws.WebService;
import javax.xml.ws.Endpoint;

import interfaces.GameServer;
import model.Player;
import util.LocationConstants;
import util.PortConstants;

/**
 * NA Server Class
 * Create separate server instance for NA location
 * Register those to the registry service to expose interface APIs
 * @author <a href="mailto:rajmistry2298@gmail.com">Raj Mistry</a>
 */
@WebService(endpointInterface = "interfaces.GameServer")
public class NAServer implements GameServer {
	
	public String serverLocation = LocationConstants.NORTHAMERICA;
	private List<String> locationList = Arrays.asList(LocationConstants.NORTHAMERICA, LocationConstants.EUROPE, LocationConstants.ASIA);
	private ConcurrentHashMap<String, List<Player>> accounts;

	public NAServer() throws RemoteException,IOException{
		this.accounts = new ConcurrentHashMap<String, List<Player>>();
		addInitialAccounts();
	}
	
	public static void main(String[] args){
		try {
			writeLog("NA Server Started!!!");
            
			final NAServer gameServerImpl = new NAServer();
			
            /*Binding it to service registry --> WSDL file is created*/
        	Endpoint endpoint = Endpoint.publish(LocationConstants.NORTHAMERICA_ADDRESS, gameServerImpl);
        	
        	System.out.println("North-America Service Is Published : " + endpoint.isPublished());
        	writeLog("North-America Service Is Published : " + endpoint.isPublished());
        	
            /*Start UDP server for NA*/
            new Thread(() -> {
            	startUDPServer(gameServerImpl);
            }).start();
         
	        System.out.println("#================= North-America Server is started =================#");	        
		}catch(Exception e) {
			System.out.println("EXCEPTION :: "+ e.getMessage());
		}
	}
	
	private static void startUDPServer(final NAServer gameServer) {
        DatagramSocket socket = null;
		try {
			socket = new DatagramSocket(PortConstants.getUdpPort(gameServer.getServerName()));
			writeLog("NA UDP Server Started!!!");
			while (true) {
				try {
					final byte[] data = new byte[4096];
					final DatagramPacket dp = new DatagramPacket(data, data.length);
					socket.receive(dp);
				
					new Thread(() -> {
						processUDPRequest(gameServer, dp);
		        	}).start();
				} catch (IOException e) {
					System.out.println(e.getMessage());
				}
			}
		} catch (SocketException e1) {
			System.out.println(e1.getMessage());
		} catch (IOException e1) {
			System.out.println("Error In Writing Logs: "+e1.getMessage());
		} finally {
            if (socket != null) {
            	socket.close();
            }
        }
    }
    
    private static void processUDPRequest(final NAServer gameServer, final DatagramPacket dp) {
    	byte[] response;
        DatagramSocket socket = null;
        final String request = new String(dp.getData()).trim();
        final String[] dpData = request.split(",");
        final String requestType = dpData[1].trim();
        try {
			writeLog("UDP Request received :"+requestType);
		} catch (IOException e1) {
			System.out.println("Error In Writing Logs: "+e1.getMessage());
		}
        try {
            socket = new DatagramSocket();
            
            if("GETSTATUS".equalsIgnoreCase(requestType)) {
            	response = gameServer.getPlayerStatusCount().toString().getBytes();
            	socket.send(new DatagramPacket(response, response.length, dp.getAddress(), dp.getPort()));
            	try {
        			writeLog("Response Sent For :"+requestType);
        		} catch (IOException e1) {
        			System.out.println("Error In Writing Logs: "+e1.getMessage());
        		}
            } else if("TRANSFERACCOUNT".equalsIgnoreCase(requestType)) {
            	final String playerData = dpData[2].trim();
            	response = gameServer.transferPlayerAccount(playerData).getBytes();
            	socket.send(new DatagramPacket(response, response.length, dp.getAddress(), dp.getPort()));
            	try {
        			writeLog("Response Sent For :"+requestType);
        		} catch (IOException e1) {
        			System.out.println("Error In Writing Logs: "+e1.getMessage());
        		}
            }            
        } catch (IOException e) {
        	System.out.println(e.getMessage());
        } finally {
            if (socket != null) {
            	socket.close();
            }
        }
    }
    
    public static void writeLog(String logData) throws IOException {
		if (!Files.exists(Paths.get("src/server/logs/NA_Server_Log.txt"))) {
			PrintWriter writer = new PrintWriter("src/server/logs/NA_Server_Log.txt", "UTF-8");
			writer.println(new SimpleDateFormat("yyyy.MM.dd.HH.mm.ss").format(new Date()) + " : NA Log File Crated!!");
			writer.close();
		}
		logData = new SimpleDateFormat("yyyy.MM.dd.HH.mm.ss").format(new Date()) + " : " + logData + "\n";
		Files.write(Paths.get("src/server/logs/NA_Server_Log.txt"), logData.getBytes(), StandardOpenOption.APPEND);
	}

    public String getServerName() {
    	return serverLocation;
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

	public String createPlayerAccount(String firstName, String lastName, String age, String username, String password,String ipAddress) {
		String message=null;
        boolean valid = validateUsername(username);
        synchronized (this) {
          	if(valid) {
           		final Player player = new Player(firstName, lastName, age, username, password, ipAddress);
           		this.addRecord(Character.toString(username.charAt(0)), player);
           		message = "Player Account \""+username+"\" Successfully Created!!!";
				try {
					writeLog(message);
				} catch (IOException e) {
					System.out.println("Error In Writig Logs : "+e.getMessage());
				}
           	}
           	else {
           		message = "Player Account With Username \""+username+"\" Is Alredy Exist!!!";
           		try {
					writeLog("PlayerClient Tried to Create A "+message);
				} catch (IOException e) {
					System.out.println("Error In Writig Logs : "+e.getMessage());
				}
           	}
        }
        return message;
	}

	public String playerSignIn(String username, String password){
		String message=null;	
        boolean valid = validateUsername(username);
        synchronized (this) {
          	if(valid) {
           		message ="Player Acount with Username \""+username+"\" does not exist!!!";
           		try {
					writeLog("PlayerClient Tried to Sign In But "+message);
				} catch (IOException e) {
					System.out.println("Error In Writig Logs : "+e.getMessage());
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
           							writeLog(message);
           						} catch (IOException e) {
           							System.out.println("Error In Writig Logs : "+e.getMessage());
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

	public String playerSignOut(String username){
		String message=null;
		boolean valid = validateUsername(username);
           synchronized (this) {
           	if(valid) {
           		message ="Player Acount with Username \""+username+"\" does not exist!!!";
           		try {
					writeLog("PlayerClient Tried to Sign Out But "+message);
				} catch (IOException e) {
					System.out.println("Error In Writig Logs : "+e.getMessage());
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
               							writeLog(message);
               						} catch (IOException e) {
               							System.out.println("Error In Writig Logs : "+e.getMessage());
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

	public String getPlayerStatus(){
		String playerStatus = null;
		final ArrayList<String> otherServerPlayerStatus = new ArrayList<>();
    	final CountDownLatch latch = new CountDownLatch(locationList.size()-1);
    	
    	for (final String location : locationList) {
            if (location.equalsIgnoreCase(serverLocation)) {
            	playerStatus = serverLocation + ": " + this.getPlayerStatusCount();
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
        	try {
				writeLog("Admin Requested To Get Player Status. And Status Of All Player Account Returned After Getting Status from Other 2 Server : "+playerStatus);
			} catch (IOException e) {
				System.out.println("Error In Writig Logs : "+e.getMessage());
			}
        	return playerStatus;
        }
        else {
        	playerStatus = getPlayerStatus(); 
        }
        return playerStatus;
	}
	
	private String sendPlayerStatusRequest(final String location) {
    	String playerStatus = null;
    	try {
			final InetAddress inetAddress = InetAddress.getByName("localhost");
			final int udpPort = PortConstants.getUdpPort(location);
			
			final DatagramSocket socket = new DatagramSocket();
			byte[] data = (serverLocation+",GETSTATUS").toString().getBytes();
			final DatagramPacket dp = new DatagramPacket(data, data.length, inetAddress, udpPort);
			socket.send(dp);
			try {
				writeLog("UDP Request \"GETSTATUS\" Sent To "+location);
			} catch (IOException e) {
				System.out.println("Error In Writig Logs : "+e.getMessage());
			}
			
			data = new byte[4096];
			socket.receive(new DatagramPacket(data, data.length));
			playerStatus = location + ": " + new String(data);
			try {
				writeLog("UDP Response For \"GETSTATUS\" Received From "+location);
			} catch (IOException e) {
				System.out.println("Error In Writig Logs : "+e.getMessage());
			}
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
	
	public String transferAccount(String username, String password, String newIpAddress) {
		String message=null;	
        boolean valid = validateUsername(username);
        boolean validPass = false;
        synchronized (this) {
          	try{
          		if(valid) {
           		message ="Player Acount With Username \""+username+"\" does not exist!!!";
           		try {
					writeLog("PlayerClient Tried TO Transfer Account But "+message);
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
           		            		validPass = true;
           		            	}
           		            	else {
           		            		message="You have Entered Invalid Password For Transfer Player Account \""+username+"\" !!!";
           		            		try {
           		 					writeLog("PlayerClient Tried TO Transfer Account But Entered Invalid Password For "+username+"!!!");
           		 				} catch (IOException e) {
           		 					e.printStackTrace();
           		 				}
           		            	}	
           		            }
           		          }
           	        	}
           			}
           		}
          	if(validPass) {
          		final Player player = this.getPlayerFromMap(username);
          		final String playerValues = player.getValues();
				final String requestData = serverLocation + "," + "TRANSFERACCOUNT" + "," + playerValues + ":" + newIpAddress;
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
                	message = "Player Account " + username + " Transfered Succesfully To "+ remoteServerLocation +" !!!";
                	try {
    					writeLog(message);
    				} catch (IOException e) {
    					e.printStackTrace();
    				}
                }
          	}
            }catch (Exception e) {
             	message = "Account Transfer Failed : "+ e.getMessage();
             }
          	
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
			try {
				writeLog("UDP Request \"TRANSFERACCOUNT\" Sent To "+location);
			} catch (IOException e) {
				System.out.println("Error In Writig Logs : "+e.getMessage());
			}
			
			byte[] response = new byte[4096];
			socket.receive(new DatagramPacket(response, response.length));
			try {
				writeLog("UDP Response For \"TRANSFERACCOUNT\" Received From "+location);
			} catch (IOException e) {
				System.out.println("Error In Writig Logs : "+e.getMessage());
			}
			socket.close();

			return new String(response);
		} catch (Exception e) {
			return "Error In Transfering : "+ e.getMessage();
		}
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
	
	public String getRemoteServer(String newIpAddress) {
		String remoteServer=null;
		if(newIpAddress.split("\\.")[0].equals("132")) {
    		remoteServer = LocationConstants.NORTHAMERICA;
    	}
    	else if(newIpAddress.split("\\.")[0].equals("93")) {
    		remoteServer = LocationConstants.EUROPE;
    	}
    	else if(newIpAddress.split("\\.")[0].equals("182")) {
    		remoteServer = LocationConstants.ASIA;
    	}
		return remoteServer;
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
					PrintWriter writer = new PrintWriter("src/client/logs/players/" + this.serverLocation + "_" + username + "_" + "Log.txt", "UTF-8");
					writer.println(new SimpleDateFormat("yyyy.MM.dd.HH.mm.ss").format(new Date()) + " : " + message+" => Playe Account Successfully Created.");
					writer.close();
    				writeLog(message+": Player Account "+username+" Successfully Created At "+ this.serverLocation +"!!!");
				} catch (IOException e) {
					System.out.println("Error In Writig Logs : "+e.getMessage());
				}
           	}
           	else {
           		message = "Transfere Unsuccessful Because Player Account With Username \""+username+"\" Is Alredy Exist On "+ this.serverLocation + "!!!";
           		try {
					writeLog(message);
				} catch (IOException e) {
					System.out.println("Error In Writig Logs : "+e.getMessage());
				}
           	}
        }
        return message;
	}
	
	public String suspendAccount(String usernameToSuspend) {
		boolean userNotExist = validateUsername(usernameToSuspend);
		if(userNotExist) {
			try {
				writeLog("Admin Tried To Suspend Account But Player Account \""+usernameToSuspend+"\" Does Not Exist!!!");
			} catch (IOException e) {
				System.out.println("Error In Writig Logs : "+e.getMessage());
			}
			return "Sorry!! Account "+ usernameToSuspend +" You Want To Suspend Does Not Exist!!";
		}
		deletePlayerAccount(usernameToSuspend);
		try {
			writeLog("Admin Successfully Suspended Player Account \""+usernameToSuspend+"\" !!!");
		} catch (IOException e) {
			System.out.println("Error In Writig Logs : "+e.getMessage());
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
}