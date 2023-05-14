package core.service.transport.server;

import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

@Configuration
@EnableWebSocketMessageBroker
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer{

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
  }
}
