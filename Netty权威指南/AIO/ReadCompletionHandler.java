package Netty;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousSocketChannel;
import java.nio.channels.CompletionHandler;
import java.util.Date;

/**
 * Created by 11981 on 2018/1/13.
 */
public class ReadCompletionHandler implements CompletionHandler<Integer, ByteBuffer> {
    private AsynchronousSocketChannel channel;

    public ReadCompletionHandler(AsynchronousSocketChannel channel){
        if (this.channel == null){
            this.channel = channel;
        }
    }

    public void completed(Integer result, ByteBuffer attachment){
        attachment.flip();//为后续从缓冲区读取数据做准备
        byte[] body = new byte[attachment.remaining()];
        attachment.get(body);
        try {
            String req = new String(body, "UTF-8");
            System.out.println("The time server receive order : " + req);
            //如果是“QUERY TIME ORDER”，则返回系统当前时间
            String currentTime = "QUERY TIME ORDER".equalsIgnoreCase(req) ?
                    new Date(System.currentTimeMillis()).toString() : "BAD ORDER";
            doWrite(currentTime);
        }catch (UnsupportedEncodingException e){
            e.printStackTrace();
        }
    }

    //将消息发回给客户端
    private void doWrite(String currentTime){
        if (currentTime != null && currentTime.trim().length() > 0){
            byte[] bytes = (currentTime).getBytes();
            ByteBuffer writeBuffer = ByteBuffer.allocate(bytes.length);
            writeBuffer.put(bytes);
            writeBuffer.flip();
            channel.write(writeBuffer, writeBuffer, new CompletionHandler<Integer, ByteBuffer>() {
                @Override
                public void completed(Integer result, ByteBuffer buffer) {
                    if (buffer.hasRemaining()){
                        channel.write(buffer, buffer, this);
                    }
                }

                @Override
                public void failed(Throwable exc, ByteBuffer attachment) {

                    try {
                        channel.close();
                    }catch (IOException e){

                    }
                }
            });
        }
    }

    public void failed(Throwable exc, ByteBuffer attachment){
        try {
            this.channel.close();
        }catch (IOException e){
            e.printStackTrace();
        }
    }
}
