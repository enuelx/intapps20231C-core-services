package core.service.transport;

import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;

public class Producer {

  RabbitTemplate rabbitTemplate;

  @Autowired
  public Producer(RabbitTemplate rabbitTemplate){
    this.rabbitTemplate = rabbitTemplate;
  }

  public void sendTo(String queue, String message){
    rabbitTemplate.convertAndSend(RabbitConfig.CORE_EXCHANGE, queue, message);
  }
}
