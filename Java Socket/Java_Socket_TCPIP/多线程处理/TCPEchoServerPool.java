package tcpip;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Created by 11981 on 2017/12/6.
 */
public class TCPEchoServerPool {
    public static void main(String[] args) throws IOException{
        if (args.length != 2){
            throw new IllegalArgumentException("Parameters:<Port><Threads>");
        }

        int echoServPort = Integer.parseInt(args[0]);
        int threadPoolSize = Integer.parseInt(args[1]);
        final ServerSocket servSock = new ServerSocket(echoServPort);
        final Logger logger = Logger.getLogger("practical");
        for (int i=0; i < threadPoolSize; i++){
            Thread thread = new Thread(new Runnable() {
                @Override
                public void run() {
                    while (true){
                        try {
                            Socket clnSock = servSock.accept();
                            EchoProtocal.handleEchoClient(clnSock, logger);
                        }catch (IOException ex){
                            logger.log(Level.WARNING, "Client accpet failed", ex);
                        }
                    }
                }
            });
            thread.start();
            logger.info("Create and started Thread = " + thread.getName());
        }
    }
}
