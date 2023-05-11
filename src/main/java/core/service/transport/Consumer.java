package core.service.transport;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;

import core.service.transport.server.IncommingMessage;
import core.service.transport.server.OutputMessage;

public class Consumer {
  @Autowired
  private SimpMessagingTemplate socketTemplate;

  private static final Logger logger = LogManager.getLogger(Consumer.class);

  public Consumer(){

  }

  @RabbitListener(queues = {RabbitConfig.TRADING_QUEUE})
  public void consume(String in){
    logger.info("Mensaje Consumido desde la Cola de Trading: " + in);
    IncommingMessage ret = new IncommingMessage(in, "Trading");
    socketTemplate.convertAndSend("/topic/trading", ret);
  }
}
