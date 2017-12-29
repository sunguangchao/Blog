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
	        System.out.println("connect to server … sending echo string");
	        //用来接收数据
	        InputStream is = socket.getInputStream();
	        //用来发送数据
	        OutputStream os = socket.getOutputStream();

	        //发送字符串到服务器
	        os.write(data);
	        int totalBytesRcvd = 0;
	        int bytesRcvd = 0;
	        while (totalBytesRcvd < data.length){
	        	/**
	        	 * read:
	        	 * 1.接收数据的数组
	        	 * 2.接收的第一个字节应该放入数组的位置，字节偏移量
	        	 * 3.放入数组的最大字节数
	        	 * read在没有数据时会等待，直到有数据可读。
	        	 * 返回实际放入数组的字节数
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
