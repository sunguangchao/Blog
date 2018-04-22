

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;

import java.lang.reflect.InvocationTargetException;

/**
 * 反射工具类
 * Created by 11981 on 2018/4/22.
 */
public final class ReflectionUtils {
    private ReflectionUtils(){
        throw new AssertionError();
    }

    /**
     * 通过反射取对象指定字段（属性）的值
     * @param target
     * @param fieldName
     * @return
     */
    public static Object getValue(Object target, String fieldName){
        Class<?> clazz = target.getClass();
        String[] fs = fieldName.split("\\.");
        try {
            for (int i=0; i < fs.length; i++){
                Field f = clazz.getDeclaredField(fs[i]);
                f.setAccessible(true);
                target = f.get(target);
                clazz = target.getClass();
            }
            Field f = clazz.getDeclaredField(fs[fs.length - 1]);
            f.setAccessible(true);
            return f.get(target);
        }catch (NoSuchFieldException e){
            e.printStackTrace();
        }catch (  IllegalAccessException  e){
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 通过反射给对象的指定字段赋值
     * @param target
     * @param fieldName
     * @param value
     */
    public static void setValue(Object target, String fieldName, Object value){
        Class<?> clazz = target.getClass();
        String[] fs = fieldName.split("\\.");
        try {
            for (int i=0; i < fs.length; i++){
                Field f = clazz.getDeclaredField(fs[i]);
                f.setAccessible(true);
                Object val = f.get(target);
                if (val == null){
                    Constructor<?> c = f.getType().getDeclaredConstructor();
                    c.setAccessible(true);
                    val = c.newInstance();
                    f.set(target, val);
                }
                target = val;
                clazz = target.getClass();
            }
            Field f = clazz.getDeclaredField(fs[fs.length - 1]);
            f.setAccessible(true);
            f.set(target, value);
        }catch (NoSuchFieldException e){
            e.printStackTrace();
        }catch (IllegalAccessException e){
            e.printStackTrace();
        }catch (NoSuchMethodException e){
            e.printStackTrace();
        }catch (InstantiationException e){
            e.printStackTrace();
        }catch (InvocationTargetException e){
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {

    }
}
