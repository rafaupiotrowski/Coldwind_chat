package http_chat;

import java.io.*;
import java.io.IOException;
import java.net.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

public class HttpServer {
	static int i =0;
	
	public static void main (String[] args) {			
		try (ServerSocket serverSocket = new ServerSocket(8080)){
				while(true){	
					Socket threadSocket = serverSocket.accept();
					System.out.println(i);
					i++;
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
	PrintWriter answer;
	public HttpServerHandler (Socket incomingConnection){
		incoming = incomingConnection;
	}
	
	public void run(){
		String[] headerTokens;
		ArrayList<String> lines =new ArrayList<>();
		try
			(Scanner testServerReader = new Scanner(new InputStreamReader(incoming.getInputStream()));
				OutputStream outStream =incoming.getOutputStream()){
			PrintWriter out = new PrintWriter(new OutputStreamWriter(outStream, "UTF-8"));
			while(testServerReader.hasNextLine()){
				line = testServerReader.nextLine();
				if(line.contentEquals("")) break;
				lines.add(line);
			};
//			System.out.println(lines);
			headerTokens =lines.get(0).split(" ");
			System.out.println("Nagłówek: " +Arrays.toString(headerTokens));
			if (headerTokens.length != 3){
				System.out.println("Nieprawidłowa liczba parametrów w nagłówku");
				System.exit(0);
			}
			if(headerTokens[0].equals("GET")) {
				System.out.println("Obsługa żądania typu GET");
				if (headerTokens[1].equals("/") || headerTokens[1].equals("/index.html")){
					String indexHtml =handleGetIndex();
					out.print("HTTP/1.1 200 OK \r\n");
					out.print("Content-Type: text/html; charset=utf-8\r\n");
					out.print("\r\n");
					out.print(indexHtml);
					out.flush();
					out.close();
					incoming.close();
				}
				if (headerTokens[1].equals("/style.css")) handleGetStyle();
			}
			
		}
		catch (IOException e){
			System.out.println("Błąd w metodzie run");
			System.out.println(e.getMessage());
		}
	}

	private String handleGetIndex() throws IOException {
		System.out.println("Wywołano metodę handleGetIndex");
		Path pathToIndex =Paths.get("httpChatIndex.html");
		String htmlContent =new String (Files.readAllBytes(pathToIndex));
	//	System.out.println(htmlContent);
		return htmlContent;
	}	
	private void handleGetStyle(){
		System.out.println("Wywołano metodę handleGetstyle");
	}
}
