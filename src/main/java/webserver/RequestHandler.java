package webserver;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.Socket;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import db.DataBase;
import model.User;
import util.HttpRequestUtils;
import util.HttpRequestUtils.Pair;
import util.IOUtils;

public class RequestHandler extends Thread {
    private static final Logger log = LoggerFactory.getLogger(RequestHandler.class);

    private Socket connection;

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
            br = new BufferedReader(new InputStreamReader(in));

            String requestLine = br.readLine();
            log.debug("REQUEST LINE:"+requestLine);
        	String[] tokens = requestLine.split(" ");
            String httpMethod = tokens[0];
        	String requestPath = tokens[1];
       
            String query=null;
            Map<String,String> queryMap = null;
            Map<String,String> headerMap = new HashMap<>();
            Map<String,String> cookieMap=null;
            Boolean logined = null;
            String line = null;
            
            if(httpMethod.equals("GET")){
            	if(requestPath.contains("?")){
            	query = requestPath.substring(requestPath.indexOf("?")+1);
            	requestPath = requestPath.substring(0, requestPath.indexOf("?"));
            	queryMap = HttpRequestUtils.parseQueryString(query);
            	}
            	while((line=br.readLine())!=null){
            		log.debug(line);
            		if(line.equals("")) break;
            		Pair pair = HttpRequestUtils.parseHeader(line);
            		headerMap.put(pair.getKey(), pair.getValue());
            	}
             }
            if(httpMethod.equals("POST")){
            	while(!(line=br.readLine()).equals("")){
        			Pair pair = HttpRequestUtils.parseHeader(line);
        			headerMap.put(pair.getKey(), pair.getValue());
        		}
            	if(headerMap.get("Content-Length")!=null){
            	query = IOUtils.readData(br, Integer.parseInt(headerMap.get("Content-Length")));
        		queryMap = HttpRequestUtils.parseQueryString(query);
            	}
            }
            
            if(!httpMethod.equals("POST") && !httpMethod.equals("GET") ){
            	while(!(line=br.readLine()).equals("")){
                	log.debug("[[???]] "+line);	
            	}
            }
            
          //Cookie 확인
            String cookieQuery =null;
            if((cookieQuery = headerMap.get("Cookie"))!=null){
            		log.debug("***********쿠키있음!!*********");
            		log.debug("***********"+cookieQuery+"*********");
            		cookieMap = HttpRequestUtils.parseCookies(cookieQuery);
            		logined = Boolean.parseBoolean(cookieMap.get("logined"));
            }
            
            byte[] body = null;
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
            		
//            		body = Files.readAllBytes(new File("./webapp/index.html").toPath());
//            		response200Cookie(dos, body.length,"logined=true");
//            		responseBody(dos, body);
//            		log.debug("입력한 ID:"+id+"\n입력한 PASSWORD:"+password);
                
            		response302Cookie(dos, "/index.html", "logined=true");
            		return;
            }else if("/index.html".equals(requestPath)){
            	if(logined!=null && logined.equals(Boolean.TRUE)){
            		log.debug("로그인 상태입니당****************");
            	}
            }else if("/user/list.html".equals(requestPath)){
                 if(logined!=null && logined.equals(Boolean.FALSE)){
                	 log.debug("[PAGE:"+requestPath+"] "+" [LOGIN]=FALSE");
                 	response302Header(dos, "/user/login.html");
                 	return;
                 }else if(logined!=null && logined.equals(Boolean.TRUE)){
                	 log.debug("[PAGE:"+requestPath+"]"+" [LOGIN]=TRUE");
                 }
                 
            }else{
            	
            }

        	log.debug("[REQUEST PATH] " + requestPath);
        	
            Path path = new File("./webapp"+requestPath).toPath();
            body = Files.readAllBytes(path);
            
            if(requestPath.endsWith(".css")){
            	responseCss(dos, body.length);
                responseBody(dos, body);
                return;
            }
            response200Header(dos, body.length);
            responseBody(dos, body);
            
        } catch (IOException e) {
            log.error(e.getMessage());
        }
    }
    
    private void responseCss(DataOutputStream dos, int lengthOfBodyContent){
		try {
			dos.writeBytes("HTTP/1.1 200 OK \r\n");
			dos.writeBytes("Content-Type: text/css;charset=utf-8\r\n");
			dos.writeBytes("Content-Length: " + lengthOfBodyContent + "\r\n");
			dos.writeBytes("\r\n");
		} catch (IOException ioe) {
			ioe.printStackTrace();
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
    
    private void response200Header(DataOutputStream dos, int lengthOfBodyContent) {
        try {
            dos.writeBytes("HTTP/1.1 200 OK \r\n");
            dos.writeBytes("Content-Type: text/html;charset=utf-8\r\n");
            dos.writeBytes("Content-Length: " + lengthOfBodyContent + "\r\n");
            dos.writeBytes("\r\n");
        } catch (IOException e) {
            log.error(e.getMessage());
        }
    }

    private void responseBody(DataOutputStream dos, byte[] body) {
        try {
            dos.write(body, 0, body.length);
            dos.flush();
        } catch (IOException e) {
            log.error(e.getMessage());
        }
    }
}
