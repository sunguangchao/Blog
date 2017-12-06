package tcpip;


import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.logging.Logger;

/**
 * Created by 11981 on 2017/12/6.
 */
public class TCPEchoServerThread {
    public static void main(String[] args) throws IOException{
        if (args.length != 1){
            throw new IllegalArgumentException("Parameter(s):<Port>");
        }
        int echoServerPort = Integer.parseInt(args[0]);
        ServerSocket server = new ServerSocket(echoServerPort);
        Logger logger = Logger.getLogger("practical");
        while (true){
            Socket socket = server.accept();
            Thread thread = new Thread(new EchoProtocal(socket, logger));
            thread.start();
            logger.info("Create and started Thread " + thread.getName());
        }
    }
}
