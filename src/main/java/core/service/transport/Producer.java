package core.service.transport;

import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;

public class Producer {

  RabbitTemplate rabbitTemplate;

  @Autowired
  public Producer(RabbitTemplate rabbitTemplate){
    this.rabbitTemplate = rabbitTemplate;
  }

  @Scheduled(initialDelay = 5000, fixedDelay = 5000)
  public void sendMsg(){
    String msg = new String("Hello from Producer");
    System.out.println("Message to Sent from Producer: " + msg);
    rabbitTemplate.convertAndSend(RabbitConfig.TRADING_EXCHANGE, RabbitConfig.TRADING_QUEUE, msg);
  }
}
