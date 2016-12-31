response对象
------------
response 对象包含了响应客户请求的有关信息，但在jsp中很少直接用到它。它是`HttpServletResponse`类的实例。`response`对象具有页面作用域，即访问一个页面时，该页面内的 response 对象只能对这次访问有效，其他页面的 response 对象对当前页面无效，常用的方法有：

* String getCharacterEncoding() 返回响应的是何种字符编码
* void setContentType(String type) 设置响应的MIME类型
* PrintWriter getWriter() 返回可以向客户端输出字符的一个对象
* sendRedirect(java.lang.String location) 重新定向客户端的请求

