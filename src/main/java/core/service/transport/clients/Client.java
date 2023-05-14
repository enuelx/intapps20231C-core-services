package core.service.transport.clients;

import java.io.Console;
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
    String message = "";

    String options[] = new String[]{
      "trading",
      "business",
      "analytics",
      "users",
    };

    Boolean selected[] = new Boolean[]{
      false, false, false, false
    };

    Console console = System.console();

    while (!message.equals("continue")) {
      System.out.println("Escoja cual de los siguientes canales quiere escuchar: escriba continue para salir");
      System.out.println("1 - Trading");
      System.out.println("2 - Business");
      System.out.println("3 - Analytics");
      System.out.println("4 - Users");
      message = console.readLine("");

      if (message.equals("continue"))
        break;

      try {
        int option = Integer.parseInt(message);

        if (option >= 1 && option <= 4) {
          selected[option - 1] = true;
          session.subscribe(WebSocketConstants.PREFIX_TOPIC + "/" + options[option - 1], stopmSessionHandler);
        }
      } catch (Exception e) {
        System.out.println("Opcion Invalida");
        continue;
      }
    }

    for (int i = 0; i < selected.length; i++) {
      if (selected[i])
        System.out.println("Subscrito a : " + options[i]);
    }

    message = "";

    while(!message.equals("exit")){
      System.out.println(message);
      System.out.println("Escriba el mensaje a enviar hacia el Servidor de WebSocket, escriba exit para finalizar");
      message = console.readLine();
      sendToSubscribed(selected, options, message, session);
    }
  }

  public static void sendToSubscribed(Boolean[] selected, String[] destinations, String message, StompSession session){
    for (int i = 0; i < destinations.length; i++) {
        if (selected[i])
          session.send(WebSocketConstants.PREFIX_APP + "/send/" + destinations[i], message);
    }
  }
}


