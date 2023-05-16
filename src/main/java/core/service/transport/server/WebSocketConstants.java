package core.service.transport.server;

import java.util.Map;

import core.service.transport.RabbitConfig;

public class WebSocketConstants {
  
  public static final Map<String, String> ENDPOINTS = Map.of(
    "trading", "ws://localhost:8080/trading",
    "users", "ws://localhost:8080/users",
    "business", "ws://localhost:8080/business",
    "analytics", "ws://localhost:8080/analytics"

  );

  public static final Map<String, String> QueueNameMaps = Map.of(
    "/topic/business", RabbitConfig.BUSINESS_QUEUE,
    "/topic/users", RabbitConfig.USERS_QUEUE,
    "/topic/analytics", RabbitConfig.ANALYTICS_QUEUE,
    "/topic/trading", RabbitConfig.TRADING_QUEUE
  );

  public static final String PREFIX_APP = "/app";
  public static final String PREFIX_TOPIC = "/topic";
}
