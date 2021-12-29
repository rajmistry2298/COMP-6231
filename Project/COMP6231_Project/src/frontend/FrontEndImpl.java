package frontend;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.PortUnreachableException;
import java.net.SocketTimeoutException;
import java.util.ArrayList;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CountDownLatch;

import FrontEndApp.FrontEndPOA;
import model.Player;
import service.ActivityLoggerService;
import service.CounterService;
import util.ActionConstants;
import util.FieldConstants;
import util.FileConstants;
import util.LocationConstants;
import util.PortConstants;

/**
 * Implementation of methods of FrontEnd interface
 * @author <a href="mailto:rajmistry2298@gmail.com">Raj Mistry</a>
 */
public class FrontEndImpl extends FrontEndPOA {
	
	private String serverName;
    private ActivityLoggerService activityLogger;
    private ConcurrentHashMap<String, String> leaderReplicas;
    private ConcurrentHashMap<String, Integer> leaderReplicaPortNumbers;

    public FrontEndImpl(final String serverName, final ActivityLoggerService activityLogger) throws IOException {
    	this.serverName = serverName;
        this.activityLogger = activityLogger;
        this.leaderReplicas = getLeaderReplicas();
        this.leaderReplicaPortNumbers = getLeaderReplicaPortNumbers();
    }
    
    private ConcurrentHashMap<String, String> getLeaderReplicas(){
    	final ConcurrentHashMap<String, String> primaryReplicas = new ConcurrentHashMap<>();
    	primaryReplicas.put(LocationConstants.NORTHAMERICA, LocationConstants.NORTHAMERICA_REPLICA_1);
    	primaryReplicas.put(LocationConstants.EUROPE, LocationConstants.EUROPE_REPLICA_1);
    	primaryReplicas.put(LocationConstants.ASIA, LocationConstants.ASIA_REPLICA_1);
    	return primaryReplicas;
    }
    
    public synchronized String getLeaderReplica(final String location) {
    	return leaderReplicas.get(location);
    }
    
    private ConcurrentHashMap<String, Integer> getLeaderReplicaPortNumbers(){
    	final ConcurrentHashMap<String, Integer> primaryReplicaPortNumbers = new ConcurrentHashMap<>();
    	primaryReplicaPortNumbers.put(LocationConstants.NORTHAMERICA, PortConstants.NA_REPLICA_1_UDP_PORT);
    	primaryReplicaPortNumbers.put(LocationConstants.EUROPE, PortConstants.EU_REPLICA_1_UDP_PORT);
    	primaryReplicaPortNumbers.put(LocationConstants.ASIA, PortConstants.AS_REPLICA_1_UDP_PORT);
    	return primaryReplicaPortNumbers;
    }

    public synchronized Integer getLeaderReplicaPortNo(final String location) {
    	return leaderReplicaPortNumbers.get(location);
    }
    
