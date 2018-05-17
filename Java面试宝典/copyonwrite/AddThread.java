package concurrent.copyonwrite;

import java.util.List;

public class AddThread implements Runnable{
	private List<Double> list;
	
	public AddThread(List<Double> list) {
		// TODO Auto-generated constructor stub
		this.list = list;
	}

	@Override
	public void run() {
		// TODO Auto-generated method stub
		for(int i= 0 ; i < 10000; ++i) {
			list.add(Math.random());
		}
		
	}
	

}
