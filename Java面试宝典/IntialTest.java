package iooc.timer;
/**
 * 
 * @author sunguangchao
 * ��������ʱ�������ĵ���˳��
 * �ȳ�ʼ����̬��Ա��Ȼ����ø��๹������
 * �ٳ�ʼ���Ǿ�̬��Ա����������������
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
