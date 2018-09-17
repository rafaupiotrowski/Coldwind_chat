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
		Scanner httpRequest;
		int i =0;
		try{
			Scanner testServerReader = new Scanner(new InputStreamReader(incoming.getInputStream())); 
			while(testServerReader.hasNextLine()){
				line = testServerReader.nextLine();
				String [] lineTokens = line.split(" ");
				if(lineTokens[0].equals("GET")){ handleGet(lineTokens);}
				if(line.contentEquals("")) System.out.println("rn");
				else System.out.println(line);
			};
		}
		catch (IOException e){
			System.out.println("Bła∂ w metodzie run");
			System.out.println(e.getMessage());
		}
	}

	private void handleGet(String[] lineTokens) {
		System.out.println("Wywołano metodę handleGet");
		System.out.println(Arrays.toString(lineTokens));
		
	}	
}
