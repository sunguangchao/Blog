
单一主键
-------------
生成策略：
* assigned:由Java应用程序负责生成(手工赋值)
* native:由底层数据库自动生成标识符，如果是MySQL就是increment，如果是Oracle就是sequence，等等。

基本类型
------------
![](http://o90jubpdi.bkt.clouddn.com/hibernate%E6%95%B0%E6%8D%AE%E7%B1%BB%E5%9E%8B.png)

对象类型
------------
| 映射类型   | Java类型           | 标准SQL类型 | MySQL类型 | Qracle类型 |
| ------ | ---------------- | ------- | ------- | -------- |
| binary | byte[]           | VARCHAR | BLOB    | BLOB     |
| text   | java.lang.String | CLOB    | TEXT    | CLOB     |
| clob   | java.sql.Clob    | CLOB    | TEXT    | CLOB     |
| blob   | java.sql.Blob    | BLOB    | BLOB    | BLOB     |

组件属性
-----------
实体类中的某个属于用户自定义的类的对象
```xml
<component name="address" class="Address">
	<property name="postcode" column="POSTCODE"></property>
	<property name="phone" column="PHONE"></property>
	<property name="address" column="ADDRESS"></property>
</component>
```
单表CRUD操作实例
* save
* update
* delete
* get/load(查询单个记录)

get和load的区别
----------
在不考虑缓存的情况下，get方法会在调用之后立即向数据库发出sql语句，返回持久化对象。

laod方法会在调用后返回一个代理对象。该代理对象只保存了实体对象的id，直到使用对象的非主键属性时才会发出sql语句。

查询数据库不存在的数据时，get方法返回null，load方法抛出异常`org.hibernate.ObjectNotFoundException`



