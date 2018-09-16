package http_chat;

import java.io.*;
import java.io.IOException;
import java.net.*;
import java.util.*;

public class HttpServer {
	
	public static void main (String[] args) {			
		try (ServerSocket serverSocket = new ServerSocket(8080)){
				while(true){	
					Socket threadSocket = serverSocket.accept();
					Runnable serverHandler = new HttpServerHandler(threadSocket);
					Thread clientThread = new Thread(serverHandler);
					clientThread.start();
				}
		} catch(IOException e){
			System.out.println("Bła∂ w metodzie main");
			System.out.println(e.getMessage());
		}
	}
}

class HttpServerHandler implements Runnable {
	
	Socket incoming;
	String line;
	
	public HttpServerHandler (Socket incomingConnection){
		incoming = incomingConnection;
	}
	
	public void run(){
		try{
			BufferedReader testServerReader = new BufferedReader(new InputStreamReader(incoming.getInputStream())); 
			while((line =testServerReader.readLine()) != null){
				if(line.contentEquals("")) System.out.println("rn");
				else System.out.println(line);
			};
		}
		catch (IOException e){
			System.out.println("Bła∂ w metodzie run");
			System.out.println(e.getMessage());
		}		
	}	
}
