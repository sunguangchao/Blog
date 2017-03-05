import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
/**
 * Created by 11981 on 2017/3/5.
 *
 */
public class ReflecTest {
    public static Car initByDefaultConst() throws Throwable
    {
        //通过类装载器获取Car类对象
        //获取当前线程的ClassLoader，然后通过指定的类装载对应的反射实例
        ClassLoader loader = Thread.currentThread().getContextClassLoader();
        Class clazz = loader.loadClass("Car");//怎么找到这个Car的类，没出来结果可能是这里出问题。

        //获取类的默认构造器对象并通过它来实例化Car
        //通过Car的反射类对象获取Car的构造类对象cons
        //通过构造函数的newInstance方法实例化Car对象
        Constructor cons = clazz.getDeclaredConstructor((Class[])null);
        Car car = (Car)cons.newInstance();

        //通过反射方法设置属性
        //通过Car的反射类对象的getMethod获取属性setter方法对象
        //获取方法对象后，即可用invoke方法调用目标类的方法
        Method setBrand = clazz.getMethod("setBrand",String.class);
        setBrand.invoke(car,"红旗CA72");
        Method setColor = clazz.getMethod("setColor",String.class);
        setColor.invoke(car,"黑色");
        Method setMaxSpeed = clazz.getMethod("setMaxSpeed",int.class);
        setMaxSpeed.invoke(car,"200");

        return car;

    }
    public static void main(String[] args) throws Throwable{
        Car car = initByDefaultConst();
        car.introduce();
    }
}


/*ERROR:
* Exception in thread "main" java.lang.IllegalArgumentException: argument type mismatch
* */