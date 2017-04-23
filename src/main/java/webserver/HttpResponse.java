package webserver;

import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
/*
 * 응답을 보낼 때 HTML, CSS, JAVASCRIPT 파일을 직접 읽어 응답으로 보내는 메소드는 forward()
 * 다른 URL로 리다이레긑하는 메소드는 sendRedirect()
 */
public class HttpResponse {
	private static final Logger log = LoggerFactory.getLogger(HttpResponse.class);
	private static final String EMPTY_LINE = "\r\n";
	DataOutputStream dos;
	
	byte[] body=null;
    Map<String,String> header = new HashMap<>();
    Map<String,String> cookie= new HashMap<>();
	
	public HttpResponse(OutputStream os){
		dos = new DataOutputStream(os);
	    header = new HashMap<>();
	    cookie= new HashMap<>();
	}
	
	// Set-Cookie: aa=aa; bb=bb; cc=cc
	private String arrangeCookie(){
		StringBuilder sb = new StringBuilder();
		for(String key : cookie.keySet()){
			sb.append(key+"="+ cookie.get(key));
			sb.append("; ");
		}
		return sb.toString().substring(0, sb.length()-2);
	}
	
	public void sendRedirect(String page){
   	 try {
            dos.writeBytes("HTTP/1.1 302 Found \r\n");
            if(cookie.size()>0){
            	String cookieString = arrangeCookie();
                dos.writeBytes("Set-Cookie: "+cookieString+"\r\n");
            }
            dos.writeBytes("Location: http://localhost:8080"+page +"\r\n");
            dos.writeBytes("\r\n");
            dos.flush();
        } catch (IOException e) {
            log.error(e.getMessage());
        }
   }

	public void forward(String requestPath) {
		try {
			Path path = new File("./webapp" + requestPath).toPath();
			byte[] body = Files.readAllBytes(path);
			if(requestPath.endsWith(".css")){
				responseCssHeader(body.length);
			}else{
				response200Header(body.length);
			}
			responseBody(body);
		} catch (IOException ioe) {
			log.error(ioe.getMessage());
		}
	}
	public void forward(byte[] body) {
		try {
			response200Header(body.length);
			responseBody(body);
		} catch (IOException ioe) {
			log.error(ioe.getMessage());
		}
	}
	private void responseCssHeader(int lengthOfBodyContent) throws IOException{
			dos.writeBytes("HTTP/1.1 200 OK \r\n");
			dos.writeBytes("Content-Type: text/css;charset=utf-8\r\n");
			dos.writeBytes("Content-Length: " + lengthOfBodyContent + "\r\n");
			if(cookie.size()>0){
            	String cookieString = arrangeCookie();
                dos.writeBytes("Set-Cookie: "+cookieString+"\r\n");
            }
			dos.writeBytes("\r\n");
	}
	private void response200Header(int lengthOfBodyContent) throws IOException {
		dos.writeBytes("HTTP/1.1 200 OK \r\n");
		dos.writeBytes("Content-Type: text/html;charset=utf-8\r\n");
		dos.writeBytes("Content-Length: " + lengthOfBodyContent + "\r\n");
		if(cookie.size()>0){
        	String cookieString = arrangeCookie();
            dos.writeBytes("Set-Cookie: "+cookieString+"\r\n");
        }
		dos.writeBytes("\r\n");
	}

   private void responseBody(byte[] body) throws IOException {
           dos.write(body, 0, body.length);
           dos.flush();
   }

public Map<String, String> getHeader() {
	return header;
}

public void setHeader(Map<String, String> header) {
	this.header = header;
}

public Map<String, String> getCookie() {
	return cookie;
}
public String getCookie(String key) {
	return cookie.get(key);
}

public void setCookie(Map<String, String> cookie) {
	this.cookie = cookie;
}
public void setCookie(String key, String value) {
	if(cookie == null){
		cookie = new HashMap<>();
	}
	cookie.put(key, value);
}
   
   
}
