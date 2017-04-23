package webserver;

import java.io.FileOutputStream;
import java.util.HashMap;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;

public class HttpResponseTest {
	
	@Before
	public void init(){
		
	}
	
	@Test
	public void forwardTest1_내용() throws Exception{
		FileOutputStream fos = new FileOutputStream("./farward1.txt");
		HttpResponse response = new HttpResponse(fos);
		
		response.setCookie("logined", "true");
		
		byte[] body = "Contents.........".getBytes();
		response.forward(body);
	}
	
	@Test
	public void forwardTest2_페이지() throws Exception{
		FileOutputStream fos = new FileOutputStream("./forward2.txt");
		HttpResponse response = new HttpResponse(fos);
		
		response.setCookie("aa","bb");
		response.setCookie("aaa","bbb");
		response.setCookie("aaaa","bbbb");
		String page = "/js/scripts.js";
		
		response.forward(page);
	}
	
	
	@Test
	public void redirectTest() throws Exception{
		FileOutputStream fos = new FileOutputStream("./redirect1.txt");
		HttpResponse response = new HttpResponse(fos);
		
		response.setCookie("aa","bb");
		String page = "/gogo.html";
		
		response.sendRedirect(page);
	}
	
	
}