	@Override
	public String createPlayerAccount(String firstName, String lastName, String age, String username, String password,String ipAddress) {
		String response = "";
        String requestId;
		try {
			requestId = "REQ" + String.format("%05d", CounterService.getCounter(FileConstants.REQUEST_COUNTER_FILE_PATH));
			final Player player = new Player(firstName, lastName, age, username, password, ipAddress);
			final String request = serverName + FieldConstants.SEPARATOR_ARROW + ActionConstants.CREATE_PLAY + 
												FieldConstants.SEPARATOR_ARROW + requestId + FieldConstants.SEPARATOR_ARROW + player.getValues();
			response = sendRequest(request, LocationConstants.getLocation(ipAddress), ActionConstants.CREATE_PLAY); 
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
		return response;
	}

	@Override
	public String playerSignIn(String username, String password, String ipAddress) {
		String response = "";
        String requestId;
		try {
			requestId = "REQ" + String.format("%05d", CounterService.getCounter(FileConstants.REQUEST_COUNTER_FILE_PATH));
			final String request = serverName + FieldConstants.SEPARATOR_ARROW + ActionConstants.PLAY_SIN + 
												FieldConstants.SEPARATOR_ARROW + requestId + FieldConstants.SEPARATOR_ARROW + username + 
												FieldConstants.SEPARATOR_ARROW + password +
												FieldConstants.SEPARATOR_ARROW + ipAddress;
			response = sendRequest(request, LocationConstants.getLocation(ipAddress), ActionConstants.PLAY_SIN);
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
		return response;
	}

	@Override
	public String playerSignOut(String username, String ipAddress) {
		String response = "";
        String requestId;
		try {
			requestId = "REQ" + String.format("%05d", CounterService.getCounter(FileConstants.REQUEST_COUNTER_FILE_PATH));
			final String request = serverName + FieldConstants.SEPARATOR_ARROW + ActionConstants.PLAY_SOUT + 
												FieldConstants.SEPARATOR_ARROW + requestId + FieldConstants.SEPARATOR_ARROW + username +
												FieldConstants.SEPARATOR_ARROW + ipAddress;
			response = sendRequest(request, LocationConstants.getLocation(ipAddress), ActionConstants.PLAY_SOUT);
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
		return response;
	}

	@Override
	public String transferAccount(String username, String password, String oldIpAddress, String newIpAddress) {
		String response = "";
		String requestId;
		try {
			requestId = "REQ" + String.format("%05d", CounterService.getCounter(FileConstants.REQUEST_COUNTER_FILE_PATH));
			final String request = serverName + FieldConstants.SEPARATOR_ARROW + ActionConstants.TRANSFERACCOUNT + 
												FieldConstants.SEPARATOR_ARROW + requestId + 
												FieldConstants.SEPARATOR_ARROW + username +
												FieldConstants.SEPARATOR_ARROW + password +
												FieldConstants.SEPARATOR_ARROW + oldIpAddress +
												FieldConstants.SEPARATOR_ARROW + newIpAddress;
			response = sendRequest(request, LocationConstants.getLocation(oldIpAddress), ActionConstants.TRANSFERACCOUNT);
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
		return response;
	}

	@Override
	public String getPlayerStatus(String ipAddress) {
		String response = "";
		String requestId;
		try {
			requestId = "REQ" + String.format("%05d", CounterService.getCounter(FileConstants.REQUEST_COUNTER_FILE_PATH));
			final String request = serverName + FieldConstants.SEPARATOR_ARROW + ActionConstants.GETSTATUS + 
												FieldConstants.SEPARATOR_ARROW + requestId + 
												FieldConstants.SEPARATOR_ARROW + ipAddress;
			response = sendRequest(request, LocationConstants.getLocation(ipAddress), ActionConstants.GETSTATUS);
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
		return response;
	}

	@Override
	public String suspendAccount(String usernameToSuspend, String ipAddress) {
		String response = "";
        String requestId;
		try {
			requestId = "REQ" + String.format("%05d", CounterService.getCounter(FileConstants.REQUEST_COUNTER_FILE_PATH));
		    final String request = serverName + FieldConstants.SEPARATOR_ARROW + ActionConstants.SUSPEND_PLAY + 
												FieldConstants.SEPARATOR_ARROW + requestId + FieldConstants.SEPARATOR_ARROW + usernameToSuspend +
												FieldConstants.SEPARATOR_ARROW + ipAddress;
		    response = sendRequest(request, LocationConstants.getLocation(ipAddress), ActionConstants.SUSPEND_PLAY); 
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
        return response;
	}
	
	private String sendRequest(final String request, final String location, final String action) {
		
		final ArrayList<String> response = new ArrayList<>();
    	final CountDownLatch latch = new CountDownLatch(1);
        
        new Thread(() -> {
        	response.add(sendUdpRequest(request, location, action));
        	latch.countDown();
        }).start();
        
        try {
			latch.await();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
        return response.get(0);
    }
    
    private String sendUdpRequest(final String request, final String location, final String action) {
    	String response = null;
    	DatagramSocket socket = null;
    	try {
    		socket = new DatagramSocket();
    		activityLogger.log("INFO", String.format("UDP %s request has been sent to %s Server.", action, getLeaderReplica(location)));

    		final long beginTime = System.currentTimeMillis();
    		while(true) {
    			final DatagramPacket packet = new DatagramPacket(request.getBytes(), request.getBytes().length, LocationConstants.getInetAddress(location), getLeaderReplicaPortNo(location));
    			socket.send(packet);
    			
    			try {
    				socket.setSoTimeout(5000);
    				byte[] data = new byte[1000];
    				socket.receive(new DatagramPacket(data, data.length));
    				response = new String(data);
    				activityLogger.log("INFO", String.format("UDP %s response has been received from %s Server. :\n "+response, action, getLeaderReplica(location)));
    				break;
    			} catch (SocketTimeoutException | PortUnreachableException e) {
    				final long currentTime = System.currentTimeMillis();
    				// 10 Seconds timeout so that client don't wait forever
    				final double timeout = (currentTime - beginTime) / 1000 ;
    				if(timeout > 10) {
    					activityLogger.log("ERROR", String.format("No response has been sent from %s Server.", getLeaderReplica(location)));
    					response = false + FieldConstants.SEPARATOR_ARROW + String.format("No response has been sent from %s Server.", getLeaderReplica(location));
    					break;
    				}
    			} 
    		}
		} catch (Exception e) {
			activityLogger.log("ERROR", e.getMessage());
			response = false + FieldConstants.SEPARATOR_ARROW + e.getMessage();
		} finally {
			if(socket != null) {
				socket.close();
			}
		}
    	return response;
    }
}