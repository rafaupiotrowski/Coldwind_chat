package http_chat;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.JsonPrimitive;

public class SimpleChatWWW {
		
	String resource;
	String data;
	static int newest_message_id;
	int last_message_id;
	
	Gson gson;
	JsonParser parser = new JsonParser();
	JsonObject receivedText = new JsonObject();
	JsonObject text = new JsonObject();
	
	HashMap<String, String> resources = new HashMap<>();
	HashMap<String, String> contentType = new HashMap<>();
	ArrayList<String[]> allMessages;

	public SimpleChatWWW (){
		gson =new GsonBuilder().create();
		data ="";
		last_message_id=0;
		allMessages = new ArrayList<String[]>();
		resources.put("/", "httpChatIndex.html");
		resources.put("/index.html", "httpChatIndex.html");	
		resources.put("/style.css", "httpChatStyle.css");	
		resources.put("/main.js", "httpChatMain.js");
		contentType.put("/", "text/html; charset=utf-8");
		contentType.put("/index.html", "text/html; charset=utf-8");
		contentType.put("/style.css", "text/css; charset=utf-8");
		contentType.put("/main.js", "application/javascript");
	}
	
	public HashMap<String, String> handleHttpRequest(Map<String, String> aRequest) throws IOException{
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

	private HashMap<String, String> handlePost(Map<String, String> aRequest) throws IOException{
		System.out.println("Obsługa żądania POST");
		HashMap<String, String> response = new HashMap<>();
		if(aRequest.get("query").equals("/messages")) {
			receivedText = parser.parse(aRequest.get("data")).getAsJsonObject();
			last_message_id =receivedText.get("last_message_id").getAsInt();
			if (last_message_id <0) last_message_id =0;
			JsonObject text = new JsonObject();
			JsonPrimitive currentMessageId = new JsonPrimitive(newest_message_id);
			JsonArray messagesToSend = jsonfyMessages(last_message_id);
			text.add("last_message_id", currentMessageId);
			text.add("messages", messagesToSend);
			response.put("statusCode", "200");
			response.put("statusCodeDescription", "OK");
			response.put("data", text.toString());
			response.put("contentType", "application/json");

		} else if(aRequest.get("query").equals("/chat")){
			String[] message= new String[2];
			message[0] = aRequest.get("senderIP");
			text =parser.parse(aRequest.get("data")).getAsJsonObject();
			message[1] = text.get("text").getAsString();
			allMessages.add(message);
			response.put("statusCode", "200");
			response.put("statusCodeDescription", "OK");
			newest_message_id++;
			}
		return response;
	}
	
	private JsonArray jsonfyMessages(int last_message_id){
		JsonArray answer = new JsonArray();	
		for(int i=last_message_id; i<newest_message_id; i++){
			String element1 =allMessages.get(i)[0];
			String element2 =allMessages.get(i)[1];
			JsonArray singleArray = new JsonArray();
			singleArray.add(element1);
			singleArray.add(element2);
			answer.add(singleArray);
		}
		return answer;
	}
	
	private HashMap<String, String> handleGet(Map<String, String> aRequest) throws IOException {		
		HashMap<String, String> response = new HashMap<>();
		String aResource =aRequest.get("query");
		if(resources.containsKey(aResource)){
			resource = resources.get(aResource);
			Path pathToIndex =Paths.get(resource);
			String resourceContent =new String (Files.readAllBytes(pathToIndex));
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
