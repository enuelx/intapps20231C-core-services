package core.service.transport;

import org.springframework.amqp.rabbit.annotation.RabbitListener;

public class Consumer {
public Consumer(){

}

@RabbitListener(queues = {RabbitConfig.TRADING_QUEUE})
public void consume(String in){
  System.out.println("Message Consumed from Transport queue: " + in);
}  
}
