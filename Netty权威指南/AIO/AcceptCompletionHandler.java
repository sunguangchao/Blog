package Netty;

import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousSocketChannel;
import java.nio.channels.CompletionHandler;

/**
 * Created by 11981 on 2018/1/13.
 */
public class AcceptCompletionHandler implements
        CompletionHandler<AsynchronousSocketChannel, AsyncTimeServerHandler>{
    public void completed(AsynchronousSocketChannel result,
                          AsyncTimeServerHandler attachment){
        //从attachment获取成员变量AsynchronousSocketChannel
        attachment.asynchronousServerSocketChannel.accept(attachment, this);
        //预分配1M的缓冲区
        ByteBuffer buffer = ByteBuffer.allocate(1024);
        /**
         * read方法的三个参数（按序）：
         * 1.ByteBuffer dst:接收缓冲区，用于从异步Channel中读取数据包
         * 2.A attachment:异步Channel携带的附件，通知回调的时候作为入参使用
         * 3.CompletionHandler<Integer, ? super A>:接收通知回调的业务Handler
         */
        result.read(buffer, buffer, new ReadCompletionHandler(result));
    }

    public void failed(Throwable exc, AsyncTimeServerHandler attachment){
        exc.printStackTrace();
        attachment.latch.countDown();
    }

}
