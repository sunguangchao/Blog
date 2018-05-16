package reflection;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * Created by 11981 on 2018/5/16.
 */
public class Client {
    public static void main(String[] args) {
        Foo foo = new Foo();
        Class c1 = Foo.class;
        Class c2 = foo.getClass();
        System.out.println(c1 == c2);

        Class c3 = null;
        try {
            c3 = Class.forName("reflection.Foo");
        }catch (ClassNotFoundException e){
            e.printStackTrace();
        }
        System.out.println(c2 == c3);


        try {
            Foo newFoo = (Foo)c1.newInstance();
            newFoo.print();
        }catch (InstantiationException e){
            e.printStackTrace();
        }catch (IllegalAccessException e){
            e.printStackTrace();
        }

        //方法的反射操作
        Class clazz = Foo.class;

        try {
            Method method = clazz.getDeclaredMethod("printStr", new Class[]{String.class, String.class});
            method.setAccessible(true);
            method.invoke(new Foo(), "key", "value");


            //构造函数的反射调用
            Class[] types = new Class[]{String.class};
            Constructor cons = Foo.class.getConstructor(types);
            Object[] objects = new Object[]{"a"};
            Foo fooo = (Foo) cons.newInstance(args);


        }catch (NoSuchMethodException e){
            e.printStackTrace();
        }catch (IllegalAccessException e){
            e.printStackTrace();
        }catch (InvocationTargetException e){
            e.printStackTrace();
        }catch (InstantiationException e){
            e.printStackTrace();
        }


    }
}
