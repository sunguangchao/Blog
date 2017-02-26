什么是ORM
-----------
ORM(Object/Relationship Mapping):对象/关系映射
写SQL语句有什么不好吗？

* 不同的数据库使用的SQL语法不同
* 同样功能在不同数据库中有不同的实现方式
* 程序过分依赖SQL对程序的移植及扩展，维护带来很大麻烦

什么是Hiberbate
--------------
Hibernate是Java领域的一款开源的ORM框架技术
Hibernate对JDBC进行了非常轻量级的对象封装

进行使用前，需要安装一个插件：[hibernate tools for eclipse plugins](http://hibernate.org/tools/)

Hibernate jar 包：http://hibernate.org/orm/downloads/

编写第一个Hibernate例子
------------------
流程：

* 创建Hibernate的配置文件
* 创建持久化类
* 创建对象-关系映射文件
* 通过Hibernate API编写访问数据库的代码

@Test:测试方法  
@Before：初始化方法  
@After：释放资源  
