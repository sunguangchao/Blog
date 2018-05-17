package concurrent.philosopher;

import java.util.concurrent.Semaphore;

class AppContext{
	public static final int NUM_OF_FORKS = 5;//������������Դ��
	public static final int NUM_OF_PHILO = 5;//��ѧ���������̣߳�
	
	public static Semaphore[] forks;//���ӵ��ź���
	
	public static Semaphore counter;//��ѧ�ҵ��ź���
	
	static {
		forks = new Semaphore[NUM_OF_FORKS];
		for(int i = 0, len = forks.length; i < len; ++i) {
			forks[i] = new Semaphore(1);//ÿ�����ӵ��ź���Ϊ1
		}
		// �����N����ѧ�ң����ֻ����N-1��ͬʱȡ����
		counter = new Semaphore(NUM_OF_PHILO - 1);
	}
	/**
	 * ȡ�ò���
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
	 * �Żز���
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
				System.out.println(name + "���ڳ�������棨ͨ�ķۣ�...");
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
		String[] names = {"���", "����", "������", "���", "��Ī��"};
		for(int i=0, len = names.length; i < len; ++i) {
			new Thread(new Philosopher(i, names[i])).start();
		}
	}

}
