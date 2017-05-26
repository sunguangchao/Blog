ClassLoader的三个作用：
* 将Class加载到JVM
* 审查每个类应该由谁加载，是一种父优先的等级加载制度
* 将Class字节码解析成JVM统一要求的对象格式

ClassLoader类结构分析
-----------
ClassLoader的方法：
* `defineClass()`：方法用来将byte字节流解析成JVM能够识别的Class对象
* `findClass()`：通过直接覆盖`ClassLoader`父类的`findClass`方法来实现类的加载规则，从而取得要加载类的字节码。然后调用`defineClass`方法生成类的Class对象
* `resolveClass()`：如果想在类被加载到JVM时就被链接(Link),那么可以调用`resloveClass`方法
* `loadClass()`：如果只想在运行时加载自己制定的一个类，可以用`this.getClass().getClassLoader().loadClass("class")`调用ClassLoader的loadClass方法以获取这个类的Class对象。

ClassLoader的等级加载制度
* Bootstrap ClassLoader（引导类加载器）
   * 用来加载Java的核心库，是用原生代码实现的，并不继承自java.lang.ClassLoader
* ExtClassLoader（扩展类加载器）
   * 用来加载Java的扩展库。Java虚拟机的实现会提供一个扩展库目录。该加载器在此目录里面查找并加载Java类。服务目标在`System.getProperty("java.ext.dirs")`目录下
* AppClassLoader（系统类加载器）
   * 所有在`System.getProperty("java.class.path")`目录下的类都可以被这个类加载器加载。它根据Java应用的类路径(CLASSPATH)来加载Java类。一般来说，Java的类都是由它来加载完成的。可以通过`ClassLoader.getSystemClassLoader()`来获取它

类加载器树状组织结构示意图
![](http://o90jubpdi.bkt.clouddn.com/ClassLoader.png)

如何加载class文件
------------
1. 加载字节码到内存
   * 子类URLClassLoader如何实现`findClass()`：在URLClassLoader中通过一个URLClassPath类帮助取得要加载的class文件字节流，而这个URLClassPath定义了到哪里去找到这个class文件，如果找到这个class文件，再读取它的byte字节流，通过调用defineClass()方法来创建对象。
2. 验证与解析
   * 字节码验证，类装入器要对类的字节码做许多检测，以确保格式正确、行为正确
   * 类准备，这个阶段要准备每个类中定义的字段、方法和实现接口所必须的数据结构
   * 解析，在这个阶段类装入器装入类所引用的其他所有类。
3. 初始化Class对象
   * 在类中包含的静态初始化器都被执行，在这一阶段末尾静态字段被初始化为默认值

[深入探讨Java类加载器](https://www.ibm.com/developerworks/cn/java/j-lo-classloader/#code1)

常见加载类错误分析
-------------
书中列举了很多常见的加载类错误分析，这里就不详细说了，等碰到的时候回来看看
* ClassNotFoundException
* NoClassDefFoundError
* UnsatisfiedLinkError
* ClassCastException
* ExceptionInInitializerError

常见的ClassLoader分析
-------------------
在这个Servlet中打印加载它的ClassLoader:
```
public class HelloWorldServlet extends HttpServlet{
 public void deGet(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException{
   ClassLoader classLoader = this.getClass().getClassLoader();
   while (classLoader != null){
     System.out.print(classLoader.getClass().getCanonicalName());
     classLoader = classLoader.getParent();
   }
 }
}
```
Tomcat有关的ClassLoader的实现原理。
一个应用在Tomcat中由一个StandardContext表示，由StandardContext来解释Web应用的web.xml配置文件实例化所有Servlet。Servlet的class是由<servlet-class>来指定的，所以，每个Servlet类的加载肯定是通过显式加载方法加载到Tomcat容器中的。


如何实现自己的ClassLoader
----------
实现一个ClassLoader:
```
public class PathClassLoader extends ClassLoader{
   private String classPath;

   public PathClassLoader(String classPath){
       this.classPath = classPath;
   }

   protected Class<?> findClass(String name) throw ClassNotFoundExecption{
       if(packageName.startsWith(name)){
           byte[] classData = getDate(name);
           if(classData == null){
               throw new ClassNotFoundExecption;
           }else{
               return defineClass(name, classData, 0, classData.length);
           }
       }else{
           return super.loadClass(name);
       }
   }

   private byte[] getDate(String className){
       String path = classPath + File.separatorChar + className.replace
       ('.', File.separatorChar) + ".class";
       try{
           InputStream is = new FileInputStream(path);
           ByteArrayOutputStream stream = new ByteArrayOutputStream();
           byte[] buffer = new byte[2048];
           int num = 0;
           while((num = is.read(buffer)) != -1){
               stream.write(buffer, 0, num);
           }
           return stream.toByteArray();
       }catch(IOException w){
           e.printStackTrace();
       }
       return null;
   }
}
```

继承URLClassLoader类，然后设置自定义路径的URL来加载URL下的类
```
public class URLPathClassLoader extends URLCLassLoader{
   private String packageName = "net.xulingbo.classloader";

   public URLPathClassLoader(URL[] classPath, ClassLoader parent){
       super(classPath,parent);
   }

   protected Class<?> findClass(String name) throw ClassNotFoundExecption{
       Class<?> aClass = findLoadedClass(name);
       if(aClass != null){
           return aClass;
       }
       if(!packageName.startsWith(name)){
           return super.loadClass(name);
       }else{
           return findClass(name);
       }
   }
}
```
实现类的热部署
-------------
JVM判断是否为同一个类有两个条件：
* 看这个类的完整类名是否一样
* 看加载这个类的ClassLoader是否是同一个
