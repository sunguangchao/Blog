package com.gcsun.fixedlLength;

import io.netty.channel.ChannelHandlerAdapter;
import io.netty.channel.ChannelHandlerContext;

/**
 * Created by 11981 on 2018/2/17.
 */
public class EchoServerHandler extends ChannelHandlerAdapter {
    @Override
    public void channelRead(ChannelHandlerContext ctx, Object obj) throws Exception{
        System.out.println("Receive client message : [" + obj + "]");
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext context, Throwable cause) throws Exception{
        cause.printStackTrace();
        //发生异常，关闭链路
        context.close();
    }
}
