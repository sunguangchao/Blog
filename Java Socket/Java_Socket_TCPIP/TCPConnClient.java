package TCPIP;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.Socket;
import java.net.UnknownHostException;

public class TCPConnClient {
	
	public void connect(String server, int port) throws UnknownHostException, IOException {
		if (server.equals("") || port == 0) {
			throw new IllegalArgumentException("Argument is illegal");
		}
		byte[] data = getData().getBytes();
		Socket socket = new Socket(server, port);
		System.out.println("socket builder");
		
		
		InputStream is = socket.getInputStream();
		OutputStream os = socket.getOutputStream();
//		OutputStreamWriter osw = new OutputStreamWriter(os);
//		BufferedWriter bw = new BufferedWriter(osw);
		
		os.write(data);
		os.flush();

		socket.close();
		
		
	}
	
	public String getData() {
		String text = "<?xml version=\"1.0\" encoding=\"UTF-8\" ?>" +
    			"<ROOT><resultCode>0</resultCode>" +
    			"<totalCount>2</totalCount>" +
    			"<resultsmap>" +
    			"<backlist>" +
    				"<userId>0</userId>" +
    				"<loginName>张三0</loginName>" +
    				"<email>qsina0@sina.com</email>" +
    				"<userType>1</userType>" +
    				"<status>1</status>" +
    				"<roleType>1</roleType>" +
    				"<registerIP>192.168.1.0</registerIP>" +
    				"<registerTime>2012-06-07 15:35:33</registerTime>" +
    				"</backlist>" +
//    			"<backlist>" +
//    				"<userId>1</userId>" +
//    				"<loginName>张三1</loginName>" +
//    				"<email>qsina1@sina.com</email>" +
//    				"<userType>1</userType>" +
//    				"<status>1</status>" +
//    				"<roleType>1</roleType>" +
//    				"<registerIP>192.168.1.1</registerIP>" +
//    				"<registerTime>2012-06-07 15:35:33</registerTime>" +
//    				"</backlist>" +
    			"</resultsmap>" +
    			"</ROOT>";
		
		return text;
	}
	public static void main(String[] args) {
		TCPConnClient client = new TCPConnClient();
		String server = "localhost";
		int port = 8888;
		try {
			client.connect(server, port);
		} catch (UnknownHostException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
