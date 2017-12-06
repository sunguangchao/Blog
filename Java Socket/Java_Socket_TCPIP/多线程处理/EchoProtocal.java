package tcpip;


import jdk.internal.util.xml.impl.Input;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Created by 11981 on 2017/12/5.
 */
public class EchoProtocal implements Runnable {
    private static final int BUFSIZE = 32;

    private Socket clntSocket;
    private Logger logger;
    public EchoProtocal(Socket clntSocket, Logger logger){
        this.clntSocket = clntSocket;
        this.logger = logger;
    }

    public static void handleEchoClient(Socket clntSocket, Logger logger){
        try {
            InputStream in = clntSocket.getInputStream();
            OutputStream out = clntSocket.getOutputStream();
            int recvMsgSize;//size of received message
            int totalBytesEchoed = 0;//Bytes received from client
            byte[] echoBuffer = new byte[BUFSIZE];//receive buffer
            //循环直到连接关闭，每次循环中在接收数据后就立即写回
            while ((recvMsgSize = in.read(echoBuffer)) != -1){
                out.write(echoBuffer, 0, recvMsgSize);
                totalBytesEchoed += recvMsgSize;
            }

            logger.info("Client " + clntSocket.getRemoteSocketAddress() + ", echoed "
                    + totalBytesEchoed + " bytes.");
        }catch (IOException ex){
            logger.log(Level.WARNING, "Exception in echo protocol", ex);

        }finally {
            try {
                clntSocket.close();
            }catch (Exception e){

            }
        }

    }

    public void run(){
        handleEchoClient(clntSocket, logger);
    }
}
