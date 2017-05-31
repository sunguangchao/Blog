ServletGetFormInfo
------------------------
此项目是用表单实现一个注册的功能，并将信息保存在session中，跳转到另一个页面展示出来

结果：
![](http://o90jubpdi.bkt.clouddn.com/%E7%94%A8%E6%88%B7%E6%B3%A8%E5%86%8C.png)
![](http://o90jubpdi.bkt.clouddn.com/%E7%94%A8%E6%88%B7%E4%BF%A1%E6%81%AF.png)

ServletCart
----------------
Servlet实现一个购物车的功能

* 商品展示
* 商品添加删除
* 计算购物车内总的商品价格

![](http://o90jubpdi.bkt.clouddn.com/%E5%95%86%E5%93%81%E5%B1%95%E7%A4%BA.png)
![](http://o90jubpdi.bkt.clouddn.com/%E8%B4%AD%E7%89%A9%E8%BD%A6.png)

**问题总结：**  
一开始做的时候报了一个错，如下图所示

![](http://o90jubpdi.bkt.clouddn.com/%23error.png)

detail.jsp文件中做出如下修改，原因是JDK版本问题。

```java
list+=request.getParameter("id")+"#";
//如果浏览记录超过1000条，清零.
String[] arr = list.split("#");
if(arr!=null&&arr.length>0)
{
   if(arr.length>=1000)
   {
       list="";
   }
}
Cookie cookie = new Cookie("ListViewCookie",list);
response.addCookie(cookie);
//将逗号，换成#，记得itemdao的方法也是
```

