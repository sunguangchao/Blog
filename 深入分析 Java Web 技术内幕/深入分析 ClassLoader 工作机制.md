ClassLoader的三个作用：
* 将Class加载到JVM
* 审查每个类应该由谁加载，是一种父优先的等级加载制度
* 将Class字节码解析成JVM统一要求的对象格式

ClassLoader类结构分析
-----------
`defineClass`方法用来将byte字节流解析成JVM能够识别的Class对象

我们通过直接覆盖`ClassLoader`父类的`findClass`方法来实现类的加载规则，从而取得要加载类的字节码。然后调用`defineClass`方法生成类的Class对象，如果想在类被加载到JVM时就被链接(Link),那么可以调用`resloveClass`方法

如果只想在运行时加载自己制定的一个类，可以用`this.getClass().getClassLoader().loadClass("class")`调用ClassLoader的loadClass方法以获取这个类的Class对象。

ClassLoader的等级加载制度
* Bootstrap ClassLoader
    * 主要加载JVM自身工作需要的类，仅是一个类加载工具
* ExtClassLoader
    * 服务目标在`System.getProperty("java.ext.dirs")`目录下
* AppClassLoader
    * 所有在`System.getProperty("java.class.path")`目录下的类都可以被这个类加载器加载

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

常见加载类错误分析
-------------
书中列举了很多常见的加载类错误分析，这里就不详细说了，等碰到的时候回来看看
* ClassNotFoundException
* NoClassDefFoundError
* UnsatisfiedLinkError
* ClassCastException
* ExceptionInInitializerError

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
