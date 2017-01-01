package com.guangchao.sun;

import java.util.ArrayList;

import javax.servlet.http.HttpSessionEvent;
import javax.servlet.http.HttpSessionListener;

import com.guangchao.sun.entity.User;
import com.guangchao.sun.util.SessionUtil;

public class MyHttpSessionListener implements HttpSessionListener {

	private int userNumber = 0;
	@Override
	public void sessionCreated(HttpSessionEvent arg0) {
		// TODO Auto-generated method stub
		userNumber++;
		arg0.getSession().getServletContext().setAttribute("userNumber", userNumber);
	}

	@Override
	public void sessionDestroyed(HttpSessionEvent arg0) {
		// TODO Auto-generated method stub
		userNumber--;
		arg0.getSession().getServletContext().setAttribute("userNumber", userNumber);
		ArrayList<User> userList = null;
		userList = (ArrayList<User>)arg0.getSession().getServletContext().getAttribute("userList");
		
		if(SessionUtil.getUserBySessionId(userList, arg0.getSession().getId()) != null){
			userList.remove(SessionUtil.getUserBySessionId(userList, arg0.getSession().getId()));
		}
	}

}
