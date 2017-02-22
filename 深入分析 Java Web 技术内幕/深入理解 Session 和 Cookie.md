学习目标：
如何处理跨域名来共享Cookie的问题




Session和Cookie的作用都是为了保持访问用户与后端服务器的交互状态
理解Cookie
==============
Cookie的工作机制是用户识别及状态管理。Web网站为了管理用户的状态会通过Web浏览器，把一些数据临时写入计算机内。接着当用户访问该网站时，可通过通信方式取回之前发放的Cookie。
我们用如下方式创建Cookie:
```
String getCookie(Cookie[] cookies, String key){
	if (cookies != null) {
		for (Cookie cookie : cookies){
			if (cookie.getName().equals(key)) {
				return cookie.getValue();
			}
		}

	}
	return null;
}
@Override
public void doGet(HttpServletRequest request, HttpServletResponse response)
throws IOException, ServletException{
	Cookie[] cookies = request.getCookies();
	String userName = getCookie(cookies, "userName");
	String userAge = getCookie(cookies, "userAge");
	if (userName == null) {
		response.addCookie(new Cookie("userName", "guangchao"));
	}
	if (userAge == null) {
		response.addCookie(new Cookie("userAge", "24"));
	}
	response.getHeaders("Set-Cookie");
}

```
理解Session
============
Session工作的三种方式
* 基于URL Path Parameter，默认支持
* 基于Cookie，如果没有修改Context容器的Cookie标识，则默认也是支持的。
* 基于SSL，默认不支持，只有connector.getAttribute("SSLEnable")为True时才支持。

session如何工作？-P270
------------

StandardManager类负责Servlet容器中所有的StandardSession对象的生命周期管理

分布式Session框架
=============
首先，统一通过订阅服务器推送配置，可以有效几种管理资源。如Zookeeper集群管理服务器。

如何封装HttpSession对象和拦截请求  
在应用的web.xml中配置一个`SessionFilter`，用于在请求到达MVC框架之前封装`HttpServletRequest`和`HttpServletResponse`对象，并创建自己的`InnerHttpSession`对象，把它设置到request和response对象中。


Cookie压缩
==========
表单重复提交问题
============
多终端Session统一
========
这里还讲解到了手机扫码登录的原理
