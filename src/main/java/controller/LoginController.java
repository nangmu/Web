package controller;

import db.DataBase;
import model.User;
import webserver.HttpRequest;
import webserver.HttpResponse;

public class LoginController extends AbstractController{

	protected void doGet(HttpRequest request, HttpResponse response) {
		doPost(request,response);
	}

	protected void doPost(HttpRequest request, HttpResponse response) {
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
