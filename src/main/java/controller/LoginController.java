package controller;

import db.DataBase;
import model.User;
import webserver.HttpRequest;
import webserver.HttpResponse;

public class LoginController implements Controller{

	@Override
	public void service(HttpRequest request, HttpResponse response) {
		// TODO Auto-generated method stub
		String id = request.getParameter("userId");
		String password = request.getParameter("password");

		User user = DataBase.findUserById(id);
		if (user == null || "".equals(user) || !password.equals(user.getPassword())) {
			response.sendRedirect("/user/login_failed.html");
			return;
		}
		response.setCookie("logined", "true");
		response.sendRedirect("/index.html");
	}

}
