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
	String httpMethod;
	String requestPath;
    String query=null;
    Map<String,String> parameter = null;
    Map<String,String> header = new HashMap<>();
    Map<String,String> cookie=null;
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
    	settingRequestLine();
    	service();
    }
    
    private void settingRequestLine() throws IOException{
    	String requestLine = br.readLine();

    	log.debug("\n*******************[REQUEST LINE]******************\n\n"+requestLine
    			+"\n\n***************************************************");
    	String[] tokens = requestLine.split(" ");
        httpMethod = tokens[0];
    	requestPath = tokens[1];
    }
    private void service() throws IOException{
    	if(httpMethod.equals("GET")){
        	get();
         }
        if(httpMethod.equals("POST")){
        	post();
        }
   	 	if(header.get("Cookie")!=null){
    		cookie = HttpRequestUtils.parseCookies(header.get("Cookie"));
   	 	}
    }
    
    private void get() throws IOException{
    	if(requestPath.contains("?")){
    	query = requestPath.substring(requestPath.indexOf("?")+1);
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
    	query = IOUtils.readData(br, Integer.parseInt(header.get("Content-Length")));
		parameter = HttpRequestUtils.parseQueryString(query);
    	}
    }

	public String getHttpMethod() {
		return httpMethod;
	}

	public void setHttpMethod(String httpMethod) {
		this.httpMethod = httpMethod;
	}

	public String getRequestPath() {
		return requestPath;
	}

	public void setRequestPath(String requestPath) {
		this.requestPath = requestPath;
	}

	public Map<String, String> getParameter() {
		return parameter;
	}

	public void setParameter(Map<String, String> queryMap) {
		this.parameter = queryMap;
	}

	public Map<String, String> getHeader() {
		return header;
	}

	public void setHeader(Map<String, String> headerMap) {
		this.header = headerMap;
	}

	public Map<String, String> getCookie() {
		return cookie;
	}

	public void setCookie(Map<String, String> cookieMap) {
		this.cookie = cookieMap;
	}
    
    
}
