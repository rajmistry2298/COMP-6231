package server.replicas;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import server.GameServerImpl;
import server.UDP.UdpRequestProcessor;
import service.ActivityLoggerService;
import util.FileConstants;
import util.LocationConstants;

public class NAServerReplica2 {

	public static void main(String[] args) {

		try {
			final List<String> replicaList = new ArrayList<>(Arrays.asList(LocationConstants.NORTHAMERICA_REPLICA_1,
														   				   LocationConstants.NORTHAMERICA_REPLICA_2, 
														   				   LocationConstants.NORTHAMERICA_REPLICA_3));
			
			final ActivityLoggerService activityLogger = new ActivityLoggerService(FileConstants.SERVER_LOG_FILE_PATH + LocationConstants.NORTHAMERICA + "/" + args[0].trim()+FileConstants.FILE_TYPE);
			final GameServerImpl gameServerImpl = new GameServerImpl(args[0].trim(), Integer.parseInt(args[1].trim()), Boolean.valueOf(args[2].trim()));
			replicaList.remove(gameServerImpl.getServerName());

			new Thread(() -> {
				startUdpServer(activityLogger, gameServerImpl, replicaList);
			}).start();
			
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private static void startUdpServer(final ActivityLoggerService activityLogger, final GameServerImpl server, final List<String> replicaList) {
		DatagramSocket socket = null;
		try {
			socket = new DatagramSocket(server.getPortNo());
			activityLogger.log("INFO",String.format("%s - UDP server has been started and running.", server.getServerName()));
			while (true) {
				try {
					final byte[] data = new byte[1000];
					final DatagramPacket packet = new DatagramPacket(data, data.length);
					socket.receive(packet);

					new UdpRequestProcessor(activityLogger, server, packet, replicaList).start();
					
				} catch (IOException e) {
					activityLogger.log("ERROR", e.getMessage());
				}
			}
		} catch (SocketException e1) {
			activityLogger.log("ERROR", e1.getMessage());
		} finally {
			if (socket != null) {
				socket.close();
			}
		}
	}

}