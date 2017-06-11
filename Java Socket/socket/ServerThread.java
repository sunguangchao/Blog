package socket;

import java.io.*;
import java.net.Socket;

/**
 * Created by 11981 on 2017/6/11.
 * 服务器端线程处理类
 */
public class ServerThread extends Thread{
    //和线程相关的socket
    Socket socket = null;
    public ServerThread(Socket socket){
        this.socket = socket;
    }
    //线程执行的操作，响应客户端请求
    public void run(){
        InputStream is = null;
        InputStreamReader isr = null;
        BufferedReader br = null;
        OutputStream os = null;
        PrintWriter pw = null;
        try{
            is = socket.getInputStream();//字节输入流
            isr = new InputStreamReader(is);//将字节流转化为字符流
            br = new BufferedReader(isr);//为输入流添加缓冲
            String info = null;
            while ((info=br.readLine())!=null){
                System.out.println("我是服务器，客户端说："+info);
            }
            socket.shutdownInput();//关闭输入流

            os = socket.getOutputStream();
            pw = new PrintWriter(os);//包装为打印流
            pw.write("欢迎您");
            pw.flush();//将缓存输出
        }catch (IOException e){
            e.printStackTrace();
        }finally {
            try{
                pw.close();
                os.close();
                br.close();
                isr.close();
                is.close();
                socket.close();
            }catch (IOException e){
                e.printStackTrace();
            }
        }
    }
}
