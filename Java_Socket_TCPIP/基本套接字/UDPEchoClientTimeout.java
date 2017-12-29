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
		//�õ�Ҫ���͵���Ϣ
		//convert the argument string to bytes using the default encoding
		byte[] bytesToSend = args[1].getBytes();
		int serverPort = (args.length == 3) ? Integer.parseInt(args[2]) : 7;
		
		DatagramSocket socket = new DatagramSocket();
		socket.setSoTimeout(TIMEOUT);
		//����һ��Ҫ���͵����ݱ��ģ�����ָ�������ݣ����ݳ��ȣ�Ŀ�ĵ�ַ��Ŀ�Ķ˿�
		DatagramPacket sendPacket = new DatagramPacket(bytesToSend, bytesToSend.length, 
				serverAddress, serverPort);
		//����һ��Ҫ���յ����ݱ��ģ�����һ��������ű������ݵ��ֽ�����
		DatagramPacket receivePacket = new DatagramPacket(
				new byte[bytesToSend.length],  bytesToSend.length);
		//���Լ�����
		int tries = 0;
		boolean receiveResponse = false;
		//��Ϊ���ݱ��Ŀ��ܶ�ʧ��������׼�����ش����ݣ��������ǳ���5��
		do {
			//�����ݱ��Ĵ��䵽ָ���ĵ�ַ�Ͷ˿ں�
			socket.send(sendPacket);
			try {
				//�����ȴ���ֱ�����յ����ݻ��ߵȴ���ʱ
				socket.receive(receivePacket);
				if (!receivePacket.getAddress().equals(serverAddress)) {
					throw new IOException("Received packet from an unknown source");
				}
				receiveResponse = true;
			}catch (InterruptedIOException e) {
				//�����ʱ�����Լ�������1
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
