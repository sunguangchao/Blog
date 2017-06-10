什么是ORM
-----------
ORM(Object/Relationship Mapping):对象/关系映射  
利用面向对象思想编写的数据库应用程序最终都是把对象信息保存在关系型数据库中，于是要编写很多和底层数据库相关的SQL语句

写SQL语句有什么不好吗？
* 不同的数据库使用的SQL语法不同
* 同样功能在不同数据库中有不同的实现方式。比如分页SQL
* 程序过分依赖SQL对程序的移植及扩展，维护带来很大麻烦

什么是Hiberbate
--------------
Hibernate是Java领域的一款开源的ORM框架技术。  
Hibernate对JDBC进行了非常轻量级的对象封装。  

* 进行使用前，需要安装一个插件：[hibernate tools for eclipse plugins](http://hibernate.org/tools/)
* Hibernate jar 包：http://hibernate.org/orm/downloads/

编写第一个Hibernate例子
------------------
流程：

* 创建Hibernate的配置文件-hibernate.cfg.xml
* 创建持久化类
* 创建对象-关系映射文件
* 通过Hibernate API编写访问数据库的代码



hibernate.cfg.xml:

```xml
<hibernate-configuration>
    <session-factory>
      <property name="connection.username">root</property>
      <property name="connection.password">root</property>
      <property name="connection.driver_class">com.mysql.jdbc.Driver</property>
      <property name="connection.url">jdbc:mysql://localhost:3306/hibernate?useUnicode=true&amp;characterEncoding=UTF-8</property>
      <property name="dialect">org.hibernate.dialect.MySQL5Dialect</property>
      
      <property name="show_sql">true</property>
      <property name="format_sql">true</property>
      <property name="hbm2ddl.auto">create</property>
      <property name="current_session_context_class">thread</property>
      <mapping class="Students"/>
      <mapping resource="Students.hbm.xml"/>
    </session-factory>
</hibernate-configuration>
```

持久化类使用JavaBeans设计原则：
* 共有的类

* 提供公有的不带默认参数的默认的构造方法

* 属性私有

* 属性setter/getter封装

  ​


使用Junit进行测试：
* @Test：测试方法  
* @Before：初始化方法  
* @After：释放资源  