package core.service.transport.clients;

import java.util.Scanner;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.messaging.converter.MappingJackson2MessageConverter;
import org.springframework.messaging.simp.stomp.StompSession;
import org.springframework.web.socket.client.WebSocketClient;
import org.springframework.web.socket.client.standard.StandardWebSocketClient;
import org.springframework.web.socket.messaging.WebSocketStompClient;

import core.service.transport.server.IncommingMessage;
import core.service.transport.server.WebSocketConstants;

public class Client {

  public static void main(String args[]) throws InterruptedException, ExecutionException{
    WebSocketClient client = new StandardWebSocketClient();
    WebSocketStompClient stompClient = new WebSocketStompClient(client);
    stompClient.setMessageConverter(new MappingJackson2MessageConverter());

    ClientSessionHandler stopmSessionHandler = new ClientSessionHandler();

    String URL = WebSocketConstants.ENDPOINTS.get("trading");
    
    CompletableFuture<StompSession> sessionAsync = stompClient.connectAsync(URL, stopmSessionHandler);
    StompSession session = sessionAsync.get();

    //session.subscribe("/topic/client", handler);

    if (!session.isConnected())
      return;

    while (true){
      session.send("/app/client", new IncommingMessage("Hello World", "UADE"));
      Thread.sleep(2000);
    }

    // ClientSessionHandler handler = new ClientSessionHandler();

    // stompClient.connect(urlString, handler, args)

    // CompletableFuture<StompSession> sessionAsync = stompClient.connectAsync(urlString, handler);
    // StompSession session = sessionAsync.get();

    // //session.subscribe("/topic/client", handler);

    // while (true){
    //   session.send("/app/client", new IncommingMessage("Hello World", "UADE"));
    //   Thread.sleep(2000);
    // }
  }
}


