package controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import db.DataBase;
import model.User;
import webserver.HttpRequest;
import webserver.HttpResponse;

public class CreateUserController extends AbstractController {
	private static final Logger log = LoggerFactory.getLogger(CreateUserController.class);
	
	protected void doGet(HttpRequest request, HttpResponse response) {
		doPost(request, response);
	}

	protected void doPost(HttpRequest request, HttpResponse response) {
		User user = new User(request.getParameter("userId"), request.getParameter("password"),
				request.getParameter("name"), request.getParameter("email"));
		DataBase.addUser(user);
		response.sendRedirect("/index.html");
	}
	
}
