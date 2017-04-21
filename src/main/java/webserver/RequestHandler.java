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
    
    public RequestHandler(Socket connectionSocket) {
        this.connection = connectionSocket;
    }

    private String getRequestResource(String requestLine){
    	if(requestLine==null) return null;
    	String[] tokens = requestLine.split(" ");
    	return tokens[1];
    	
    }
    public void run() {
        log.debug("New Client Connect! Connected IP : {}, Port : {}", connection.getInetAddress(),
                connection.getPort());
        BufferedReader br = null;
        try (InputStream in = connection.getInputStream(); OutputStream out = connection.getOutputStream()) {
            // TODO 사용자 요청에 대한 처리는 이 곳에 구현하면 된다.
            DataOutputStream dos = new DataOutputStream(out);
           
            request= new HttpRequest(in);
            request.go();

            String requestPath = request.getRequestPath();
            Map<String,String> queryMap = request.getQueryMap();
            Map<String,String> headerMap = request.getHeaderMap();
            Map<String,String> cookieMap= request.getCookieMap();
           
            String line = null;
            byte[] body = null;
            
          //Cookie 확인
            Boolean logined = Boolean.parseBoolean(cookieMap.get("logined"));
            if(requestPath ==null){
            	log.error("Client HttpRequest Error");
            	return;
            }else if("/user/create".equals(requestPath)){
                User user = new User(queryMap.get("userId"),queryMap.get("password"),
                			queryMap.get("name"),queryMap.get("email"));
                DataBase.addUser(user);
               	response302Header(dos,"/index.html");
            	return;
            }else if("/user/login".equals(requestPath)){
            		String id = queryMap.get("userId");
            		String password = queryMap.get("password");
            		
            		User user = DataBase.findUserById(id);
            		if(user==null || "".equals(user) || !password.equals(user.getPassword())){
            			response302Header(dos, "/user/login_failed.html");
            			return;
            		} 
            		response302Cookie(dos, "/index.html", "logined=true");
            		return;
            }else if("/index.html".equals(requestPath)){
            	
            }else if("/user/list.html".equals(requestPath)){
                 if(!logined){
                 	response302Header(dos, "/user/login.html");
                 	return;
                 }else{
                	 StringBuilder sb = new StringBuilder();
                	 for(User user :DataBase.findAll()){
                		 sb.append("ID:"+user.getUserId()+"PW:"+user.getPassword()+"\r\n");
                	 }
                	 body = sb.toString().getBytes();
                     response200Header(dos, body.length);
                     responseBody(dos, body);
                     return;
                 }
            }else{
            	
            }
            responseResource(dos,requestPath);
        } catch (IOException e) {
            log.error(e.getMessage());
        }
    }
    
  
    private void response302Header(DataOutputStream dos,String page){
    	 try {
             dos.writeBytes("HTTP/1.1 302 Found \r\n");
             dos.writeBytes("Location: http://localhost:8080"+page +"\r\n");
             dos.writeBytes("\r\n");
             dos.flush();
         } catch (IOException e) {
             log.error(e.getMessage());
         }
    }
    private void response302Cookie(DataOutputStream dos,String page, String cookieQuery){
   	 try {
            dos.writeBytes("HTTP/1.1 302 Found \r\n");
            dos.writeBytes("Set-Cookie: "+cookieQuery+"\r\n");
            dos.writeBytes("Location: http://localhost:8080"+page +"\r\n");
            dos.writeBytes("\r\n");
            dos.flush();
        } catch (IOException e) {
            log.error(e.getMessage());
        }
   }
    private void response200Cookie(DataOutputStream dos,int lengthOfBodyContent, String cookieQuery){
    	try{
    		dos.writeBytes("HTTP/1.1 200 OK \r\n");
            dos.writeBytes("Content-Type: text/html;charset=utf-8\r\n");
            dos.writeBytes("Content-Length: " + lengthOfBodyContent + "\r\n");
            dos.writeBytes("Set-Cookie: "+cookieQuery+"\r\n");
            dos.writeBytes("\r\n");
    	}catch(IOException e){
    		
    	}
    }

	private void responseResource(DataOutputStream dos, String requestPath) {
		try {
			Path path = new File("./webapp" + requestPath).toPath();
			byte[] body = Files.readAllBytes(path);
			if(requestPath.endsWith(".css")){
				responseCssHeader(dos,body.length);
			}else{
				response200Header(dos, body.length);
			}
			responseBody(dos, body);
		} catch (IOException ioe) {
			log.error(ioe.getMessage());
		}
	}
	private void responseCssHeader(DataOutputStream dos, int lengthOfBodyContent) throws IOException{
			dos.writeBytes("HTTP/1.1 200 OK \r\n");
			dos.writeBytes("Content-Type: text/css;charset=utf-8\r\n");
			dos.writeBytes("Content-Length: " + lengthOfBodyContent + "\r\n");
			dos.writeBytes("\r\n");
	}
	private void response200Header(DataOutputStream dos, int lengthOfBodyContent) throws IOException {
		dos.writeBytes("HTTP/1.1 200 OK \r\n");
		dos.writeBytes("Content-Type: text/html;charset=utf-8\r\n");
		dos.writeBytes("Content-Length: " + lengthOfBodyContent + "\r\n");
		dos.writeBytes("\r\n");
	}

    private void responseBody(DataOutputStream dos, byte[] body) throws IOException {
            dos.write(body, 0, body.length);
            dos.flush();
    }
}
