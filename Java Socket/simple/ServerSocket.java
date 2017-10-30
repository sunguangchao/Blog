package Socket;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.Socket;

/**
 * Created by 11981 on 2017/10/30.
 */
public class ServerSocket {
    public static void main(String[] args) {
        int port = 8919;
        try{

            java.net.ServerSocket server = new java.net.ServerSocket(port);
            /**
             * 调用accept方法监听并获取客户端的请求socket
             * accept方法是一个阻塞方法，在服务器端与客户端之间建立联系之前会一直等待阻塞。
             */
            Socket socket = server.accept();
            Reader reader = new InputStreamReader(socket.getInputStream());
            char chars[] = new char[1024];
            int len;
            StringBuilder sb = new StringBuilder();
            while ((len=reader.read(chars)) != -1){
                sb.append(new String(chars, 0, len));
            }
            System.out.println("Receive from client message=: " + sb);
            reader.close();
            socket.close();
            server.close();
        }catch (IOException e){
            e.printStackTrace();
        }
    }
}
