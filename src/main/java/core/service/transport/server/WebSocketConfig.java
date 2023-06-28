package core.service.transport.server;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.concurrent.atomic.AtomicInteger;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.config.ChannelRegistration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;
import core.service.transport.RabbitConsumer;

@Configuration
@EnableWebSocketMessageBroker
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer{

  private static final Logger logger = LogManager.getLogger(WebSocketConfig.class);

  @Autowired
  private BeanFactory beanFactory;

  HashMap<String, ArrayList<String>> clientSubscriptions;
  HashMap<String, ConsumerPair> consumers;

  @Override
  public void configureMessageBroker(MessageBrokerRegistry config) {

    config.enableSimpleBroker(WebSocketConstants.PREFIX_TOPIC);
    config.setApplicationDestinationPrefixes(WebSocketConstants.PREFIX_APP);
  }

  @Override
  public void registerStompEndpoints(StompEndpointRegistry registry) {
    for (var itr : WebSocketConstants.ENDPOINTS.keySet()){
      registry.addEndpoint("/" + itr).withSockJS();
      registry.addEndpoint("/" + itr);
    }

    clientSubscriptions = new HashMap<>();
    consumers = new HashMap<>();
  }

  @Override
  public void configureClientInboundChannel(ChannelRegistration registration) {
    registration.interceptors(new ChannelInterceptor() {
      @Override
      public
      void postSend(Message<?> message, MessageChannel channel, boolean sent) {
        logger.info("POST SEND: " + message.toString());

        Object stompHeader = message.getHeaders().get("stompCommand");
        String sessionId = (String) message.getHeaders().get("simpSessionId");

        if (sessionId == null || stompHeader == null)
          return;

        StompCommand command = (StompCommand) stompHeader;
        switch(command){
          case SUBSCRIBE:{
            String destination = (String) message.getHeaders().get("simpDestination");
            logger.info("DESTINATION: " + destination);
            registerSubscribe(destination);
            clientSubscriptions.get(sessionId).add(destination);
            break;
          }
          case UNSUBSCRIBE:{

            break;
          }
          case DISCONNECT:{
            ArrayList<String> subscriptions = clientSubscriptions.get(sessionId);

            if (subscriptions == null)
              return;

            for(String sub : subscriptions){
              logger.info("Disconnecting Points: " + sub + " from Session: " + sessionId);
              unRegisterSubscribe(sub);
            }

            clientSubscriptions.remove(sessionId);

            break;
          }
          case CONNECT:{
            if (!clientSubscriptions.containsKey(sessionId)){
              clientSubscriptions.put(sessionId, new ArrayList<String>());
            }
            break;
          }
          default:{
            break;
          }
        }
      }
    });
  }


  @Scheduled(fixedDelay = 600 * 1000)
  public void checkConnections(){
    for (var itr : consumers.keySet()){
      ConsumerPair info = consumers.get(itr);
      logger.info("Listen counter for Channel: " + itr + "--" + info.getCounter());
    }
  }

  public void registerSubscribe(String destination){
    if (!consumers.containsKey(destination)){
      String queueName = WebSocketConstants.QueueNameMaps.get(destination);

      if (queueName == null)
        return;

      RabbitConsumer consumer = beanFactory.getBean(RabbitConsumer.class, queueName, destination);
      logger.info("QUEUE: " + queueName);
      consumers.put(destination, new ConsumerPair(consumer));
    }
    else{
      ConsumerPair pair = consumers.get(destination);
      pair.increaseCounter();
    }
  }

  public void unRegisterSubscribe(String destination){
    if (!consumers.containsKey(destination)){
      return;
    }
    else{
      ConsumerPair pair = consumers.get(destination);
      pair.decreaseCounter();

      if (pair.getCounter() <= 0){
        pair = null;
        consumers.remove(destination);
      }
    }
  }

  private class ConsumerPair{

    private RabbitConsumer consumer;
    public RabbitConsumer getConsumer() {
      return consumer;
    }

    private AtomicInteger counter;

    public Integer getCounter(){
      return counter.get();
    }

    public void increaseCounter() {
      counter.incrementAndGet();
    }

    public void decreaseCounter(){
      counter.decrementAndGet();
    }

    public ConsumerPair(RabbitConsumer cRabbitConsumer){
      consumer = cRabbitConsumer;
      counter = new AtomicInteger(1);
    }

  }
}
