package webserver;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import util.HttpRequestUtils;
import util.HttpRequestUtils.Pair;
import util.IOUtils;

public class HttpRequest {

    private static final Logger log = LoggerFactory.getLogger(RequestHandler.class);
	BufferedReader br;
	private HttpMethod method;
	String requestPath;
    Map<String,String> parameter = new HashMap<>();
    Map<String,String> header = new HashMap<>();
    Map<String,String> cookie=new HashMap<>();
	String line=null;
	
    public HttpRequest(InputStream in){
		br =new BufferedReader(new InputStreamReader(in));
		try{
		go();
		}catch(IOException ioe){
			ioe.printStackTrace();
		}
	}
    
    private void go() throws IOException{
    	if(!setRequestLine()){
    		return;
    	}
    	service();
    }
    
    private boolean setRequestLine() throws IOException{
    	String requestLine = br.readLine();
    	if(requestLine ==null){
    		log.error("Http Request Header가 비어있습니다.");
    		return false;
    	}
    	if(requestLine.length()<3){
    		log.error("Http Request Line 형식이 잘못되었습니다.");
    		return false;
    	}
    	
    	String[] tokens = requestLine.split(" ");
    	
    	method = HttpMethod.valueOf(tokens[0]);
    	requestPath = tokens[1];
    	return true;
    }
    private void service() throws IOException{
    	if(method.isGET()){
        	get();
         }
        if(method.isPOST()){
        	post();
        }
   	 	if(header.get("Cookie")!=null){
    		cookie = HttpRequestUtils.parseCookies(header.get("Cookie"));
   	 	}
    }
    
    private void get() throws IOException{
    	if(requestPath.contains("?")){
    	String query = requestPath.substring(requestPath.indexOf("?")+1);
    	requestPath = requestPath.substring(0, requestPath.indexOf("?"));
    	parameter = HttpRequestUtils.parseQueryString(query);
    	}
    	while((line=br.readLine())!=null){
    		//log.debug(line);
    		if(line.equals("")) break;
    		Pair pair = HttpRequestUtils.parseHeader(line);
    		header.put(pair.getKey(), pair.getValue());
    	}
    }
    
    private void post() throws IOException{
    	while(!(line=br.readLine()).equals("")){
			Pair pair = HttpRequestUtils.parseHeader(line);
			header.put(pair.getKey(), pair.getValue());
		}
    	if(header.get("Content-Length")!=null){
    	String query = IOUtils.readData(br, Integer.parseInt(header.get("Content-Length")));
		parameter = HttpRequestUtils.parseQueryString(query);
    	}
    }

	public HttpMethod getHttpMethod() {
		return method;
	}
	public String getRequestPath() {
		return requestPath;
	}
	public String getParameter(String key) {
		return parameter.get(key);
	}
	public String getHeader(String key) {
		return header.get(key);
	}
	public String getCookie(String key) {
		return cookie.get(key);
	}
}
