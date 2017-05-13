JSP内置对象是Web容器创建的一组对象，不使用new关键字就可以使用的内置对象。
九大内置对象：out,request,response,session,  
page,pageContext,config,exception

out对象
----------
out对象是JspWriter类的实例，是向客户端输出内容常用的对象  
常用方法：

* void println() 向客户端打印字符串
* void clear() 清除缓冲区的内容，如果在flush之后调用会抛出异常
* void clearBuffer() 清除缓冲区的内容，如果在flush之后调用不会抛出异常
* void flush() 将缓冲区内容输出到客户端
* int getBufferSize() 返回缓冲区字节数的大小，如不设缓冲区则为0
* int getRemaining() 返回缓冲区还剩余多少可用
* void close() 关闭输出流

get与post区别
-------------
get：以明文的方式通过URL提交数据，数据在URL中可以看到。提交的数据最多不超多2KB。安全性较低但效率比post方式高，适合提交数据量不大，安全性不高的数据，比如：搜索，查询等功能。

post：将用户提交的信息封装在HTML HEADER内。适合提交数据量大，安全性高的用户信息。比如：注册、修改、上传等功能。

requset对象
-------------
客户端的请求信息被封装在request中，通过它才能了解到客户的需求，然后做出响应。它是HttpServletRequset类的实例。request对象具有请求域，即完成客户端的请求之前，该对象一直有效。

* String getParameter(String name) 返回name指定参数的参数值
* String[] getParameterValues(String name)返回包含参数name的所有值的数组
* void setAttribute(String, Object)存储此请求中的属性
* Object getAttribute(String name)返回指定属性的属性值
* String getContextType()得到请求体的MIME类型
* String getProtocol()返回请求用的协议类型及版本号
* String getServerName()返回接受请求的服务器主机名


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
	* 从本质上讲等于两次请求，前一次的请求不会保存，地址栏的URL地址会`改变`
* 请求转发：服务器行为，`resquest.getRequestDispatcher().forward(req,resp);`
	* 是一次请求，转发后请求对象会保存，地址栏的URL地址不会改变
