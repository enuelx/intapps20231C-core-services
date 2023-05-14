package core.service.transport;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessagingTemplate;

import core.service.transport.server.IncommingMessage;

public class Consumer {
  @Autowired
  private SimpMessagingTemplate socketTemplate;

  private static final Logger logger = LogManager.getLogger(Consumer.class);

  public Consumer(){

  }

  @RabbitListener(queues = {RabbitConfig.ANALYTICS_QUEUE, RabbitConfig.BUSINESS_QUEUE, RabbitConfig.TRADING_QUEUE, RabbitConfig.USERS_QUEUE})
  public void genericConsume(Message message, @Payload String payload){
    String queueName = message.getMessageProperties().getConsumerQueue();
    logger.info("Mensaje: " + payload + " --- Consumido desde la Cola: " + queueName);

    String[] topics = queueName.split("-");
    String destination = "/topic/" + topics[0];
    logger.info("Destination: " + destination);
    
    IncommingMessage retMessage = new IncommingMessage(payload, topics[0]);
    socketTemplate.convertAndSend(destination, retMessage);
  }
}
