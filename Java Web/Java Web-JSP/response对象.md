 response对象
------------
response 对象包含了响应客户请求的有关信息，但在jsp中很少直接用到它。它是`HttpServletResponse`类的实例。`response`对象具有页面作用域，即访问一个页面时，该页面内的 response 对象只能对这次访问有效，其他页面的 response 对象对当前页面无效，常用的方法有：

* String getCharacterEncoding() 返回响应的是何种字符编码
* void setContentType(String type) 设置响应的MIME类型
* PrintWriter getWriter() 返回可以向客户端输出字符的一个对象
* sendRedirect(java.lang.String location) 重新定向客户端的请求

注：`PrintWriter`对象总是在内置`outer`对象之前输出，如果想改变这种情况，可以使用`flush()`方法

请求转发与请求重定向
---------------------

* 请求重定向：客户端行为，`response.sendRedirect();`
	* 从本质上讲等于两次请求，前一次的请求不会保存，地址栏的URL地址会改变
* 请求转发：服务器行为，`resquest.getRequestDispatcher().forward(req,resp);`
	* 是一次请求，转发后请求对象会保存，地址栏的URL地址不会改变