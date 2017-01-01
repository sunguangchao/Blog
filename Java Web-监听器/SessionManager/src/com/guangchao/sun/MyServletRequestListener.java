package com.guangchao.sun;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import javax.servlet.ServletRequestEvent;
import javax.servlet.ServletRequestListener;
import javax.servlet.http.HttpServletRequest;

import com.guangchao.sun.entity.User;
import com.guangchao.sun.util.SessionUtil;

public class MyServletRequestListener implements ServletRequestListener {

	private ArrayList<User> userList;
	@Override
	public void requestDestroyed(ServletRequestEvent arg0) {
		// TODO Auto-generated method stub

	}

	@Override
	public void requestInitialized(ServletRequestEvent arg0) {
		// TODO Auto-generated method stub
		userList = (ArrayList<User>)arg0.getServletContext().getAttribute("userList");
		if(userList == null)
			userList = new ArrayList<User>();
	
		HttpServletRequest request = (HttpServletRequest) arg0.getServletRequest();
		String sessionIdString = request.getSession().getId();
		if(SessionUtil.getUserBySessionId(userList, sessionIdString) == null){
			User user = new User();
			user.setSessionIdString(sessionIdString);
			user.setFirstTimeString(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()));
			user.setIpString(request.getRemoteAddr());
			userList.add(user);
		}
		arg0.getServletContext().setAttribute("userList", userList);

	}

}
