package account;

import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Created by 11981 on 2018/4/22.
 */
public class Account {
    private Lock accountLock = new ReentrantLock();
    private double balance;

    /**
     * 存入金额
     * @param money
     */
//    public synchronized void deposit(double money){
//        double newBalance = balance + money;
//        try {
//            Thread.sleep(10);
//        }catch (InterruptedException e){
//            e.printStackTrace();
//        }
//        balance = newBalance;
//    }
    public void deposit(double money){
        accountLock.lock();
        try {
            double newBalance = balance + money;
            try {
                Thread.sleep(10);
            }catch (InterruptedException e){
                e.printStackTrace();
            }
            balance = newBalance;
        }finally {
            accountLock.unlock();
        }

    }

    public double getBalance(){
        return balance;
    }
}
