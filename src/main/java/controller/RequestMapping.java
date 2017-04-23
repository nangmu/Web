package controller;

import java.util.HashMap;
import java.util.Map;

public class RequestMapping {
	private static Map<String, Controller> controllers = new HashMap<>();
	
	static{
		controllers.put("/user/create", new CreateUserController());
		controllers.put("/user/list", new ListUserController());
		controllers.put("/user/login", new LoginController());
	}
	
	public static Controller getController(String requestPath){
		return controllers.get(requestPath);
	}
}
