package concurrent.prac;
/**
 * CountDownLatch
 * @author huang
 * CountDownLatch��һ�ּ򵥵�ͬ��ģʽ��
 * ����һ���߳̿��Եȴ�һ�������߳�������ǵĹ���
 * �Ӷ�������ٽ���Դ���������������ĸ������⡣
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
