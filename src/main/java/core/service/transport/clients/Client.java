package core.service.transport.clients;

import java.lang.reflect.Type;
import java.util.Scanner;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.logging.Handler;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.lang.Nullable;
import org.springframework.messaging.converter.MappingJackson2MessageConverter;
import org.springframework.messaging.simp.stomp.StompFrameHandler;
import org.springframework.messaging.simp.stomp.StompHeaders;
import org.springframework.messaging.simp.stomp.StompSession;
import org.springframework.messaging.simp.stomp.StompSession.Subscription;
import org.springframework.web.socket.client.WebSocketClient;
import org.springframework.web.socket.client.standard.StandardWebSocketClient;
import org.springframework.web.socket.messaging.WebSocketStompClient;

import core.service.transport.server.IncommingMessage;
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

    session.subscribe(WebSocketConstants.PREFIX_TOPIC + "/users", stopmSessionHandler);

    while (true){
      session.send(WebSocketConstants.PREFIX_APP + "/send/users", new String("Hello World Message"));
      // session.send(WebSocketConstants.PREFIX_APP + "/send/trading", new IncommingMessage("Hola Soy", "Martin"));
      // session.send(WebSocketConstants.PREFIX_APP + "/send/business", new IncommingMessage("Hola Soy", "Martin"));
      // session.send(WebSocketConstants.PREFIX_APP + "/send/analytics", new IncommingMessage("Hola Soy", "Martin"));
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


