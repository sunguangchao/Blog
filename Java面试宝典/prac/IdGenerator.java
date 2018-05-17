package concurrent.prac;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.Executor;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * IDÐòÁÐÉú³ÉÆ÷
 * @author huang
 *
 */
public class IdGenerator {
	private final AtomicInteger sequenceNumber = new AtomicInteger(0);
	
	public long next() {
		return sequenceNumber.getAndIncrement();
	}
	public static void main(String[] args) {
		Executor executor = new ThreadPoolExecutor(Runtime.getRuntime().availableProcessors(),
				10, 120L, TimeUnit.SECONDS, new ArrayBlockingQueue<Runnable>(200));
		executor.execute(new Runnable() {
			
			@Override
			public void run() {
				IdGenerator generator = new IdGenerator();
				for(int i = 0; i < 100; i++) {
					// TODO Auto-generated method stub
					System.out.println(generator.next());
				}
			}
		});
	}
}
