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
	
    Map<String,String> header = new HashMap<>();
    Map<String,String> cookie= new HashMap<>();
	
	public HttpResponse(OutputStream os){
		dos = new DataOutputStream(os);
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
        	processHeaders();
            dos.writeBytes("Location: http://localhost:8080"+page +"\r\n");
            dos.writeBytes("\r\n");
            dos.flush();
        } catch (IOException e) {
            log.error(e.getMessage());
        }
   }

	public void forward(String url) {
		try {
			Path path = new File("./webapp" + url).toPath();
			byte[] body = Files.readAllBytes(path);
			if(url.endsWith(".css")){
				header.put("Contente-Type", "text/css");
			}else if(url.endsWith(".js")){
				header.put("Content-Type", "application/javascript");
			}else{
				header.put("Content-Type", "text/html;charset=utf-8");
			}
			header.put("Content-Length", body.length+"");
			response200Header();
			responseBody(body);
		} catch (IOException ioe) {
			log.error(ioe.getMessage());
		}
	}
	public void forward(byte[] body) {
		try {
			header.put("Content-Length", body.length+"");
			response200Header();
			responseBody(body);
		} catch (IOException ioe) {
			log.error(ioe.getMessage());
		}
	}
	private void response200Header() throws IOException {
		dos.writeBytes("HTTP/1.1 200 OK \r\n");
		processHeaders();
		dos.writeBytes("\r\n");
	}

	private void responseBody(byte[] body) throws IOException {
		dos.write(body, 0, body.length);
		dos.writeBytes("\r\n");
		dos.flush();
	}

	public Map<String, String> getHeader() {
		return header;
	}
	public String getHeader(String key) {
		return header.get(key);
	}
	public void setHeader(String key, String value) {
		header.put(key, value);
	}
	public Map<String, String> getCookie() {
		return cookie;
	}
	public String getCookie(String key) {
		return cookie.get(key);
	}
	public void setCookie(String key, String value) {
		cookie.put(key, value);
	}
	private void processHeaders() throws IOException {
		if (!cookie.isEmpty()) {
			String cookieValue = arrangeCookie();
			header.put("Set-Cookie", cookieValue);
		}
		for (String key : header.keySet()) {
			dos.writeBytes(key + ": " + header.get(key) + EMPTY_LINE);
		}
	}
}
