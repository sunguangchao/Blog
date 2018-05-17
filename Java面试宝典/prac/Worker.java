package concurrent.prac;
/**
 * CountDownLatch
 * @author huang
 * CountDownLatch是一种简单的同步模式，
 * 它让一个线程可以等待一个或多个线程完成它们的工作
 * 从而避免对临界资源并发访问所引发的各种问题。
 *
 */
public class Worker {
	private String name;
	
	private long workDuration;
	
	public Worker(String name, long workDuration) {
		this.name = name;
		this.workDuration = workDuration;
	}
	
	public void doWork() {
		System.out.println(name + " begins to work");
		try {
			Thread.sleep(workDuration);
		} catch (InterruptedException e) {
			// TODO: handle exception
			e.printStackTrace();
		}
		System.out.println(name + " has finish the job");
	}
}
