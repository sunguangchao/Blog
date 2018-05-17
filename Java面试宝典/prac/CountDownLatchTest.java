package concurrent.prac;

import java.util.concurrent.CountDownLatch;

public class CountDownLatchTest {

	private static final int MAX_WORK_DURATION = 5000;
	private static final int MIN_WORK_DURATION = 1000;
	
	private static long getRandomWorkDuration(int min, int max) {
		return (long)(Math.random() * (max - min) + min);
	}
	public static void main(String[] args) {
		CountDownLatch latch = new CountDownLatch(2);//创建倒计时，并指定倒计时次数为2
		Worker worker1 = new Worker("sungun", getRandomWorkDuration(MIN_WORK_DURATION, MAX_WORK_DURATION));
		Worker worker2 = new Worker("王大锤", getRandomWorkDuration(MIN_WORK_DURATION, MAX_WORK_DURATION));
		new Thread(new WorkerTestThread(worker1, latch)).start();
		new Thread(new WorkerTestThread(worker2, latch)).start();
		
		try {
			latch.await();
			System.out.println("all jods have been finished");
		} catch (Exception e) {
			// TODO: handle exception
		}
	}
}
