package iooc.timer;

public class IntegerTest {
	public static void main(String[] args) {
		Integer a = new Integer(3);
		Integer b = 3; //��3װ���Integer����
		int c = 3;
		System.out.println(a == b);//false
		System.out.println(a == c);//������ٱȽ�-true
		
		Integer f1 = 100, f2 = 100, f3 = 150, f4= 150;
		System.out.println(f1 == f2);//true
		System.out.println(f3 == f4);//false
		//��Χ:-128��127�Ǹ��ã����½������
	}

}
