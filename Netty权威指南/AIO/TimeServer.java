package Netty;

/**
 * Created by 11981 on 2018/1/14.
 */
public class TimeServer {
    public static void main(String[] args) {
        int port = 9000;
        if (args != null && args.length > 0){
            try {
                port = Integer.valueOf(args[0]);
            }catch (NumberFormatException e){

            }

        }
        Runnable r = new AsyncTimeServerHandler(port);
        Thread t = new Thread(r);
        t.start();
    }
}
