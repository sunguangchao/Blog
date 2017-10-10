package reference_and_value;

/**
 * Created by 11981 on 2017/10/10.
 */
public class ReferenceTest2 {
    private void test(AA a){
        a = new AA(); //增加此行
        a.age = 5;
        System.out.println("test()方法中：age = " + a.age);
    }

    public static void main(String[] args) {
        ReferenceTest2 referenceTest2 = new ReferenceTest2();
        AA a = new AA();
        a.age = 10;
        referenceTest2.test(a);
        System.out.println("main()方法中：age = " + a.age);

    }
}
class AA{
        public int age = 3;
}
/**
 * test()方法中：age = 5
 * main()方法中：age = 10
 */

