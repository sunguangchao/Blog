package concurrent.philosopher;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class CallableTest {
	private static final int POOL_SIZE = 10;
	static class CallThread implements Callable<Double>{
		private List<Double> dataList = new ArrayList<>();
		
		public CallThread() {
			for(int i =0; i < 10000; ++i) {
				dataList.add(Math.random());
			}
		}

		@Override
		public Double call() throws Exception {
			double total = 0;
			for(Double d : dataList) {
				total += d;
			}
			return total / dataList.size();
		}
		
	}
	
	public static void main(String[] args) {
		List<Future<Double>> fList = new ArrayList<>();
		ExecutorService es = Executors.newFixedThreadPool(POOL_SIZE);
		for(int i=0; i < POOL_SIZE; ++i) {
			fList.add(es.submit(new CallThread()));
		}
		
		for(Future<Double> f : fList) {
			try {
				//获取结果。如果结果还没有准备好，get方法会阻塞直到取得结果；
				//当然也可以通过参数设置阻塞超时时间。
				System.out.println(f.get());
			} catch (InterruptedException | ExecutionException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		es.shutdown();
	}
}
