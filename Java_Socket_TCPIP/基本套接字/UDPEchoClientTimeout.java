package TCPIP;

import java.io.IOException;
import java.io.InterruptedIOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

public class UDPEchoClientTimeout {
	private static final int TIMEOUT = 8000;
	private static final int MAXTRIES = 5;
	public static void main(String[] args) throws IOException{
		if (args.length < 2 || args.length > 3) {
			System.out.println(args[1].toString());
			throw new IllegalArgumentException("Parameters: <Server> <Word> [<Port>]");
		}
		//Server address
		InetAddress serverAddress = InetAddress.getByName(args[0]);
		//得到要发送的信息
		//convert the argument string to bytes using the default encoding
		byte[] bytesToSend = args[1].getBytes();
		int serverPort = (args.length == 3) ? Integer.parseInt(args[2]) : 7;
		
		DatagramSocket socket = new DatagramSocket();
		socket.setSoTimeout(TIMEOUT);
		//创建一个要发送的数据报文，这里指定了数据，数据长度，目的地址，目的端口
		DatagramPacket sendPacket = new DatagramPacket(bytesToSend, bytesToSend.length, 
				serverAddress, serverPort);
		//创建一个要接收的数据报文，定义一个用来存放报文数据的字节数组
		DatagramPacket receivePacket = new DatagramPacket(
				new byte[bytesToSend.length],  bytesToSend.length);
		//尝试计数器
		int tries = 0;
		boolean receiveResponse = false;
		//因为数据报文可能丢失，所以需准备好重传数据，这里我们尝试5次
		do {
			//将数据报文传输到指定的地址和端口号
			socket.send(sendPacket);
			try {
				//阻塞等待，直到接收到数据或者等待超时
				socket.receive(receivePacket);
				if (!receivePacket.getAddress().equals(serverAddress)) {
					throw new IOException("Received packet from an unknown source");
				}
				receiveResponse = true;
			}catch (InterruptedIOException e) {
				//如果超时，尝试计数器加1
				tries += 1;
				System.out.println("Time out, " + (MAXTRIES - tries + " more tries..."));
				// TODO: handle exception
			}
		}while((!receiveResponse) && (tries<MAXTRIES));
		
		if (receiveResponse) {
			System.out.println("Received: " + new String(receivePacket.getData()));
		}else {
			System.out.println("No response -- giving up.");
		}
		socket.close();
	}
}
