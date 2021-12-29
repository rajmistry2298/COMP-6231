package server.udp;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;

import server.GameServerImpl;
import util.PortConstants;


public class UdpServer extends Thread {
    private GameServerImpl gameServer;
    private int udpPort;
    private DatagramSocket socket = null;

    public UdpServer(final GameServerImpl gameServer) throws IOException {
        this.gameServer = gameServer;
        this.udpPort = PortConstants.getUdpPort(gameServer.serverLocation);
        this.socket = new DatagramSocket(udpPort);
    }

    
    @Override
    public void run() {
        Thread.currentThread().setName("UDP Server: " + gameServer.serverLocation.toString());
        try {
            while (true) {
                try {
                    final byte[] data = new byte[100];
                    final DatagramPacket dp = new DatagramPacket(data, data.length);
                    socket.receive(dp);
                    //Start a new thread on each request
                    new ServeUdpRequest(dp, gameServer).start();
                } catch (IOException e) {
                	System.out.println(e.getMessage());
                }
            }
        } finally {
        	if (socket != null) {
                socket.close();
            }
        }
    }

}