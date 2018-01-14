package Netty;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.channels.AsynchronousServerSocketChannel;
import java.util.concurrent.CountDownLatch;

/**
 * Created by 11981 on 2018/1/13.
 */
public class AsyncTimeServerHandler implements Runnable {
    private int port;

    //在一组正在执行的操作完成之前，允许当前的线程一致阻塞。
    CountDownLatch latch;
    AsynchronousServerSocketChannel asynchronousServerSocketChannel;
    public AsyncTimeServerHandler(int port){
        this.port = port;
        try {
            //创建一个异步的服务端通道
            asynchronousServerSocketChannel = AsynchronousServerSocketChannel.open();
            asynchronousServerSocketChannel.bind(new InetSocketAddress(port));
            System.out.println("The time server is start in port : " + port);
        }catch (IOException e){
            e.printStackTrace();
        }
    }

    @Override
    public void run(){
        latch = new CountDownLatch(1);
        doAccept();
        try{
            latch.await();
        }catch (InterruptedException e){
            e.printStackTrace();
        }
    }

    //接收客户端的连接
    public void doAccept(){
        asynchronousServerSocketChannel.accept(this, new AcceptCompletionHandler());
    }
}
