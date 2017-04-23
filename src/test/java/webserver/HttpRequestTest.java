package webserver;

import static org.junit.Assert.*;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;

import org.junit.Test;

public class HttpRequestTest {
	private String testDirectory ="./src/test/resources/";
	
	@Test
	public void request_GET() throws Exception{
		InputStream in = new FileInputStream(new File(testDirectory +
				"Http_GET.txt"));
		HttpRequest request = new HttpRequest(in);
		
		assertEquals("GET", request.getHttpMethod());
		assertEquals("keep-alive",request.getHeader().get("Connection"));
		assertEquals("id", request.getParameter().get("userId"));
		assertEquals("SW",request.getParameter().get("name"));
		assertEquals("/user/create", request.getRequestPath());
	}
	
	@Test
	public void 임의스트링_바이트개수(){
		String s = "userId=id&password=pw&name=SW";
		int length = s.getBytes().length;
		
		//UTF-8 --> 영문자 1byte 한글 3byte. 아마?
		assertEquals(29,length);
	}
	
	@Test
	public void request_POST() throws Exception{
		InputStream in = new FileInputStream(new File(testDirectory +
				"Http_POST.txt"));
		HttpRequest request = new HttpRequest(in);
		
		assertEquals("POST",request.getHttpMethod());
		assertEquals("keep-alive",request.getHeader().get("Connection"));
		assertEquals("id", request.getParameter().get("userId"));
		assertEquals("SW",request.getParameter().get("name"));
		assertEquals("/user/create", request.getRequestPath());
		}
	
}
