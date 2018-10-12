package http_chat;

import java.io.IOException;
import java.net.Socket;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.JsonPrimitive;

public class SimpleChatWWW {
		
	String line;
	String handleAnswer;
	String resource;
	String data;
	int newest_message_id;
	int last_message_id;
	Gson gson;
	JsonParser parser = new JsonParser();
	JsonObject last_message_id_json = new JsonObject();
	JsonObject text = new JsonObject();
	
	HashMap <String, String> statusCodeDescription = new HashMap<>();
	HashMap<String, String> resources = new HashMap<>();
	HashMap<String, String> contentType = new HashMap<>();
	ArrayList<String[]> allMessages;
	String messagesToSendJson;

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
		HashMap<String, String> response = new HashMap<>();
		if(aRequest.get("query").equals("/messages")) {
			last_message_id_json = parser.parse(aRequest.get("data")).getAsJsonObject();
			last_message_id =last_message_id_json.get("last_message_id").getAsInt();
			if (last_message_id <0) last_message_id =0;
			System.out.println("Odczytano last_messages_id: " + last_message_id);
			ArrayList<String[]> messagesToSend =new ArrayList<>(allMessages.subList(last_message_id, allMessages.size()));
			messagesToSendJson = gson.toJson(messagesToSend);
			JsonObject test = new JsonObject();
			JsonPrimitive currentMessageId = new JsonPrimitive(allMessages.size()); //allMessages.size();
			JsonArray messagesToSendTest =new JsonArray();
			for(int i=last_message_id; i<allMessages.size(); i++){
				String element1 =allMessages.get(i)[0];
				String element2 =allMessages.get(i)[1];
				JsonArray singleArray = new JsonArray();
				singleArray.add(element1);
				singleArray.add(element2);
				messagesToSendTest.add(singleArray);
			}
			System.out.println(messagesToSendTest.toString());
			test.add("last_message_id", currentMessageId);
			test.add("messages", messagesToSendTest);
			response.put("statusCode", "200");
			response.put("statusCodeDescription", "OK");
			response.put("data", test.toString());
			response.put("contentType", "application/json");
			System.out.println("Wysłano" + response);

		} else if(aRequest.get("query").equals("/chat")){
			String[] message= new String[2];
			message[0] = aRequest.get("senderIP");
			text =parser.parse(aRequest.get("data")).getAsJsonObject();
			message[1] = text.get("text").getAsString();
			allMessages.add(message);
			response.put("statusCode", "200");
			response.put("statusCodeDescription", "OK");
			System.out.println(Arrays.toString(message));
			for(String[] test: allMessages){
			System.out.println("Wszystkie wiadomości: " +Arrays.toString(test));
			}
			}
		return response;
	}
	
	private HashMap<String, String> handleGet(HashMap<String, String> aRequest) throws IOException {		
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
