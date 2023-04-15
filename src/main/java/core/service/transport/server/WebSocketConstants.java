package core.service.transport.server;

import java.util.HashMap;
import java.util.Map;

public class WebSocketConstants {
  
  public static final Map<String, String> ENDPOINTS = Map.of(
    "trading", "ws://localhost:8080/trading",
    "users", "ws://localhost:8080/users"
  );
}
