package server;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;
import java.text.DateFormat;
import java.util.Date;

import server.replicas.ASServerReplica3;
import server.replicas.EUServerReplica3;
import server.replicas.NAServerReplica3;
import util.FieldConstants;
import util.LocationConstants;
import util.PortConstants;

/**
 *Replica 3 Manager
 * @author <a href="mailto:rajmistry2298@gmail.com">Raj Mistry</a>
 */
public class RM3 {
	public static void main(String[] args) {
    	
		String[] args1 = {LocationConstants.NORTHAMERICA_REPLICA_3, String.valueOf(PortConstants.NA_REPLICA_3_UDP_PORT),"false"};
		String[] args2 = {LocationConstants.EUROPE_REPLICA_3, String.valueOf(PortConstants.EU_REPLICA_3_UDP_PORT),"false"};   	
    	String[] args3 = {LocationConstants.ASIA_REPLICA_3, String.valueOf(PortConstants.AS_REPLICA_3_UDP_PORT),"false"};
    	  	
    	NAServerReplica3.main(args1);
    	EUServerReplica3.main(args2);
    	ASServerReplica3.main(args3);
    	
    	new Thread(() -> {
    		startUDPServer();
        }).start();
    }
	
	private static void startUDPServer() {
        DatagramSocket socket = null;
		try {
			socket = new DatagramSocket(PortConstants.getUdpPort(LocationConstants.RM3));

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
        final String serverName = dpData[0].trim();
        final String requestType = dpData[1].trim();
        
        try {
            socket = new DatagramSocket();
            
             if("Recover Your Data".equalsIgnoreCase(requestType)) {
            	System.out.println(DateFormat.getDateTimeInstance().format(new Date())+FieldConstants.SEPARATOR_ARROW+"INFO : "
     								+ "=====RM3 Recieved Request Regarding solving BYZANTINE Error : " +requestType+"=====");
            	byte[] replicarequest = (LocationConstants.RM3+FieldConstants.SEPARATOR_ARROW+"Send Recovery Data To RM3").getBytes();
            	socket.send(new DatagramPacket(replicarequest, replicarequest.length, LocationConstants.getInetAddress(LocationConstants.RM2), PortConstants.getUdpPort(LocationConstants.RM2)));
            	System.out.println(DateFormat.getDateTimeInstance().format(new Date())+FieldConstants.SEPARATOR_ARROW+"INFO : "
							+ "=====RM3 Sent Request To RM2 For Getting Correct Data To Resolve BYZANTINE Error.======");
            } else if("Sent Recovery Data".equalsIgnoreCase(requestType) && serverName.contains("NA")) {
            	String data = dpData[2].trim();
            	byte[] replicarequest = (LocationConstants.RM3+FieldConstants.SEPARATOR_ARROW+"Change Recovered Data"+FieldConstants.SEPARATOR_ARROW+data).getBytes();
            	socket.send(new DatagramPacket(replicarequest, replicarequest.length, LocationConstants.getInetAddress(LocationConstants.NORTHAMERICA_REPLICA_3), PortConstants.getUdpPort(LocationConstants.NORTHAMERICA_REPLICA_3)));
            	System.out.println(DateFormat.getDateTimeInstance().format(new Date())+FieldConstants.SEPARATOR_ARROW+"INFO : "
						+ "======RM3 Recieved Correct Data From "+serverName+" & Sent It To NA_Replica_3=====");
            } else if("Sent Recovery Data".equalsIgnoreCase(requestType) && serverName.contains("EU")) {
            	String data = dpData[2].trim();
            	byte[] replicarequest = (LocationConstants.RM3+FieldConstants.SEPARATOR_ARROW+"Change Recovered Data"+FieldConstants.SEPARATOR_ARROW+data).getBytes();
            	socket.send(new DatagramPacket(replicarequest, replicarequest.length, LocationConstants.getInetAddress(LocationConstants.EUROPE_REPLICA_3), PortConstants.getUdpPort(LocationConstants.EUROPE_REPLICA_3)));
            	System.out.println(DateFormat.getDateTimeInstance().format(new Date())+FieldConstants.SEPARATOR_ARROW+"INFO : "
						+ "=====RM3 Recieved Correct Data From "+serverName+" & Sent It To EU_Replica_3======");
            } else if("Sent Recovery Data".equalsIgnoreCase(requestType) && serverName.contains("AS")) {
            	String data = dpData[2].trim();
            	byte[] replicarequest = (LocationConstants.RM3+FieldConstants.SEPARATOR_ARROW+"Change Recovered Data"+FieldConstants.SEPARATOR_ARROW+data).getBytes();
            	socket.send(new DatagramPacket(replicarequest, replicarequest.length, LocationConstants.getInetAddress(LocationConstants.ASIA_REPLICA_3), PortConstants.getUdpPort(LocationConstants.ASIA_REPLICA_3)));
            	System.out.println(DateFormat.getDateTimeInstance().format(new Date())+FieldConstants.SEPARATOR_ARROW+"INFO : "
						+ "=====RM3 Recieved Correct Data From "+serverName+" & Sent It To AS_Replica_3=====");
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