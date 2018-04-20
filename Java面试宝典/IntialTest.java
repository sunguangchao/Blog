package iooc.timer;
/**
 * 
 * @author sunguangchao
 * 创建对象时构造器的调用顺序：
 * 先初始化静态成员，然后调用父类构造器，
 * 再初始化非静态成员，最后调用自身构造器
 *
 */
public class IntialTest {

	public static void main(String[] args) {
		A a = new B();
		B b = new B();
	}
}

class A{
	static {
		System.out.print("1");
	}
	
	public A() {
		System.out.print("A");
	}
}

class B extends A{
	static {
		System.out.print("2");
	}
	public B() {
		System.out.print("B");
	}
}

/**
 * output:
 * 12ABAB
 */
