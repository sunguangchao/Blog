package reference_and_value;

/**
 * Created by 11981 on 2017/10/10.
 */
public class ReferenceTest {
    private void test(A a){
        a.age = 5;
        System.out.println("test()方法中：age = " + a.age);
    }

    public static void main(String[] args) {
        ReferenceTest referenceTest = new ReferenceTest();
        A a = new A();
        a.age = 10;
        referenceTest.test(a);
        System.out.println("main()方法中：age = " + a.age);

    }

}
class A{
    public int age = 3;
}
/**output:
 * test()方法中：age = 5
 * main()方法中：age = 5
 */
