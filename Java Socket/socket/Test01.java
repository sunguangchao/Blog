package socket;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Arrays;

/**
 * Created by 11981 on 2017/6/11.
 */
public class Test01 {
    public static void main(String[] args) throws UnknownHostException{
        //获取本机的InetAddress实例
        InetAddress address = InetAddress.getLocalHost();
        System.out.println("计算名："+address.getHostName());
        System.out.println("IP地址："+address.getHostAddress());
        byte[] bytes = address.getAddress();//获取字节数组形式的IP
        System.out.println("字节数组形式的IP："+ Arrays.toString(bytes));
        System.out.println(address);//直接输出InetAddress对象

        //根据机器名获取InetAddress实例
        InetAddress address1 = InetAddress.getByName("LAPTOP-JOTN4Q82");
        System.out.println("计算名："+address1.getHostName());
        System.out.println("IP地址："+address1.getHostAddress());
    }
}
