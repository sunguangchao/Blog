package tcpip;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.logging.Logger;

/**
 * Created by 11981 on 2017/12/6.
 */
public class TCPEchoServerExecutor {
    public static void main(String[] args) throws IOException{
        if (args.length != 1){
            throw new IllegalArgumentException("Parameter(s):<Port>");
        }
        int echoServerPort = Integer.parseInt(args[0]);
        ServerSocket server = new ServerSocket(echoServerPort);
        Logger logger = Logger.getLogger("practical");
        Executor service = Executors.newCachedThreadPool();
        while (true){
            Socket clnSock = server.accept();
            service.execute(new EchoProtocal(clnSock, logger));
        }
    }
}
