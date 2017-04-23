package controller;

import db.DataBase;
import model.User;
import webserver.HttpRequest;
import webserver.HttpResponse;

public class ListUserController implements Controller{

	@Override
	public void service(HttpRequest request, HttpResponse response) {
		Boolean logined = false;
		
		if (request.getCookie("logined") != null) {
			logined = Boolean.parseBoolean(request.getCookie("logined"));
		}
		if (!logined) {
			response.sendRedirect("/user/login.html");
			return;
		}

		StringBuilder sb = new StringBuilder();
		sb.append("<table border='1'>");
		for (User user : DataBase.findAll()) {
			sb.append("<tr>");
			sb.append("<td>" + user.getUserId() + "</td>");
			sb.append("<td>" + user.getName() + "</td>");
			sb.append("<td>" + user.getEmail() + "</td>");
		}
		byte[] body = sb.toString().getBytes();
		response.forward(body);
	}

}
