package core.service.transport.server;

import java.util.HashMap;
import java.util.Map;

public class WebSocketConstants {
  
  public static final Map<String, String> ENDPOINTS = Map.of(
    "trading", "ws://localhost:8080/trading",
    "users", "ws://localhost:8080/users",
    "business", "ws://localhost:8080/business",
    "analytics", "ws://localhost:8080/analytics"

  );

  public static final String PREFIX_APP = "/app";
  public static final String PREFIX_TOPIC = "/topic";
}
