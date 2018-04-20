package iooc.timer;

public class IntegerTest {
	public static void main(String[] args) {
		Integer a = new Integer(3);
		Integer b = 3; //将3装箱成Integer类型
		int c = 3;
		System.out.println(a == b);//false
		System.out.println(a == c);//拆箱后再比较-true
		
		Integer f1 = 100, f2 = 100, f3 = 150, f4= 150;
		System.out.println(f1 == f2);//true
		System.out.println(f3 == f4);//false
		//范围:-128至127是复用，不新建对象的
	}

}
