
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
			System.out.println("Błąd w metodzie main");
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
		String[] headerTokens;
		ArrayList<String> lines =new ArrayList<>();
		try{
			Scanner testServerReader = new Scanner(new InputStreamReader(incoming.getInputStream()));
			while(testServerReader.hasNextLine()){
				line = testServerReader.nextLine();
				if(line.contentEquals("")) break;
				lines.add(line);
			};
			System.out.println(lines);
			headerTokens =lines.get(0).split(" ");
			System.out.println("Nagłówek: " +Arrays.toString(headerTokens));
			System.out.println(headerTokens.length);
			if (headerTokens.length != 3){
				System.out.println("Nieprawidłowa liczba parametrów w nagłówku");
				System.exit(0);
			}
			if(headerTokens[0].equals("GET")) {
				System.out.println("Obsługa żądania typu GET");
				if (headerTokens[1].equals("/") || headerTokens[1].equals("/index.html")) handleGetIndex();
				if (headerTokens[1].equals("/style.css")) handleGetStyle();
			}
		}
		catch (IOException e){
			System.out.println("Błąd w metodzie run");
			System.out.println(e.getMessage());
		}
	}

	private Scanner handleGetIndex() {
		String test ="testujemy sobie coś";
		System.out.println("Wywołano metodę handleGetIndex");
		Scanner answer = new Scanner(test);
		return answer;
	}	
	private void handleGetStyle(){
		System.out.println("Wywołano metodę handleGetstyle");
	}
}
