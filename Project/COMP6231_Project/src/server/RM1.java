package server;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;

import server.replicas.ASServerReplica1;
import server.replicas.EUServerReplica1;
import server.replicas.NAServerReplica1;
import util.FieldConstants;
import util.LocationConstants;
import util.PortConstants;

/**
 * Replica 1 Manager
 * @author <a href="mailto:rajmistry2298@gmail.com">Raj Mistry</a>
 */
public class RM1 {

		public static void main(String[] args) {
	    	
	    	String[] args1 = {LocationConstants.NORTHAMERICA_REPLICA_1, String.valueOf(PortConstants.NA_REPLICA_1_UDP_PORT),"true"};
	    	String[] args2 = {LocationConstants.EUROPE_REPLICA_1, String.valueOf(PortConstants.EU_REPLICA_1_UDP_PORT),"true"};
	    	String[] args3 = {LocationConstants.ASIA_REPLICA_1, String.valueOf(PortConstants.AS_REPLICA_1_UDP_PORT),"true"};
	    	
	    	NAServerReplica1.main(args1);
	    	EUServerReplica1.main(args2);
	    	ASServerReplica1.main(args3);
	    	
	    	new Thread(() -> {
	    		startUDPServer();
            }).start();
	    
		}
		
		private static void startUDPServer() {
	        DatagramSocket socket = null;
			try {
				socket = new DatagramSocket(PortConstants.getUdpPort(LocationConstants.RM1));

				while (true) {
					try {
						final byte[] data = new byte[4096];
						final DatagramPacket dp = new DatagramPacket(data, data.length);
						socket.receive(dp);
						
						new Thread(() -> {
							processUDPRequest(dp);
			        	}).start();
					} catch (IOException e) {
						System.out.println(e.getMessage());
					}
				}
			} catch (SocketException e1) {
				System.out.println(e1.getMessage());
			} finally {
	            if (socket != null) {
	            	socket.close();
	            }
	        }
	    }
	    
	    private static void processUDPRequest(final DatagramPacket dp) {
	    	DatagramSocket socket = null;
	        final String request = new String(dp.getData()).trim();
	        final String[] dpData = request.split(FieldConstants.SEPARATOR_ARROW);
	        final String requestType = dpData[1].trim();
	        
	        try {
	            socket = new DatagramSocket();
	            
	            if("Send Recovery Data To RM3".equalsIgnoreCase(requestType)) {
	            	byte[] replicarequest = (LocationConstants.RM1+FieldConstants.SEPARATOR_ARROW+"Send Recovery Data To RM3").getBytes();
	            	socket.send(new DatagramPacket(replicarequest, replicarequest.length, LocationConstants.getInetAddress(LocationConstants.NORTHAMERICA_REPLICA_1), PortConstants.getUdpPort(LocationConstants.NORTHAMERICA_REPLICA_1)));
	            	socket.send(new DatagramPacket(replicarequest, replicarequest.length, LocationConstants.getInetAddress(LocationConstants.EUROPE_REPLICA_1), PortConstants.getUdpPort(LocationConstants.EUROPE_REPLICA_1)));
	            	socket.send(new DatagramPacket(replicarequest, replicarequest.length, LocationConstants.getInetAddress(LocationConstants.ASIA_REPLICA_1), PortConstants.getUdpPort(LocationConstants.ASIA_REPLICA_1)));
	            } 
	            else if("Recover Your Data".equalsIgnoreCase(requestType)) {
	            	byte[] replicarequest = (LocationConstants.RM1+FieldConstants.SEPARATOR_ARROW+"Send Recovery Data To RM3").getBytes();
	            	socket.send(new DatagramPacket(replicarequest, replicarequest.length, LocationConstants.getInetAddress(LocationConstants.RM2), PortConstants.getUdpPort(LocationConstants.RM2)));
	            }            
	        } catch (IOException e) {
	        	System.out.println(e.getMessage());
	        } finally {
	            if (socket != null) {
	            	socket.close();
	            }
	        }
	    }
}