package http_chat;

import java.io.*;
import java.io.IOException;
import java.net.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import com.google.gson.*;

public class HttpServer {
	static int i =0;
	
	public static void main (String[] args) {
		SimpleChatWWW  website = new SimpleChatWWW();
		try (ServerSocket serverSocket = new ServerSocket(8880)){
				while(true){	
					Socket threadSocket = serverSocket.accept();
					System.out.println(i);
					i++;
					Runnable serverHandler = new HttpServerHandler(threadSocket, website);
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
	SimpleChatWWW website;
	HashMap <String, String> answer;
	String line;
	String handleAnswer;
	String resource;
	String data;
	String[] headerTokens;
	String[] headers;
	ArrayList<String> lines =new ArrayList<>();
	HashMap<String, String> request = new HashMap<>();
	char[] formData =new char[64];

	public HttpServerHandler (Socket incomingConnection, SimpleChatWWW aWebsite){
		incoming = incomingConnection;
		website = aWebsite;
	}
	
	public void run(){
		try
			(BufferedReader testServerReader = new BufferedReader(new InputStreamReader(incoming.getInputStream()));
				OutputStream outStream =incoming.getOutputStream()){
			PrintWriter out = new PrintWriter(new OutputStreamWriter(outStream, "UTF-8"));
			while(!(line = testServerReader.readLine()).contentEquals("kuniec") ){
				System.out.println(line);
				lines.add(line);
				if(line.contentEquals("")) break;
			};
			System.out.println("Rozmiar tablicy lines: " +lines.size());
			headerTokens =lines.get(0).split(" ");
			System.out.println("Nagłówek: " +Arrays.toString(headerTokens));
			if (headerTokens.length != 3){
				System.out.println("Nieprawidłowa liczba parametrów w nagłówku");
				System.exit(0);
			}
			
			request.put("method", headerTokens[0]);
			request.put("query", headerTokens[1] );
			request.put("version", headerTokens[2] );
			
			for (int z=1; z<4; z++){
				System.out.println("Tworzenie nagłówków...");
				headers =lines.get(z).split(" ");
				System.out.println(headers[0] +" " + headers[1]);
				request.put(headers[0], headers[1]);
			}

			if (headerTokens[0].equals("POST")){
				System.out.println(request);
				System.out.println("Długość dodatkowych danych: " + request.get("Content-Length:"));
				receiveAll(testServerReader, Integer.parseInt(request.get("Content-Length:")));
			}
			System.out.println(request);
			
			answer = website.handleHttpRequest(request);
//			System.out.println(answer);
			out.print("HTTP/1.1 " + answer.get("statusCode") + " "+answer.get("statusCodeDescription") + "\r\n");
			out.print("Content-Type: " +answer.get("contentType") + "\r\n");
			out.print("\r\n");
			out.print(answer.get("data"));
			out.close();
			incoming.close();
			System.out.println("Odpowiedź wysłano");
			
		}
		catch (IOException e){
			System.out.println("Błąd w metodzie run");
			System.out.println(e.getMessage());
		}
	}	
	final void receiveAll(BufferedReader aTestServerReader, int length) throws IOException{
		StringBuilder postData = new StringBuilder();
		char postDataChar;
		for(int i=0; i<length; i++){
			postDataChar = (char) aTestServerReader.read();
			postData.append(postDataChar);
		}
		request.put("data", postData.toString()) ;
	}
}
