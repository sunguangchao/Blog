package reflection;

import java.lang.reflect.Array;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;

/**
 * Created by 11981 on 2018/5/16.
 */
public class ClassUtil {
    public static void printClassMethodMessage(Object obj){
        Class c = obj.getClass();
        System.out.println("类的名称是:"+c.getName());
        Method[] ms = c.getMethods();
        for (int i = 0; i < ms.length; i++){
            //得到方法的返回值类型的类类型
            Class returnType = ms[i].getReturnType();
            System.out.print(returnType.getName()+" ");
            //得到方法的名称
            System.out.print(ms[i].getName()+"(");
            Class[] paramTypes = ms[i].getParameterTypes();
            for (Class clazz : paramTypes){
                System.out.print(clazz.getName()+",");
            }
            System.out.println(")");
        }
    }

    public static void printFieldMessage(Object obj){
        Class c = obj.getClass();

        Field[] fs = c.getDeclaredFields();
        for (Field field : fs){
            Class fieldType = field.getType();
            String typeName = fieldType.getName();
            String fieldName = field.getName();
            System.out.println(typeName + " " + fieldName);
        }
    }

    public static void printConMessage(Object obj){
        Class c = obj.getClass();

        Constructor[] cs = c.getConstructors();
        for (Constructor constructor : cs){
            System.out.print(constructor.getName()+"(");
            Class[] paramType = constructor.getParameterTypes();
            for (Class clazz : paramType){
                System.out.print(clazz.getName() + ",");
            }
            System.out.println(")");
        }
    }

    /**
     * 通过反射增加一个字段
     * @param name
     * @param obj
     * @return
     * @throws Exception
     */
    public static int incrementField(String name, Object obj) throws Exception{
        Field field = obj.getClass().getDeclaredField(name);
        int value = field.getInt(obj) + 1;
        field.set(obj, value);
        return value;
    }

    /**
     * 通过反射增加一个JavaBean属性
     * @param name
     * @param obj
     * @return
     * @throws Exception
     */
    public static int incrementProperty(String name, Object obj) throws Exception{
        String prop = Character.toUpperCase(name.charAt(0)) + name.substring(1);
        String mname = "get"+prop;
        Class[] types = new Class[]{};
        Method method = obj.getClass().getMethod(mname, types);
        Object result = method.invoke(obj, new Object[0]);
        int value = ((Integer)result).intValue() + 1;
        mname = "set" + prop;
        types = new Class[]{int.class};
        method = obj.getClass().getMethod(mname, types);
        method.invoke(obj, new Object[] {new Integer(value)});
        return value;
    }

    /**
     * 通过反射来扩展一个数组
     * @param array
     * @param size
     * @return
     */
    public Object growArray(Object array, int size){
        Class type = array.getClass().getComponentType();
        Object grown = Array.newInstance(type, size);
        System.arraycopy(array, 0, grown, 0, Math.min(Array.getLength(array), size));
        return grown;
    }

    public static void main(String[] args) {
        Foo foo = new Foo();
        printClassMethodMessage(foo);
        printFieldMessage(foo);
        printConMessage(foo);
    }
}
