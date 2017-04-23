package webserver;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collection;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import db.DataBase;
import model.User;

public class RequestHandler extends Thread {
    private static final Logger log = LoggerFactory.getLogger(RequestHandler.class);

    private Socket connection;
    private HttpRequest request;
    private HttpResponse response;
    
    public RequestHandler(Socket connectionSocket) {
        this.connection = connectionSocket;
    }

    public void run() {
        log.debug("New Client Connect! Connected IP : {}, Port : {}", connection.getInetAddress(),
                connection.getPort());
        BufferedReader br = null;
        try (InputStream in = connection.getInputStream(); OutputStream out = connection.getOutputStream()) {
            // TODO 사용자 요청에 대한 처리는 이 곳에 구현하면 된다.
          
            request= new HttpRequest(in);
            response = new HttpResponse(out);
            String requestPath = request.getRequestPath();
			Map<String, String> parameter = request.getParameter();
            Map<String,String> header = request.getHeader();
            Map<String,String> cookie= request.getCookie();
           
            String line = null;
            byte[] body = null;
            Boolean logined = false;
          //Cookie 확인
            if(cookie!=null && cookie.get("logined")!=null){
            	logined = Boolean.parseBoolean(cookie.get("logined"));
            }            
            if(requestPath ==null){
            	log.error("Client HttpRequest Error");
            	return;
            }else if("/user/create".equals(requestPath)){
                User user = new User(parameter.get("userId"),parameter.get("password"),
                			parameter.get("name"),parameter.get("email"));
                DataBase.addUser(user);
               	response.sendRedirect("/index.html");
            	return;
            }else if("/user/login".equals(requestPath)){
            		String id = parameter.get("userId");
            		String password = parameter.get("password");
            		
            		User user = DataBase.findUserById(id);
            		if(user==null || "".equals(user) || !password.equals(user.getPassword())){
            			response.sendRedirect("/user/login_failed.html");
            			return;
            		} 
            		response.setCookie("logined","true");
            		response.sendRedirect("/index.html");
            		return;
            }else if("/index.html".equals(requestPath)){
            	
            }else if("/user/list.html".equals(requestPath)){
                 if(!logined){
                	 response.sendRedirect("/user/login.html");
                 	return;
                 }else{
                	 StringBuilder sb = new StringBuilder();
                	 for(User user :DataBase.findAll()){
                		 sb.append("ID:"+user.getUserId()+"PW:"+user.getPassword()+"\r\n");
                	 }
                	 body = sb.toString().getBytes();
                	 response.forward(body);
                     return;
                 }
            }else{
            	
			}
            response.forward(requestPath);
		} catch (IOException e) {
			log.error(e.getMessage());
		}
    }
}
