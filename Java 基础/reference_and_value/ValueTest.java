package reference_and_value;

/**
 * Created by 11981 on 2017/10/10.
 */
public class ValueTest {
    private void test(int a){
        a = 5;
        System.out.println("test()方法中：a = " + a);
    }

    public static void main(String[] args) {
        ValueTest valueTest = new ValueTest();
        int a = 3;
        valueTest.test(a);
        System.out.println("main()方法中：a = " + a);
    }
}
