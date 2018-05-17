package concurrent.copyonwrite;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class CooyOnWriteArrayListTest {
	private static final int THREA_POOL_ZISE = 2;
	public static void main(String[] args) {
//		List<Double> list = new ArrayList<>();
		List<Double> list = new CopyOnWriteArrayList<>();
        ExecutorService es = Executors.newFixedThreadPool(THREA_POOL_ZISE);
        es.execute(new AddThread(list));
        es.execute(new AddThread(list));
        es.shutdown();
	}

}
