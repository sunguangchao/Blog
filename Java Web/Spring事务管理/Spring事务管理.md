Spring事务管理高层抽象主要包括三个接口：

* PlatformTransactionManager事务管理器、
* TransactionDefinition事务定义信息（隔离、传播、超时、只读）
* TransactionStatus事务具体运行状态

PlatformTransactionManager
------------------
Spring为不同的持久化框架提供了不同PlatformTransactionManager接口实现

| 事务                                       | 说明                             |
| ---------------------------------------- | ------------------------------ |
| org.springframework.jdbc.datasource.DataSourceTransactionManager | 使用Spring JDBC或iBatis进行持久化数据时使用 |
| org.springframework.orm.hibernate3.HibernateTransactionManager | 使用Hibernate3.0进行持久化数据时使用       |
| org.springframework.orm.jpa.JpaTransactionManager | 使用JPA进行持久化时使用                  |
| org.springframework.jdo.JdoTransactionManager | 当持久化机制是Jdo时使用                  |
| org.springframework.transaction.jta.JtaTransactionManager | 使用一个JTA来管理事务，在一个事务跨越过个资源时使用    |



TransactionDefinition
---------------

如果不考虑隔离性，可能会引发一些安全问题：

* 脏读：一个事务读取了另一个事务改写但还未提交的数据，如果这些数据被回滚，则读到的数据时无效的。
* 不可重复读：在同一事务中，多次读取同一数据返回的结果有所不同
* 幻读：一个事务读取了几行记录后，另一个事务插入一些记录，幻读就发生了。再后来的查询中，第一个事务就会发现有些原来没有的记录。

![](http://o90jubpdi.bkt.clouddn.com/%E4%BA%8B%E5%8A%A1%E7%9A%84%E9%9A%94%E7%A6%BB%E7%BA%A7%E5%88%AB.png)

其中MySQL数据库默认的是REPEATABLE_READ，Oracle数据库默认是READ_COMMITED

事务的传播行为：解决业务层方法之间的相互调用问题

![](http://o90jubpdi.bkt.clouddn.com/%E4%BA%8B%E5%8A%A1%E7%9A%84%E4%BC%A0%E6%92%AD%E8%A1%8C%E4%B8%BA%E3%80%81.png)



TransactionStatus
-------------

