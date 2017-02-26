<%@ page language="java" import="java.util.*" contentType="text/html; charset=utf-8"
    pageEncoding="utf-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=utf-8">
<title>Insert title here</title>
</head>
<body>
	当前在线用户人数: ${userNumber}<br>
	<%
	ArrayList<com.guangchao.sun.entity.User> userList = (ArrayList<com.guangchao.sun.entity.User>)request.getServletContext().getAttribute("userList");
	if(userList != null){
		for(int i = 0; i < userList.size(); i++){
			com.guangchao.sun.entity.User user = userList.get(i);
		
	
	
	%>
	IP:<%=user.getIpString() %>,FistTime<%=user.getFirstTimeString() %>,SessionId:<%=user.getSessionIdString()%><br>
	<%}} %>
</body>
</html>