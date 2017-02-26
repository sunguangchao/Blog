
MyBatis 是支持定制化 SQL、存储过程以及高级映射的优秀的持久层框架。MyBatis 避免了几乎所有的
 JDBC 代码和手工设置参数以及抽取结果集。MyBatis 使用简单的 XML 或注解来配置和映射基本体，
 将接口和 Java 的 POJOs(Plain Old Java Objects,普通的 Java对象)映射成数据库中的记录。  

 [官方文档](http://www.mybatis.org/mybatis-3/)  
 [推荐一篇比较好的博客](http://blog.csdn.net/jiuqiyuliang/article/details/45286191)





Mybatis之SqlSession
-------------
SqlSession的作用：

1. 向SQL语句传入参数
2. 执行SQL语句
3. 获取SQL语句的结果
4. 事务的控制

如何得到SqlSession:

1. 通过配置文件获取数据库连接相关的信息
2. 通过配置信息构建SqlSessionFactory
3. 通过SqlSessionFactory打开数据库会话
