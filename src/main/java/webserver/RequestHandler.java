package webserver;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import controller.Controller;
import controller.RequestMapping;
import db.DataBase;
import model.User;

public class RequestHandler extends Thread {
	private static final Logger log = LoggerFactory.getLogger(RequestHandler.class);
	private Socket connection;
	Controller controller;
	public RequestHandler(Socket connectionSocket) {
		this.connection = connectionSocket;
	}

	public void run() {
		log.debug("New Client Connect! Connected IP : {}, Port : {}", connection.getInetAddress(),
				connection.getPort());
		BufferedReader br = null;
		try (InputStream in = connection.getInputStream(); OutputStream out = connection.getOutputStream()) {

			HttpRequest request = new HttpRequest(in);
			HttpResponse response = new HttpResponse(out);
			String path = request.getRequestPath();
			if (path == null) {
				log.error("Client HttpRequest Error");
				return;
			}
			
			Controller controller = RequestMapping.getController(path);
			if(controller==null){
				response.forward(path);
			}else{
				controller.service(request, response);
			}
		} catch (IOException e) {
			log.error(e.getMessage());
		}
	}
}
