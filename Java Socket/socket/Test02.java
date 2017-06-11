package socket;

import java.net.MalformedURLException;
import java.net.URL;

/**
 * Created by 11981 on 2017/6/11.
 * URL常用方法
 */
public class Test02 {
    public static void main(String[] args) {
        try{
            //创建一个URL实例
            URL imooc = new URL("http://www.imooc.com");
            //?后面表示参数，#后面表示锚点
            URL url = new URL(imooc, "index.html?username=tom#test");
            System.out.println("协议：" + url.getProtocol());
            System.out.println("主机：" + url.getHost());
            //如果未指定端口号，则使用默认端口号，此时getPort()方法的返回值是-1
            System.out.println("端口：" + url.getPort());
            System.out.println("文件路径：" + url.getPath());
        }catch (MalformedURLException e){
            e.printStackTrace();
        }
    }
}
