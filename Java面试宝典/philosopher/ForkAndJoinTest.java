package concurrent.philosopher;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.Future;
import java.util.concurrent.RecursiveTask;
/**
 * 
 * Java 7中还提供了分支/合并（fork/join）框架，
 * 它可以实现线程池中任务的自动调度，并且这种调度对用户来说是透明的。
 * 为了达到这种效果，必须按照用户指定的方式对任务进行分解，
 * 然后再将分解出的小型任务的执行结果合并成原来任务的执行结果。
 * 这显然是运用了分治法（divide-and-conquer）的思想。
 * 下面的代码使用了分支/合并框架来计算1到10000的和，
 * 当然对于如此简单的任务根本不需要分支/合并框架，
 * 因为分支和合并本身也会带来一定的开销，但是这里我们只是探索一下在代码中如何使用分支/合并框架，
 * 让我们的代码能够充分利用现代多核CPU的强大运算能力。
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
		// 当问题分解到可求解程度时直接计算结果
		if ((end - start) < THRESHOLD) {
			for(int i = start; i <= end; i++) {
				sum += i;
			}
		}else {
			int middle = (end + start) >>> 1;
			//将任务一分为2
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
