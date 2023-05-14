package core.service.transport;


import org.springframework.amqp.rabbit.connection.CachingConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.impl.AMQImpl.Exchange.Bind;

import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.util.Collection;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.FanoutExchange;
import org.springframework.amqp.core.Queue;

@Configuration
public class RabbitConfig {

  @Value("${spring.rabbitmq.host}")
  String host;

  @Value("${spring.rabbitmq.username}")
  String username;

  @Value("${spring.rabbitmq.password}")
  String password;

  @Value("${spring.rabbitmq.port}")
  int port;
 
  public static final String TRADING_QUEUE = "trading-queue";
  public static final String USERS_QUEUE = "users-queue";
  public static final String BUSINESS_QUEUE = "business-queue";
  public static final String ANALYTICS_QUEUE = "analytics-queue";

  public static final String TRADING_EXCHANGE = "trading-exchange";

  public void print(String str){
    System.out.println(str);
  }

  @Bean 
  public ConnectionFactory connectionFactory() throws KeyManagementException, NoSuchAlgorithmException{
    ConnectionFactory factory = new ConnectionFactory();

    factory.setHost(host);
    factory.setUsername(username);
    factory.setPassword(password);
    factory.setPort(port);

    return factory;
  }

  @Bean
  public org.springframework.amqp.rabbit.connection.ConnectionFactory springConnectionFactory(ConnectionFactory rabConnectionFactory){
    return new CachingConnectionFactory(rabConnectionFactory);
  }

  @Bean
  public RabbitTemplate rabbitTemplate(org.springframework.amqp.rabbit.connection.ConnectionFactory connectionFactory){
    final RabbitTemplate rabbitTemplate = new RabbitTemplate(connectionFactory);
    return rabbitTemplate;
  }

  @Bean
  public Consumer consumer(){
    return new Consumer();
  }

  @Bean
  public Producer producer(RabbitTemplate rabbitTemplate){
    return new Producer(rabbitTemplate);
  }

  @Bean
  public Queue tradingQueue(){
    return new Queue(TRADING_QUEUE);
  }

  @Bean
  public Queue userQueue(){
    return new Queue(USERS_QUEUE);
  }

  @Bean
  public Queue businessQueue(){
    return new Queue(BUSINESS_QUEUE);
  }

  @Bean
  public Queue analyticsQueue(){
    return new Queue(ANALYTICS_QUEUE);
  }

  @Bean
  public Binding analyticsBinding(Queue analyticsQueue, FanoutExchange exchange){
    return BindingBuilder.bind(analyticsQueue).to(exchange);
  }

  @Bean
  public Binding businessBinding(Queue businessQueue, FanoutExchange exchange){
    return BindingBuilder.bind(businessQueue).to(exchange);
  }

  @Bean
  public Binding userBinding(Queue userQueue, FanoutExchange exchange){
    return BindingBuilder.bind(userQueue).to(exchange);
  }

  @Bean
  public Binding tradingBinding(Queue tradingQueue, FanoutExchange exchange){
    return BindingBuilder.bind(tradingQueue).to(exchange);
  }

  @Bean
  public FanoutExchange fanoutExchange(){
    return new FanoutExchange(TRADING_EXCHANGE);
  }
}
