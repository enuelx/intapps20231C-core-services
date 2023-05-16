package core.service.transport.server;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.annotation.SubscribeMapping;
import org.springframework.stereotype.Controller;

import core.service.transport.Producer;
import core.service.transport.RabbitConfig;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Controller
public class WebSocketController {

  @Autowired
  Producer producer;
  
  private static final Logger logger = LogManager.getLogger(WebSocketController.class);


  @MessageMapping("/send/users")
  public void sendToUserQueue(String message){
    logger.info("Mensaje Enviado a la Cola de Usuarios: " + message);
    producer.sendTo(RabbitConfig.USERS_QUEUE, message);
  }

  @MessageMapping("/send/business")
  public void sendToBusinessQueue(String message){
    logger.info("Mensaje Enviado a la Cola de Business: " + message);
    producer.sendTo(RabbitConfig.BUSINESS_QUEUE, message);
  }

  @MessageMapping("/send/trading")
  public void sendToTradingQueue(String message){
    logger.info("Mensaje Enviado a la Cola de Trading: " + message);
    producer.sendTo(RabbitConfig.TRADING_QUEUE, message);
  }

  @MessageMapping("/send/analytics")
  public void sendToAnalyticsQueue(String message){
    logger.info("Mensaje Enviado a la Cola de Analytics: " + message);
    producer.sendTo(RabbitConfig.ANALYTICS_QUEUE, message);
  }
}
