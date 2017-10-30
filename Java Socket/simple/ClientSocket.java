package Socket;

import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.net.Socket;

/**
 * Created by 11981 on 2017/10/30.
 */
public class ClientSocket {
    public static void main(String[] args) {
        String host = "127.0.0.1";
        int port = 8919;
        try {
            Socket client = new Socket(host, port);
            Writer writer = new OutputStreamWriter(client.getOutputStream());
            writer.write("hello server, im client");
            System.out.println("客户端已发送信息");
            writer.flush();
            writer.close();
            client.close();
        }catch (IOException e){
            e.printStackTrace();
        }
    }
}
