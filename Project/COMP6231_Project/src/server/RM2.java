package server;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;
import java.text.DateFormat;
import java.util.Date;

import server.replicas.ASServerReplica2;
import server.replicas.EUServerReplica2;
import server.replicas.NAServerReplica2;
import util.FieldConstants;
import util.LocationConstants;
import util.PortConstants;

/**
 * Replica 2 Manager
 * @author <a href="mailto:rajmistry2298@gmail.com">Raj Mistry</a>
 */
public class RM2 {
	public static void main(String[] args) {
    	
    	String[] args1 = {LocationConstants.NORTHAMERICA_REPLICA_2, String.valueOf(PortConstants.NA_REPLICA_2_UDP_PORT),"false"};
    	String[] args2 = {LocationConstants.EUROPE_REPLICA_2, String.valueOf(PortConstants.EU_REPLICA_2_UDP_PORT),"false"};
    	String[] args3 = {LocationConstants.ASIA_REPLICA_2, String.valueOf(PortConstants.AS_REPLICA_2_UDP_PORT),"false"};
    	
    	NAServerReplica2.main(args1);
    	EUServerReplica2.main(args2);
    	ASServerReplica2.main(args3);
    	
    	new Thread(() -> {
    		startUDPServer();
        }).start();
    }
	
	private static void startUDPServer() {
        DatagramSocket socket = null;
		try {
			socket = new DatagramSocket(PortConstants.getUdpPort(LocationConstants.RM2));

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
            	System.out.println(DateFormat.getDateTimeInstance().format(new Date())+FieldConstants.SEPARATOR_ARROW+"INFO : "
							+ "=====RM2 Recieved Request To Send Correct Data TO RM3=====");
            	byte[] replicarequest = (LocationConstants.RM2+FieldConstants.SEPARATOR_ARROW+"Send Recovery Data To RM3").getBytes();
            	socket.send(new DatagramPacket(replicarequest, replicarequest.length, LocationConstants.getInetAddress(LocationConstants.NORTHAMERICA_REPLICA_2), PortConstants.getUdpPort(LocationConstants.NORTHAMERICA_REPLICA_2)));
            	socket.send(new DatagramPacket(replicarequest, replicarequest.length, LocationConstants.getInetAddress(LocationConstants.EUROPE_REPLICA_2), PortConstants.getUdpPort(LocationConstants.EUROPE_REPLICA_2)));
            	socket.send(new DatagramPacket(replicarequest, replicarequest.length, LocationConstants.getInetAddress(LocationConstants.ASIA_REPLICA_2), PortConstants.getUdpPort(LocationConstants.ASIA_REPLICA_2)));
            	System.out.println(DateFormat.getDateTimeInstance().format(new Date())+FieldConstants.SEPARATOR_ARROW+"INFO : "
						+ "=====RM2 Sent Request To Its 3 Servers NA, EU & AS To Send Correct Data TO RM3=====");
            } 
            else if("Recover Your Data".equalsIgnoreCase(requestType)) {
            	byte[] replicarequest = (LocationConstants.RM2+FieldConstants.SEPARATOR_ARROW+"Send Recovery Data To RM3").getBytes();
            	socket.send(new DatagramPacket(replicarequest, replicarequest.length, LocationConstants.getInetAddress(LocationConstants.RM1), PortConstants.getUdpPort(LocationConstants.RM1)));
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