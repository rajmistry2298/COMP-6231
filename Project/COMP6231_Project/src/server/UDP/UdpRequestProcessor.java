package server.UDP;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.PortUnreachableException;
import java.net.SocketTimeoutException;
import java.util.ArrayList;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.CountDownLatch;


import server.GameServerImpl;
import service.ActivityLoggerService;
import util.ActionConstants;
import util.FieldConstants;
import util.FileConstants;
import util.LocationConstants;
import util.PortConstants;

public class UdpRequestProcessor extends Thread {
	
    private GameServerImpl server;
    private DatagramPacket packet;
    private ActivityLoggerService activityLogger;
    private List<String> replicaList;
    private static Queue<String> requestQueue = new ConcurrentLinkedQueue<String>();;
    
    public UdpRequestProcessor(final ActivityLoggerService activityLogger, final GameServerImpl server, 
    						   final DatagramPacket packet, final List<String> replicaList) throws IOException {
        this.server = server;
        this.packet = packet;
        this.activityLogger = activityLogger;
        this.replicaList = replicaList;
    }

    @Override
    public void run() {
    	String response = "";
		DatagramSocket socket = null;
		final String request = new String(packet.getData()).trim();
		final String[] packetData = request.split(FieldConstants.SEPARATOR_ARROW.trim());
		final String senderServer = packetData[0].trim();
		final String action = packetData[1].trim();
		final List<String> allserverresponse;
		try {
			socket = new DatagramSocket();
			activityLogger.log("INFO", String.format("UDP %s request has been received from %s Server.", action, senderServer));
			
			if(!server.isLeader() ||  ActionConstants.GET_STATUS.equalsIgnoreCase(action) || 
									  ActionConstants.TRANSFER_ACCOUNT.equalsIgnoreCase(action) || 
									  "Send Recovery Data To RM3".equalsIgnoreCase(action) ||
									  "Change Recovered Data".equalsIgnoreCase(action)) {
				response = performOperation(request, action, socket);
			} else {
				requestQueue.offer(request);
				String Leaderresponse = performOperation(request, action, socket);
				synchronized (requestQueue) {
					String topRequest = requestQueue.peek();
					final StringBuilder tempRequest = new StringBuilder(topRequest);
					final int index = tempRequest.indexOf(FieldConstants.SEPARATOR_ARROW);
					topRequest = tempRequest.replace(0, index, server.getServerName()).toString();
					final String[] packet = topRequest.split(FieldConstants.SEPARATOR_ARROW.trim());
					
					allserverresponse = sendRequestToOtherReplica(topRequest, packet[1].trim());
					allserverresponse.add(Leaderresponse);
				
					if(Leaderresponse.equals(allserverresponse.get(0))) {
						response = Leaderresponse;
					}
					else if(Leaderresponse.equals(allserverresponse.get(1))) {
						response = Leaderresponse;
					}
					else if(allserverresponse.get(0).equals(allserverresponse.get(1))) {
						response = allserverresponse.get(0);
					}
					else {
						for(String res : allserverresponse) {
							if(!res.equals("SOMETHING WENT WRONG ON THIS REPLICA")) {
								response = res;
							}
						}
					}
					
					for(String resp : allserverresponse) {
						if(resp.contains("SOMETHING WENT WRONG ON THIS REPLICA")) {
							FileConstants.BYZANTINE_COUNT++;
							activityLogger.log("ERROR", "===== BYZANTINE ERROR OCCURED AT REPLICA 3 =====");
						}
					}
					if(FileConstants.BYZANTINE_COUNT == 3) {		
						activityLogger.log("INFO", "3 SUCCESSIVE BYZANTINE ERRORS AT Replica 3");
					}
					}
					requestQueue.poll();
				}

			socket.send(new DatagramPacket(response.getBytes(), response.getBytes().length, packet.getAddress(), packet.getPort()));
			activityLogger.log("INFO", String.format("UDP %s response has been sent to %s Server.", action, senderServer));
			
			if(FileConstants.BYZANTINE_COUNT == 3) {
				FileConstants.BYZANTINE_COUNT++;
				final CountDownLatch latch = new CountDownLatch(1);
		                new Thread(() -> {
		                	sendDataRecoveryTORM3(activityLogger);
		                	latch.countDown();
		                }).start();
		        try {
					latch.await();
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		} catch (IOException e) {
			activityLogger.log("ERROR", e.getMessage());
		} finally {
			if (socket != null) {
				socket.close();
			}
		}
    }

    private String performOperation(final String request, final String action, final DatagramSocket socket) {
    	String response = "";
    	switch (action) {
		case ActionConstants.GET_STATUS:
			response = server.getPlayerStatusCount().toString();
			break;
		case ActionConstants.TRANSFER_ACCOUNT:
			final String playerData = request.split(FieldConstants.SEPARATOR_ARROW)[2].trim();
			response = server.transferPlayerAccount(playerData);
			break;
		case ActionConstants.CREATE_PLAY:
			response = createPlayerAccount(request);
			break;
		case ActionConstants.PLAY_SIN:
			response = playerSignIn(request);
			break;
		case ActionConstants.PLAY_SOUT:
			response = playerSignOut(request);
			break;
		case ActionConstants.SUSPEND_PLAY:
			response = suspendAccount(request);
			break;
		case ActionConstants.GETSTATUS:
			response = getPlayerStatus(request);
			break;
		case ActionConstants.TRANSFERACCOUNT:
			response = transferAccount(request);
			break;
		case "Send Recovery Data To RM3":
			response = server.sendDataToReplica3(server.getServerName());
			break;
		case "Change Recovered Data":
			String [] packetData = request.split(FieldConstants.SEPARATOR_ARROW.trim());
			response = server.recoverDataFromOtherReplica(packetData[2].trim());
			break;
		default:
			activityLogger.log("ERROR", "Invalid Action : "+ action);
			break;
		}
    	
    	return response;
    }

	private String createPlayerAccount(final String request) {
		String response = "";
    	final String[] packetData = request.split(FieldConstants.SEPARATOR_ARROW.trim());
    	final String requestId = packetData[2].trim();
    	if(server.isRequestExecuted(requestId)) {
    		response = server.getResponse(requestId);
    	} else {
    		final String[] recordDetails = packetData[3].trim().split(":");
    		response = server.createPlayerAccount(recordDetails[0], recordDetails[1], recordDetails[2], recordDetails[3], recordDetails[4], recordDetails[5]);
    		server.addResponse(requestId, response);
    	}
		return response;
	}

    private String playerSignIn(final String request) {
    	String response = "";
    	final String[] packetData = request.split(FieldConstants.SEPARATOR_ARROW.trim());
    	final String requestId = packetData[2].trim();
    	if(server.isRequestExecuted(requestId)) {
    		response = server.getResponse(requestId);
    	} else {
    		final String username = packetData[3].trim();
			final String password = packetData[4].trim();
    		response = server.playerSignIn(username, password);
    		server.addResponse(requestId, response);
    	}
		return response;
    }
	
	private String playerSignOut(final String request) {
    	String response = "";
    	final String[] packetData = request.split(FieldConstants.SEPARATOR_ARROW.trim());
    	final String requestId = packetData[2].trim();
    	if(server.isRequestExecuted(requestId)) {
    		response = server.getResponse(requestId);
    	} else {
    		final String username = packetData[3].trim();
    		response = server.playerSignOut(username);
    		server.addResponse(requestId, response);
    	}
		return response;
    }
	
	private String suspendAccount(final String request) {
    	String response = "";
    	final String[] packetData = request.split(FieldConstants.SEPARATOR_ARROW.trim());
    	final String requestId = packetData[2].trim();
    	if(server.isRequestExecuted(requestId)) {
    		response = server.getResponse(requestId);
    	} else {
    		final String usernametosuspend = packetData[3].trim();
    		response = server.suspendAccount(usernametosuspend);
    		server.addResponse(requestId, response);
    	}
		return response;
    }

    private String getPlayerStatus(final String request) {
    	String response = "";
    	final String[] packetData = request.split(FieldConstants.SEPARATOR_ARROW.trim());
    	final String requestId = packetData[2].trim();
    	if(server.isRequestExecuted(requestId)) {
    		response = server.getResponse(requestId);
    	} else {
    		response =  server.getPlayerStatus();
    		server.addResponse(requestId, response);
    	}
		return response;
    }

    private String transferAccount(final String request) {
    	String response = "";
    	final String[] packetData = request.split(FieldConstants.SEPARATOR_ARROW.trim());
    	final String requestId = packetData[2].trim();
    	if(server.isRequestExecuted(requestId)) {
    		response = server.getResponse(requestId);
    	} else {
    		final String username = packetData[3].trim();
			final String password = packetData[4].trim();
			final String newIpAddress = packetData[6].trim();
    		response =  server.transferAccount(username, password, newIpAddress);
    		server.addResponse(requestId, response);
    	}
		return response;
    }
    
    private List<String> sendRequestToOtherReplica(final String request, final String action) {
    	final CountDownLatch latch = new CountDownLatch(replicaList.size());
    	final List<String> otherserverresponse = new ArrayList<>();
        for (final String replica : replicaList) {
            if (!replica.equalsIgnoreCase(server.getServerName())) {
                new Thread(() -> {
                	final String response = sendUdpRequest(request, replica, action);
                	if(response != null)
            			otherserverresponse.add(response);
                	latch.countDown();
                }).start();
            }
        }
        try {
			latch.await();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
        return otherserverresponse;
    }
    
    private String sendUdpRequest(final String request, final String toServer, final String action) {
    	DatagramSocket socket = null;
    	String response=null;
    	try {
			final InetAddress inetAddress = LocationConstants.getInetAddress(toServer);
			final int udpPort = PortConstants.getUdpPort(toServer);
			activityLogger.log("INFO", String.format("UDP %s request has been sent to %s Server.", action, toServer));
			
			socket = new DatagramSocket();
			final DatagramPacket packet = new DatagramPacket(request.getBytes(), request.getBytes().length, inetAddress, udpPort);
			socket.send(packet);
			
			try {
				//5 second timeout
				socket.setSoTimeout(5000);
				byte[] data = new byte[1000];
				socket.receive(new DatagramPacket(data, data.length));
				response = new String(data);
				activityLogger.log("INFO", String.format("UDP %s response has been received from %s Server.", action, toServer));
			} catch (SocketTimeoutException | PortUnreachableException e) {
				activityLogger.log("ERROR", String.format("No response has been sent from %s Server.", toServer));
			} 
		} catch (Exception e) {
			activityLogger.log("ERROR", e.getMessage());
		} finally {
			if(socket != null) {
				socket.close();
			}
		}
    	return response.trim();
    }
    
    private void sendDataRecoveryTORM3(ActivityLoggerService activityLogger2) {
    	DatagramSocket socket = null;
    	try {
			final InetAddress inetAddress = LocationConstants.getInetAddress(LocationConstants.RM3);
			final int udpPort = PortConstants.getUdpPort(LocationConstants.RM3);
			socket = new DatagramSocket();
			final String request = this.server.getServerName()+FieldConstants.SEPARATOR_ARROW+"Recover Your Data";
			final DatagramPacket packet = new DatagramPacket(request.getBytes(), request.getBytes().length, inetAddress, udpPort);
			socket.send(packet);
			activityLogger2.log("INFO", "====== Message Sent To Replica Manager 3 To Inform About Byzantine Error ======");
			 
		} catch (Exception e) {
			activityLogger.log("ERROR", e.getMessage());
		} finally {
			if(socket != null) {
				socket.close();
			}
		}
    }
}