package core.service.transport;

import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;

import core.service.transport.server.IncommingMessage;
import core.service.transport.server.OutputMessage;

public class Consumer {
  @Autowired
  private SimpMessagingTemplate socketTemplate;

  public Consumer(){

  }

  @RabbitListener(queues = {RabbitConfig.TRADING_QUEUE})
  public void consume(String in){
    System.out.println("Message Consumed from Transport queue: " + in);

    IncommingMessage ret = new IncommingMessage(in, "Trading");
    socketTemplate.convertAndSend("/topic/users", ret);
  }
}
