package TCPIP;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.net.SocketException;

public class TCPEchoClient {
	 public static void main(String[] args) throws IOException{
	        if (args.length < 2 || args.length > 3){
	            throw new IllegalArgumentException("Parameters: <Server> <Word> [<Port>]");
	        }
	        String server = args[0];
	        byte[] data = args[1].getBytes();
	        int servPort = (args.length == 3) ? Integer.parseInt(args[2]) : 7;
	        Socket socket = new Socket(server, servPort);
	        System.out.println("connect to server �� sending echo string");
	        //������������
	        InputStream is = socket.getInputStream();
	        //������������
	        OutputStream os = socket.getOutputStream();

	        //�����ַ�����������
	        os.write(data);
	        int totalBytesRcvd = 0;
	        int bytesRcvd = 0;
	        while (totalBytesRcvd < data.length){
	        	/**
	        	 * read:
	        	 * 1.�������ݵ�����
	        	 * 2.���յĵ�һ���ֽ�Ӧ�÷��������λ�ã��ֽ�ƫ����
	        	 * 3.�������������ֽ���
	        	 * read��û������ʱ��ȴ���ֱ�������ݿɶ���
	        	 * ����ʵ�ʷ���������ֽ���
	        	 */
	            if ((bytesRcvd = is.read(data, totalBytesRcvd, data.length-totalBytesRcvd)) == -1){
	                throw new SocketException("Connection.closed prematurely");
	            }
	            totalBytesRcvd += bytesRcvd;
	        }

	        System.out.println("Received: " + new String(data));
	        socket.close();
	    }
}
