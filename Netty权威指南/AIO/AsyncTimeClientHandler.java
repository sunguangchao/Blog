package Netty;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousSocketChannel;
import java.nio.channels.CompletionHandler;
import java.util.concurrent.CountDownLatch;

/**
 * Created by 11981 on 2018/1/14.
 * Interface:CompletionHandler
 * A handler for consuming the result of an asynchronous I/O operation.
 */
public class AsyncTimeClientHandler implements
        CompletionHandler<Void, AsyncTimeClientHandler>, Runnable{

    private AsynchronousSocketChannel client;
    private String host;
    private int port;
    private CountDownLatch latch;

    public AsyncTimeClientHandler(String host, int port){
        this.host = host;
        this.port = port;

        try {
            client = AsynchronousSocketChannel.open();
        }catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void run(){
        //创建CountDownLatch进行等待，防止异步操作没有完成线程就退出
        latch = new CountDownLatch(1);
        //发起异步操作
        client.connect(new InetSocketAddress(host, port), this, this);

        try {
            latch.await();
        }catch (InterruptedException e1){
            e1.printStackTrace();
        }

        try {
            client.close();
        }catch (IOException e2){
            e2.printStackTrace();
        }
    }

    //异步连接成功后回调此方法
    @Override
    public void completed(Void result, AsyncTimeClientHandler attachment){
        byte[] req = "QUERY TIME ORDER".getBytes();
        ByteBuffer writeBuffer = ByteBuffer.allocate(req.length);
        writeBuffer.put(req);
        writeBuffer.flip();
        //实现CompletionHandler接口用于写操作完成后的回调
        client.write(writeBuffer, writeBuffer, new CompletionHandler<Integer, ByteBuffer>() {
            @Override
            public void completed(Integer result, ByteBuffer buffer) {
                //如果发送缓冲区仍有尚未发送的字节，将继续异步发送
                if (buffer.hasRemaining()){
                    client.write(buffer, buffer, this);
                }else{//如果发送完成，则进行异步读取操作
                    ByteBuffer readBuffer = ByteBuffer.allocate(1024);
                    //客户端异步读取时间服务器服务端应答消息的处理逻辑
                    client.read(readBuffer, readBuffer, new CompletionHandler<Integer, ByteBuffer>() {
                        //内部匿名类用于，当读取完被JDK回调时，构造应答消息
                        @Override
                        public void completed(Integer result, ByteBuffer buffer) {
                            buffer.flip();
                            byte[] bytes = new byte[buffer.remaining()];
                            buffer.get(bytes);
                            String body;
                            try {
                                body = new String(bytes, "UTF-8");
                                System.out.println("Now is : " + body);
                                latch.countDown();
                            }catch (UnsupportedEncodingException e){
                                e.printStackTrace();
                            }
                        }

                        //读取发生异常时，关闭链路，countDown方法让AsyncTimeClientHandler线程执行完毕，客户端退出执行
                        @Override
                        public void failed(Throwable exc, ByteBuffer attachment) {
                            try {
                                client.close();
                                latch.countDown();
                            }catch (IOException e){
                                //ignore on close;
                            }

                        }
                    });
                }
            }

            @Override
            public void failed(Throwable exc, ByteBuffer attachment) {
                try {
                    client.close();
                    latch.countDown();
                }catch (IOException e){
                    //
                }
            }
        });
    }

    @Override
    public void failed(Throwable exc, AsyncTimeClientHandler attachment) {
        try {
            client.close();
            latch.countDown();
        }catch (IOException e){
            //
        }
    }

}
