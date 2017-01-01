package com.guangchao.sun.util;

import java.util.ArrayList;

import com.guangchao.sun.entity.User;

public class SessionUtil {
	public static Object getUserBySessionId(ArrayList<User> userList,String sessionIdString) {
		for(int i=0; i < userList.size(); i++){
			User user = userList.get(i);
			if(user.getSessionIdString().equals(sessionIdString)){
				return user;
			}
		}
		return null;
	}


}
