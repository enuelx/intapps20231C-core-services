package core.service.transport;

import org.springframework.amqp.rabbit.connection.CachingConnectionFactory;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

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
  public static final String TRADING_EXCHANGE = "trading-exchange";

  public void print(String str){
    System.out.println(str);
  }

  @Bean 
  public ConnectionFactory connectionFactory(){
    CachingConnectionFactory factory = new CachingConnectionFactory(host);

    // print("Host: " + host);
    // print("Username: " + username);
    // print("Password: " + password);
    // print("Port: " + port);

    factory.setUsername(username);
    factory.setPassword(password);
    factory.setPort(port);

    return factory;
  }

  @Bean
  public RabbitTemplate rabbitTemplate(ConnectionFactory connectionFactory){
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
  public Queue queue(){
    return new Queue(TRADING_QUEUE);
  }

  @Bean
  public Binding bind(Queue queue, FanoutExchange exchange){
    return BindingBuilder.bind(queue).to(exchange);
  }

  @Bean
  public FanoutExchange fanoutExchange(){
    return new FanoutExchange(TRADING_EXCHANGE);
  }
}
