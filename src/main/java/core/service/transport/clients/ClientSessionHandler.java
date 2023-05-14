package core.service.transport.clients;

import java.lang.reflect.Type;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.lang.Nullable;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaders;
import org.springframework.messaging.simp.stomp.StompSession;
import org.springframework.messaging.simp.stomp.StompSessionHandler;

import core.service.transport.server.IncommingMessage;

public class ClientSessionHandler implements StompSessionHandler {

  private static final Logger logger = LogManager.getLogger(ClientSessionHandler.class);

  public Type getPayloadType(StompHeaders headers) {
    return IncommingMessage.class;
  }

  @Override
  public void handleFrame(StompHeaders headers, @Nullable Object payload) {
	  IncommingMessage msg = (IncommingMessage) payload;
	  logger.info("Mensaje Escuchado por el Cliente: " + msg.getContent() + " desde: " + msg.getFrom());
  }

  @Override
  public void afterConnected(StompSession session, StompHeaders connectedHeaders) {
    logger.info("New session established : " + session.getSessionId());
  }

  @Override
  public void handleException(StompSession session, @Nullable StompCommand command, StompHeaders headers,
      byte[] payload, Throwable exception) {
      logger.error("Oops we got an exception", exception);
  }

  @Override
  public void handleTransportError(StompSession session, Throwable exception) {
    logger.error("Oops we got an transport error", exception);
  }
}
