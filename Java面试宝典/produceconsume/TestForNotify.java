package concurrent.produceconsume;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * 公共常量
 * @author huang
 *
 */
class Constants {
    public static final int MAX_BUFFER_SIZE = 10;
    public static final int NUM_OF_PRODUCER = 2;
    public static final int NUM_OF_CONSUMER = 3;
}

class Task{
	private String id;
	public Task() {
		id = UUID.randomUUID().toString();
	}
	
	@Override
	public String toString() {
		return "Task[" + id + "]";
	}
}
/**
 * 消费者
 * @author huang
 *
 */
class Consumer implements Runnable{
	private List<Task> buffer;
	
	public Consumer(List<Task> buffer) {
		// TODO Auto-generated constructor stub
		this.buffer = buffer;
	}

	@Override
	public void run() {
		// TODO Auto-generated method stub
		while(true) {
			synchronized (buffer) {
				while(buffer.isEmpty()) {
					try {
						buffer.wait();
					} catch (InterruptedException e) {
						// TODO: handle exception
						e.printStackTrace();
					}
				}
				Task task = buffer.remove(0);
				buffer.notifyAll();
				System.out.println("Consumer[" + Thread.currentThread().getName() + "] got " + task);
			}
		}
		
	}
	
}
/**
 * 生产者
 * @author huang
 *
 */
class Producer implements Runnable{
	private List<Task> buffer;
	
	public Producer(List<Task> buffer) {
		// TODO Auto-generated constructor stub
		this.buffer = buffer;
	}

	@Override
	public void run() {
		while(true) {
			synchronized (buffer) {
				while (buffer.size() >= Constants.MAX_BUFFER_SIZE) {
					try {
						buffer.wait();
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
				Task task = new Task();
				buffer.add(task);
				buffer.notifyAll();
				System.out.println("Producer[" + Thread.currentThread().getName() + "] put " + task);
			}
		}
		
	}
	
}

public class TestForNotify {
	public static void main(String[] args) {
		List<Task> buffer = new ArrayList<>(Constants.MAX_BUFFER_SIZE);
		ExecutorService es = Executors.newFixedThreadPool(Constants.NUM_OF_CONSUMER + Constants.NUM_OF_PRODUCER);
		for(int i=1; i < Constants.NUM_OF_PRODUCER; ++i) {
			es.execute(new Producer(buffer));
		}
		for(int i=1; i < Constants.NUM_OF_CONSUMER; ++i) {
			es.execute(new Consumer(buffer));
		}
	}
}
