package concurrent.philosopher;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.Future;
import java.util.concurrent.RecursiveTask;
/**
 * 
 * Java 7�л��ṩ�˷�֧/�ϲ���fork/join����ܣ�
 * ������ʵ���̳߳���������Զ����ȣ��������ֵ��ȶ��û���˵��͸���ġ�
 * Ϊ�˴ﵽ����Ч�������밴���û�ָ���ķ�ʽ��������зֽ⣬
 * Ȼ���ٽ��ֽ����С�������ִ�н���ϲ���ԭ�������ִ�н����
 * ����Ȼ�������˷��η���divide-and-conquer����˼�롣
 * ����Ĵ���ʹ���˷�֧/�ϲ����������1��10000�ĺͣ�
 * ��Ȼ������˼򵥵������������Ҫ��֧/�ϲ���ܣ�
 * ��Ϊ��֧�ͺϲ�����Ҳ�����һ���Ŀ�����������������ֻ��̽��һ���ڴ��������ʹ�÷�֧/�ϲ���ܣ�
 * �����ǵĴ����ܹ���������ִ����CPU��ǿ������������
 *
 */
class Calculator extends RecursiveTask<Integer>{
	private static final long serialVersionUID = 7333472779649130114L;
	private static final int THRESHOLD = 10;
	
	private int start;
	private int end;
	
	public Calculator(int start, int end) {
		// TODO Auto-generated constructor stub
		this.start = start;
		this.end = end;
	}
	

	@Override
	protected Integer compute() {
		int sum = 0;
		// ������ֽ⵽�����̶�ʱֱ�Ӽ�����
		if ((end - start) < THRESHOLD) {
			for(int i = start; i <= end; i++) {
				sum += i;
			}
		}else {
			int middle = (end + start) >>> 1;
			//������һ��Ϊ2
			Calculator left = new Calculator(start, middle);
			Calculator right = new Calculator(middle, end);
			left.fork();
			right.fork();
			sum = left.join() + right.join();
		}
		return sum;
	}
	
}


public class ForkAndJoinTest {
	public static void main(String[] args) throws InterruptedException, ExecutionException {
		ForkJoinPool pool = new ForkJoinPool();
		Future<Integer> result = pool.submit(new Calculator(1, 10000));
		System.out.println(result.get());
	}
}
