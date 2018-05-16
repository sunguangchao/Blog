package reflection;

/**
 * Created by 11981 on 2018/5/16.
 */
public class Foo {
    String s1 = null;
    String s2 = "null";
    public Foo(String s1){
        this.s1 = s1;
    }
    public Foo(){

    }

    public void print(){
        System.out.println("调用print函数");
    }

    private String printStr(String str1, String str2){
        System.out.println(str1 + "-" + str2);
        return str1+str2;
    }
}
