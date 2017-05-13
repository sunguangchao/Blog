学习目标
-------
* http协议无状态性
* 保存用户状态的两大机制
* Cookie简介
* Cookie的创建和使用
* Session与Cookie的对比

http协议的无状态性
-------------
无状态是指，当浏览器发送请求给服务器的时候，服务器响应客户端请求

但当同一个浏览器再次发送请求给服务器的时候，服务器并不知道它就是刚才那个浏览器。简单地说，就是服务器不会记得你，所以就是无状态协议。

保存用户状态的两大机制
------------
Session和Cookie

什么是Cookie
-------------
是Web服务器保存在客户端的一列文本信息

应用：

1. 判断注册用户是否已经登录网站
2. 购物车的处理

Cookie的作用：
* 对特定对象的追踪
* 保存用户网页浏览记录与习惯
* 简化登录

安全风险：容易泄露用户信息

Jsp中创建与使用Cookie
-----------------
```java
//创建Cookie对象
Cookie newCookie = new Cookie(String key,Object value);
//写入Cookie对象
response.addCookie(newCookie);
//读取Cookie对象
Cookie[] cookies = request.getCookies();
```
常用方法：
* void setMaxAge(int expiry) 设置cookie的有效期，以秒为单位
* void setValue(String value) 在cookie创建后，对cookie进行赋值
* String getName() 获取cookie的名称
* String getValue() 获取cookie的值
* int getMaxAge() 获取cookie的有效时间，以秒为单位

Session与Cookie的区别
----------------------
![](http://o90jubpdi.bkt.clouddn.com/Session%E4%B8%8ECookie%E5%8C%BA%E5%88%AB.png)
