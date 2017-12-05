package TCPIP;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;

public class UDPEchoServer {
	private static final int ECHOMAX = 25;
	public static void main(String[] args) throws IOException{
		if (args.length != 1) {
			throw new IllegalArgumentException("Parameter(s):<Port>");
		}
		int servPort = Integer.parseInt(args[0]);
		DatagramSocket socket = new DatagramSocket(servPort);
		DatagramPacket packet = new DatagramPacket(new byte[ECHOMAX], ECHOMAX);
		while(true) {//Run forever, receiving and eching datagram
			socket.receive(packet);
			System.out.println("Handing client at " + packet.getAddress().getHostAddress() 
					+ " on port " + packet.getPort());
			socket.send(packet);
			packet.setLength(ECHOMAX);//重置缓存区大小
		}	
	}
}
