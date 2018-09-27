//Funkcja do uruchomienia po załadowaniu strony.
$(function(){
	//Podepnij funkcję wysyłająca tekst do chat-input i ustaw focus na pole tekstowe
	$('#chat-input')
	.focus()
	.keypress(function(ev){
		if  (ev.which != 13){  //Klawisz ENTER
			return;
		}
		ev.preventDefault();
		
		//Pobierz zawartość i wyczyść pole tekstowe.
		var text = $(this).val();
		$(this).val('');
		
		//Wyślij tekst do serwera.
		var text_json = JSON.stringify({
			"text": text
		});
		$.ajax({
			url: '/chat',
			type: 'POST',
			data: text_json,
			async: true
		});
	});
	
	//regularnie odpytuj serwer o nowe wiadomości.
	var last_message_id = -1;
	var chat = $('#chat-text');
	function checkForNewMessages(){
		var request_json = JSON.stringify({
			"last_message_id": last_message_id
		});
		$.ajax({
			url: '/messages',
			type: 'POST',
			data: request_json,
			dataType: 'json',
			async: true,
			error: function(jqXHR, textStatus, errorThrown){
				console.log("Failed to fech new messages: " + errorThrown);
				
				//Wywoałaj ponownie funkcję za sekundę.
				window.setTimeout(chekForNewMessages, 1000);
			},
			success: function(data) {
				last_message_id = data["last_messages_id"];
				data["messages"].forEach(function(cv,idx,arr){
					var person = cv(0);
					var text = cv(1);
					var chat_line = $('<p/>');
					$('<span/>', {
						text: '<' + person + '>'
					}).addClass("person").appendTo(chat_line);
					$('<span/>', {
						text: text
					}).addClass("text").appendTo(chat_line);
					chat_line.appendTo(chat)
				});
				
				chat.animate({scrollTop:chat[0].scrollHeight }, 500);
				
				//Wywołaj ponownie funkcję za sekundę.
				window.setTimeout(checkForNewMessages, 1000);
			},
		});
	}
	checkForNewMessages();
});
