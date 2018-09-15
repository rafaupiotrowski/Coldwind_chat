package http_chat;

import java.io.*;
import java.io.IOException;
import java.net.*;
import java.util.*;

public class HttpServer {
	
	public static void main (String[] args) {
		String line;
		try (ServerSocket serverSocket = new ServerSocket(8080);
				Socket threadSocket = serverSocket.accept()){	
				BufferedReader testServerReader = new BufferedReader(new InputStreamReader(threadSocket.getInputStream())); 
				while((line =testServerReader.readLine()) != null){
					System.out.println(line);
				};			
		} catch(IOException e){
			System.out.println(e.getMessage());
		}

	}

}
