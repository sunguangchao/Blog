package concurrent.philosopher;

import java.util.concurrent.Semaphore;

class AppContext{
	public static final int NUM_OF_FORKS = 5;//叉子数量（资源）
	public static final int NUM_OF_PHILO = 5;//哲学家数量（线程）
	
	public static Semaphore[] forks;//叉子的信号量
	
	public static Semaphore counter;//哲学家的信号量
	
	static {
		forks = new Semaphore[NUM_OF_FORKS];
		for(int i = 0, len = forks.length; i < len; ++i) {
			forks[i] = new Semaphore(1);//每个叉子的信号量为1
		}
		// 如果有N个哲学家，最多只允许N-1人同时取叉子
		counter = new Semaphore(NUM_OF_PHILO - 1);
	}
	/**
	 * 取得叉子
	 */
	public static void putOnFork(int index, boolean leftFirst)throws InterruptedException {
		if (leftFirst) {
			forks[index].acquire();
			forks[(index+1) % NUM_OF_PHILO].acquire();
		}else {
			forks[(index+1) % NUM_OF_PHILO].acquire();
			forks[index].acquire();
		}
	}
	
	/**
	 * 放回叉子
	 */
	public static void putDownFork(int index, boolean leftFirst) throws InterruptedException{
		if (leftFirst) {
			forks[index].release();
			forks[(index + 1) % NUM_OF_PHILO].release();
		}else {
			forks[(index + 1) % NUM_OF_PHILO].release();
			forks[index].release();
			
		}
	}
}


class Philosopher implements Runnable{
	private int index;
	private String name;
	public Philosopher(int index, String name) {
		this.index = index;
		this.name = name;
	}
	@Override
	public void run() {
		// TODO Auto-generated method stub
		while(true) {
			try {
				AppContext.counter.acquire();
				boolean leftFirst = index % 2 == 0;
				AppContext.putOnFork(index , leftFirst);
				System.out.println(name + "正在吃意大利面（通心粉）...");
				AppContext.putDownFork(index, leftFirst);
				AppContext.counter.release();
			} catch (InterruptedException e) {
				// TODO: handle exception
				e.printStackTrace();
			}
		}
		
	}
	
	
}

public class PhilosopherTest {
	public static void main(String[] args) {
		String[] names = {"骆昊", "王大锤", "张三丰", "杨过", "李莫愁"};
		for(int i=0, len = names.length; i < len; ++i) {
			new Thread(new Philosopher(i, names[i])).start();
		}
	}

}
