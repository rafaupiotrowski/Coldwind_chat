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
	String handleAnswer;
	String statusCode;
	String resource;
	HashMap <String, String> statusCodeDescription = new HashMap<>();
	HashMap<String, String> resources = new HashMap<>();

	public HttpServerHandler (Socket incomingConnection){
		incoming = incomingConnection;
		statusCodeDescription.put("200", "OK!");
		resources.put("/", "httpChatIndex.html");
		resources.put("/index.html", "httpChatIndex.html");	
		resources.put("/style.css", "httpChatStyle.css");	
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
			headerTokens =lines.get(0).split(" ");
			System.out.println("Nagłówek: " +Arrays.toString(headerTokens));
			if (headerTokens.length != 3){
				System.out.println("Nieprawidłowa liczba parametrów w nagłówku");
				System.exit(0);
			}
			if(headerTokens[0].equals("GET")) {
				System.out.println("Obsługa żądania typu GET");
				handleAnswer =handleGet(headerTokens[1]);
			}
			out.print("HTTP/1.1 " + statusCode + " " + statusCodeDescription.get(statusCode) + "\r\n");
			out.print("Content-Type: text/html; charset=utf-8\r\n");
			out.print("\r\n");
			out.print(handleAnswer);
			out.close();
			incoming.close();
			System.out.println("Odpowiedź wysłano, kod odpowiedzi: " + statusCode);
			
		}
		catch (IOException e){
			System.out.println("Błąd w metodzie run");
			System.out.println(e.getMessage());
		}
	}

	private String handleGet(String aResource) throws IOException {
		System.out.println("Wywołano metodę handleGet");
		if(resources.containsKey(aResource)){
			resource = resources.get(aResource);
			Path pathToIndex =Paths.get(resource);
			String htmlContent =new String (Files.readAllBytes(pathToIndex));
			statusCode = "200";
			return htmlContent;
		} else{
			System.out.println("Nieprawidłowe żądanie zasobu: " + aResource);
			statusCode = "404";
			return "";
		}		
	}	
}
