package server.udp;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;


import util.PortConstants;
import util.UdpRequestTypes;

public class UdpRequest extends Thread {
    private String serverLocation;
    private String playerCount;

    public UdpRequest(final String toServerLocation, final String fromServerLocation) throws IOException {
        this.serverLocation = toServerLocation;    
    }

    public String getRemotePlayerCount() {
        return playerCount;
    }

    
    @Override
    public void run() {
    	DatagramSocket socket = null;
        try {
        	final InetAddress address = InetAddress.getByName("localhost");
        	final int port = PortConstants.getUdpPort(serverLocation);
            
            socket = new DatagramSocket();
            byte[] data = UdpRequestTypes.SEND_PLAYER_COUNT.toString().getBytes();
            final DatagramPacket packet = new DatagramPacket(data, data.length, address, port);
            socket.send(packet);
            
            data = new byte[100];
            socket.receive(new DatagramPacket(data, data.length));
            playerCount = serverLocation + ": " + new String(data);
        } catch (UnknownHostException e) {
        	System.out.println(e.getMessage());
        } catch (SocketException e) {
        	System.out.println(e.getMessage());
        } catch (IOException e) {
        	System.out.println(e.getMessage());
        } finally {
            if (socket != null) {
                socket.close();
            }
        }
    }

}