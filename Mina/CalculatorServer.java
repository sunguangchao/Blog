package Mina;

import org.apache.mina.core.service.IoAcceptor;
import org.apache.mina.filter.codec.ProtocolCodecFilter;
import org.apache.mina.filter.codec.textline.TextLineCodecFactory;
import org.apache.mina.filter.logging.LoggingFilter;
import org.apache.mina.transport.socket.nio.NioSocketAcceptor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.charset.Charset;

/**
 * Created by 11981 on 2017/12/12.
 * 使用Mina實現一个远程的计算器的功能
 */
public class CalculatorServer {
    private static final int PORT = 10010;
    private static final Logger LOGGER = LoggerFactory.getLogger(CalculatorServer.class);

    public static void main(String[] args) throws IOException{
        //首先，创建一个NioSocketAcceptor实例
        IoAcceptor acceptor = new NioSocketAcceptor();
        //获得该I/O服务的过滤器链，并添加两个新的过滤器，一个用来记录日志，另一个用来在
        //字节流和文本之间转换
        acceptor.getFilterChain().addLast("logger", new LoggingFilter());
        acceptor.getFilterChain().addLast("codec",
                new ProtocolCodecFilter(new TextLineCodecFactory(Charset.forName("UTF-8"))));
        acceptor.setHandler(new CalculatorHandler());
        acceptor.bind(new InetSocketAddress(PORT));
        LOGGER.info("计算器服务已启动， 端口是 : "+PORT);
    }
}
