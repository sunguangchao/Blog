package proxy;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

/**
 * Created by 11981 on 2017/12/28.
 */

class Meal{
    private int orderNum;
    public Meal(int orderNum){
        this.orderNum = orderNum;
    }

    public String toString() {
        return "Meal : " + orderNum;
    }
}

class WaitPerson implements Runnable{
    private Restaurant restaurant;

    public WaitPerson(Restaurant r){
        this.restaurant = r;
    }

    @Override
    public void run() {
        try {
            while (!Thread.interrupted()){
                synchronized (this){
                    while (restaurant.meal == null){
                        wait();//for the chef to produce a meal
                    }
                }
                System.out.println("WaitPerson got" + restaurant.meal);
                synchronized (restaurant.chef){
                    restaurant.meal = null;
                    restaurant.chef.notifyAll();
                }
            }
            TimeUnit.MICROSECONDS.sleep(100);
        }catch (InterruptedException e){
            System.out.println("WaitPerson interrupted");
        }
    }
}
class Chef implements Runnable{
    private Restaurant restaurant;
    private int count = 0;
    public Chef(Restaurant restaurant){
        this.restaurant = restaurant;
    }
    @Override
    public void run() {
        try {
            while (!Thread.interrupted()){
                synchronized (this){
                    while (restaurant.meal != null){
                        wait();//wait meal to be taken
                    }
                    if (++count == 10){
                        System.out.println("Out of food, closing");
                        restaurant.exec.shutdown();
                    }
                    System.out.println("Order up");
                    synchronized (restaurant.waitPerson){
                        restaurant.meal = new Meal(count);
                        restaurant.waitPerson.notifyAll();

                    }
                }
            }
        }catch (InterruptedException e){
            e.printStackTrace();
        }

    }
}
public class Restaurant {
    Meal meal;
    ExecutorService exec = Executors.newCachedThreadPool();
    WaitPerson waitPerson = new WaitPerson(this);
    Chef chef = new Chef(this);

    public Restaurant(){
        exec.execute(chef);
        exec.execute(waitPerson);
    }

    public static void main(String[] args){
        new Restaurant();
    }

}
