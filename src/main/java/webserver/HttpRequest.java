package webserver;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;

import util.HttpRequestUtils;
import util.HttpRequestUtils.Pair;
import util.IOUtils;

public class HttpRequest {

	BufferedReader br;
	String httpMethod;
	String requestPath;
    String query=null;
    Map<String,String> queryMap = null;
    Map<String,String> headerMap = new HashMap<>();
    Map<String,String> cookieMap=null;
	String line=null;
	
    public HttpRequest(InputStream in){
		br =new BufferedReader(new InputStreamReader(in));
	}
    
    public void go() throws IOException{
    	settingRequestLine();
    	service();
    }
    
    private void settingRequestLine() throws IOException{
    	String requestLine = br.readLine();
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
   	 	if(headerMap.get("Cookie")!=null){
    		cookieMap = HttpRequestUtils.parseCookies(headerMap.get("Cookie"));
   	 	}
    }
    
    private void get() throws IOException{
    	if(requestPath.contains("?")){
    	query = requestPath.substring(requestPath.indexOf("?")+1);
    	requestPath = requestPath.substring(0, requestPath.indexOf("?"));
    	queryMap = HttpRequestUtils.parseQueryString(query);
    	}
    	while((line=br.readLine())!=null){
    		//log.debug(line);
    		if(line.equals("")) break;
    		Pair pair = HttpRequestUtils.parseHeader(line);
    		headerMap.put(pair.getKey(), pair.getValue());
    	}
    }
    
    private void post() throws IOException{
    	while(!(line=br.readLine()).equals("")){
			Pair pair = HttpRequestUtils.parseHeader(line);
			headerMap.put(pair.getKey(), pair.getValue());
		}
    	if(headerMap.get("Content-Length")!=null){
    	query = IOUtils.readData(br, Integer.parseInt(headerMap.get("Content-Length")));
		queryMap = HttpRequestUtils.parseQueryString(query);
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

	public Map<String, String> getQueryMap() {
		return queryMap;
	}

	public void setQueryMap(Map<String, String> queryMap) {
		this.queryMap = queryMap;
	}

	public Map<String, String> getHeaderMap() {
		return headerMap;
	}

	public void setHeaderMap(Map<String, String> headerMap) {
		this.headerMap = headerMap;
	}

	public Map<String, String> getCookieMap() {
		return cookieMap;
	}

	public void setCookieMap(Map<String, String> cookieMap) {
		this.cookieMap = cookieMap;
	}
    
    
}
