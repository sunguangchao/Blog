package TCPIP;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketAddress;

public class TCPEchoServer {
	private static final int BUFSIZE = 32;
	
	public static void main(String[] args) throws IOException{
		if (args.length != 1) {
			throw new IllegalArgumentException("Parameter(s):<Port>");
		}
		int servPort = Integer.parseInt(args[0]);
		// create a server socket to accept client connection requests
		ServerSocket serverSocket = new ServerSocket(servPort);
		int recvMsgSize;
		byte[] receiveBuf = new byte[BUFSIZE];
		while(true) {
			Socket clntSock = serverSocket.accept();
			SocketAddress clientAddress = clntSock.getRemoteSocketAddress();
			System.out.println(clientAddress.toString());
			InputStream is = clntSock.getInputStream();
			OutputStream os = clntSock.getOutputStream();
			while((recvMsgSize = is.read(receiveBuf)) != -1) {
				os.write(receiveBuf, 0, recvMsgSize);
			}
			clntSock.close();	
		}
	}
}
