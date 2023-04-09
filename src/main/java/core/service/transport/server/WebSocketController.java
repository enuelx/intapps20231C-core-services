package core.service.transport.server;

import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.stereotype.Controller;

@Controller
public class WebSocketController {
  
  @MessageMapping("/client")
  public void hello(IncommingMessage message){
    System.out.println("Message Received from Producer: " + message.getContent());
  }
}
