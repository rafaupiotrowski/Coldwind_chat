package http_chat;

import java.io.IOException;
import java.net.Socket;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

public class SimpleChatWWW {
		
	String line;
	String handleAnswer;
	String statusCode;
	String resource;
	String data;
	int last_message_id;
	Gson gson;
	HashMap <String, String> statusCodeDescription = new HashMap<>();
	HashMap<String, String> resources = new HashMap<>();
	HashMap<String, String> contentType = new HashMap<>();

	public SimpleChatWWW (){
		data ="";
		last_message_id=0;
		gson =new GsonBuilder().create();
		statusCodeDescription.put("200", "OK!");
		resources.put("/", "httpChatIndex.html");
		resources.put("/index.html", "httpChatIndex.html");	
		resources.put("/style.css", "httpChatStyle.css");	
		resources.put("/main.js", "httpChatMain.js");
		contentType.put("/", "text/html; charset=utf-8");
		contentType.put("/index.html", "text/html; charset=utf-8");
		contentType.put("/style.css", "text/css; charset=utf-8");
		contentType.put("/main.js", "application/javascript");
	}
	
	public String handleHttpRequest(HashMap<String, String> aRequest) throws IOException{
		System.out.println("Wywołano metodę: handleHTTPRequest");
		if(aRequest.get("method").equals("GET")){
			return handleGet(aRequest);
		}
		else if(aRequest.get("method").equals("POST")){
			return handlePost(aRequest);
		}
		else return "Błąd";
	}

	private String handlePost(HashMap<String, String> aRequest) throws IOException{
		System.out.println("Obsługa żądania POST");
		return "test";
	}
	private String handleGet(HashMap<String, String> aRequest) throws IOException {
		String aResource =aRequest.get("query");
		if(resources.containsKey(aResource)){
			resource = resources.get(aResource);
			Path pathToIndex =Paths.get(resource);
			String htmlContent =new String (Files.readAllBytes(pathToIndex));
			statusCode = "200";
//			System.out.println(htmlContent);
			return htmlContent;
		} else{
			System.out.println("Nieprawidłowe żądanie zasobu: " + aResource);
			statusCode = "404";
			return "";
		}		
	}
}
