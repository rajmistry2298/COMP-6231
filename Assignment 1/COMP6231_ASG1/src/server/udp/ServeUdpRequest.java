package server.udp;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;

import exceptions.InvalidUdpRequestType;
import server.GameServerImpl;
import util.UdpRequestTypes;


public class ServeUdpRequest extends Thread {
    private DatagramPacket receivedPacket;
    private GameServerImpl gameServer;

    public ServeUdpRequest(final DatagramPacket dp, final GameServerImpl gameServer) throws IOException {
        this.receivedPacket = dp;
        this.gameServer = gameServer;
    }

    
    @Override
    public void run() {
        byte[] responseData;
        DatagramSocket socket = null;
        final String udpRequest = new String(receivedPacket.getData()).trim();
        try {
            socket = new DatagramSocket();
            
            switch (udpRequest) {
                case UdpRequestTypes.PING:
                    responseData = "How can I help you?".getBytes();
                    socket.send(new DatagramPacket(responseData, responseData.length, receivedPacket.getAddress(), receivedPacket.getPort()));
                    break;
                case UdpRequestTypes.SEND_PLAYER_COUNT:
                    responseData = gameServer.getPlayerCount().getBytes();
                    socket.send(new DatagramPacket(responseData, responseData.length, receivedPacket.getAddress(), receivedPacket.getPort()));
                    break;
                default:
                    throw new InvalidUdpRequestType();
            }
            
        } catch (IOException e) {
        	System.out.println(e.getMessage());
        } catch (InvalidUdpRequestType e) {
		System.out.println(e.getMessage(udpRequest.toString()));
        } finally {
        	if (socket != null) {
                socket.close();
            }
        }
    }

}