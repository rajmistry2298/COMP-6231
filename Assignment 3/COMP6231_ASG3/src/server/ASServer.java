package server;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.xml.ws.Endpoint;

import util.LocationConstants;
import util.PortConstants;

/**
 * AS Server Class
 * Create separate server instance for AS location
 * Register those to the registry service to expose interface APIs
 * @author <a href="mailto:rajmistry2298@gmail.com">Raj Mistry</a>
 */
public class ASServer{

	public static void main(String[] args) {
		try {
			writeLog("AS Server Started!!!");
			final GameServerImpl gameServerImpl = new GameServerImpl(LocationConstants.ASIA);
            
            /*Binding it to service registry --> WSDL file is created*/
        	Endpoint endpoint = Endpoint.publish(LocationConstants.ASIA_ADDRESS, gameServerImpl);
        	
        	System.out.println("Asia Service Is Published : " + endpoint.isPublished());
        	writeLog("Asia Service Is Published : " + endpoint.isPublished());
			            
            /*Start UDP server for AS*/
            new Thread(() -> {
            	startUDPServer(gameServerImpl);
            }).start();
       
            System.out.println("#================= Asia Server is started =================#");
		} catch(Exception e) {
			System.out.println("EXCEPTION :: "+ e.getMessage());
		}	
	}
	
	private static void startUDPServer(final GameServerImpl gameServer) {
        DatagramSocket socket = null;
		try {
			socket = new DatagramSocket(PortConstants.getUdpPort(gameServer.getServerName()));
			writeLog("AS UDP Server Started!!!");
			while (true) {
				try {
					final byte[] data = new byte[1000];
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
		}finally {
            if (socket != null) {
            	socket.close();
            }
        }
    }
    
    private static void processUDPRequest(final GameServerImpl gameServer, final DatagramPacket dp) {
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
		if (!Files.exists(Paths.get("src/server/logs/AS_Server_Log.txt"))) {
			PrintWriter writer = new PrintWriter("src/server/logs/AS_Server_Log.txt", "UTF-8");
			writer.println(new SimpleDateFormat("yyyy.MM.dd.HH.mm.ss").format(new Date()) + " : AS Log File Crated!!");
			writer.close();
		}
		logData = new SimpleDateFormat("yyyy.MM.dd.HH.mm.ss").format(new Date()) + " : " + logData + "\n";
		Files.write(Paths.get("src/server/logs/AS_Server_Log.txt"), logData.getBytes(), StandardOpenOption.APPEND);
	}
}