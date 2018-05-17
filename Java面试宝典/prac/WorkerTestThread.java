package concurrent.prac;

import java.util.concurrent.CountDownLatch;

public class WorkerTestThread implements Runnable{
	private Worker worker;
	private CountDownLatch latch;
	
	public WorkerTestThread(Worker worker, CountDownLatch latch) {
		// TODO Auto-generated constructor stub
		this.worker = worker;
		this.latch = latch;
	}

	@Override
	public void run() {
		// TODO Auto-generated method stub
		worker.doWork();//工作人员开始工作
		latch.countDown();//计时器减一
		
	}
	

}
