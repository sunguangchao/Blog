学习目标

* 了解HQL定义以及HQL语句形式
* 掌握Query对象的使用
* 能够编写符合数据查询要求的HQL语句

了解HQL
============
Hibernate Query Language  
HQL是面向对象的查询语言  

HQL：映射文件的持久化类及其属性
SQL：数据库表

HQL语句形式：  
`select` `from` `where` `group by` `having` `order by`

注意：  

* HQL是面向对象的查询语言，对Java类与属性发小写敏感  
* HQL对关键字不区分大小写

准备查询
============
Query接口简介
------

* `org.hibernate.Query`接口
    1. Query接口定义有执行查询的方法
    2. Query接口支持方法链编程风格，使得程序代码更为简洁
* Query实例创建
    1. Session的`createQuery()`方法创建Query实例
    2. createQuery方法包含一个HQL语句参数，`createQuery(hql)`
* Query执行查询
    1. Query接口的`list()`方法执行HQL查询
    2. `list()`方法返回结果数据类型为`java.util.List`，List集合中存放符合查询条件的持久化对象

from子句
----------
1. HQL语句最简形式
2. from指定了HQL语句查询主体-持久化类及其属性
