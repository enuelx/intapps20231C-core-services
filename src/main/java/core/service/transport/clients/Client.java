package core.service.transport.clients;

import java.util.Scanner;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import org.springframework.messaging.converter.MappingJackson2MessageConverter;
import org.springframework.messaging.simp.stomp.StompSession;
import org.springframework.web.socket.client.WebSocketClient;
import org.springframework.web.socket.client.standard.StandardWebSocketClient;
import org.springframework.web.socket.messaging.WebSocketStompClient;

import core.service.transport.server.WebSocketConstants;

public class Client {

  public static void main(String args[]) throws InterruptedException, ExecutionException, TimeoutException{

    WebSocketClient client = new StandardWebSocketClient();
    WebSocketStompClient stompClient = new WebSocketStompClient(client);
    stompClient.setMessageConverter(new MappingJackson2MessageConverter());

    ClientSessionHandler stopmSessionHandler = new ClientSessionHandler();

    String URL = WebSocketConstants.ENDPOINTS.get("users");
    
    CompletableFuture<StompSession> sessionAsync = stompClient.connectAsync(URL, stopmSessionHandler);
    StompSession session = sessionAsync.get(1, TimeUnit.SECONDS);

    if (!session.isConnected())
      return;

    Thread.sleep(2000);
    session.subscribe(WebSocketConstants.PREFIX_TOPIC + "/trading", stopmSessionHandler);

    String message = "";

    try (Scanner reader = new Scanner(System.in)) {
      while(!message.equals("exit")){
        System.out.println("Escriba el mensaje a enviar hacia el Servidor de WebSocket, escriba exit para finalizar");
        message = reader.nextLine();
        session.send(WebSocketConstants.PREFIX_APP + "/send/trading", message);
      }
      reader.close();
    }
  }
}


