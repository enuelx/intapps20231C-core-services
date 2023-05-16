package core.service.transport;

import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageListener;
import org.springframework.amqp.rabbit.connection.CachingConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitAdmin;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.rabbit.listener.SimpleMessageListenerContainer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Caching;
import org.springframework.messaging.simp.SimpMessagingTemplate;

import core.service.transport.server.IncommingMessage;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;

public class RabbitConsumer {

  String queue;
  String destination;

  @Autowired
  private SimpMessagingTemplate socketTemplate;

  @Autowired
  RabbitTemplate rabbitTemplate;

  SimpleMessageListenerContainer listenerContainer;

  public RabbitConsumer(String queueString, String deString){
    System.out.println("Params: " + queueString + "----" + deString);
    queue = queueString;
    destination = deString;
    listenerContainer = null;
  }

  @PreDestroy
  public void  stopListener(){
    if (listenerContainer != null){
      listenerContainer.stop();
      listenerContainer = null;
    }
  }

  @PostConstruct
  public void initListener(){
    listenerContainer= new SimpleMessageListenerContainer(rabbitTemplate.getConnectionFactory());
    listenerContainer.addQueueNames(queue);
    listenerContainer.setMessageListener(new MessageListener() {
      @Override
      public void onMessage(Message message) {
        System.out.println("MESSAGE RABBIT CONSUMER PARAMS: " + message.toString());
        String payload = new String(message.getBody());
        IncommingMessage retMessage = new IncommingMessage(payload, destination);
        socketTemplate.convertAndSend(destination, retMessage);
      }
    });
    listenerContainer.start();
  }
}
