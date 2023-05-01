package core.service.transport.server;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.annotation.SubscribeMapping;
import org.springframework.stereotype.Controller;

import core.service.transport.Producer;
import core.service.transport.RabbitConfig;

@Controller
public class WebSocketController {

  @Autowired
  Producer producer;
  
  @MessageMapping("/send/users")
  public void sendToUserQueue(String message){
    producer.sendTo(RabbitConfig.TRADING_QUEUE, message);
    System.out.println("Message for Send to User queue: " + message);
  }

  @MessageMapping("/send/business")
  public void sendToBusinessQueue(String message){
    System.out.println("Message for Send to Business queue: " + message);
  }

  @MessageMapping("/send/trading")
  public void sendToTradingQueue(String message){
    System.out.println("Message for Send to Trading queue: " + message);
  }

  @MessageMapping("/send/analytics")
  public void sendToAnalyticsQueue(String message){
    System.out.println("Message for Send to Analytics queue: " + message);
  }
}
