package http_chat;

import java.io.IOException;
import java.net.Socket;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

public class SimpleChatWWW {
		
	String line;
	String handleAnswer;
	String statusCode;
	String resource;
	String data;
	int newest_message_id;
	int last_message_id;
	Gson gson;
	JsonParser parser = new JsonParser();
	JsonObject last_message_id_json = new JsonObject();
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
	
	public HashMap<String, String> handleHttpRequest(HashMap<String, String> aRequest) throws IOException{
		System.out.println("Wywołano metodę: handleHTTPRequest");
		HashMap<String, String> response = new HashMap<>();
		if(aRequest.get("method").equals("GET")){
			return handleGet(aRequest);
		}
		else if(aRequest.get("method").equals("POST")){
			return handlePost(aRequest);
		}
		else{
			response.put("statusCode", "400");
			response.put("statusCodeDescription", "Bad Request");
			return response;
		}
	}

	private HashMap<String, String> handlePost(HashMap<String, String> aRequest) throws IOException{
		System.out.println("Obsługa żądania POST");
		if(aRequest.get("query").equals("/messages")) {
			last_message_id_json = parser.parse(aRequest.get("data")).getAsJsonObject();
			last_message_id =last_message_id_json.get("last_message_id").getAsInt();
			System.out.println("Odczytano last_message_id: " + last_message_id);
		}
		HashMap<String, String> response = new HashMap<>();
		return response;
	}
	
	private HashMap<String, String> handleGet(HashMap<String, String> aRequest) throws IOException {		
		HashMap<String, String> response = new HashMap<>();
		String aResource =aRequest.get("query");
		if(resources.containsKey(aResource)){
			resource = resources.get(aResource);
			Path pathToIndex =Paths.get(resource);
			String resourceContent =new String (Files.readAllBytes(pathToIndex));
			statusCode = "200";
			response.put("statusCode", "200");
			response.put("statusCodeDescription", "OK");
			response.put("data", resourceContent );
			response.put("contentType", contentType.get(aResource));
			return response;
		} else{
			System.out.println("Nieprawidłowe żądanie zasobu: " + aResource);
			response.put("statusCode", "400");
			response.put("statusCodeDescription", "Bad Request");
			return response;
		}		
	}
}
