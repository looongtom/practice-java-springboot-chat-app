package com.satvik.satchat.config.websocket;

import com.satvik.satchat.service.IOnlineOfflineService;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.messaging.simp.annotation.SendToUser;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.messaging.SessionConnectedEvent;
import org.springframework.web.socket.messaging.SessionDisconnectEvent;
import org.springframework.web.socket.messaging.SessionSubscribeEvent;
import org.springframework.web.socket.messaging.SessionUnsubscribeEvent;

@Component
@Slf4j
public class WebSocketEventListener {

  private final IOnlineOfflineService iOnlineOfflineService;

  private final Map<String, String> simpSessionIdToSubscriptionId;

  public WebSocketEventListener(IOnlineOfflineService iOnlineOfflineService) {
    this.iOnlineOfflineService = iOnlineOfflineService;
    this.simpSessionIdToSubscriptionId = new ConcurrentHashMap<>();
  }

  @EventListener
  public void handleWebSocketDisconnectListener(SessionDisconnectEvent event) {
    iOnlineOfflineService.removeOnlineUser(event.getUser());
  }

  @EventListener
  @SendToUser
  public void handleSubscribeEvent(SessionSubscribeEvent sessionSubscribeEvent) {
    String subscribedChannel =
        (String) sessionSubscribeEvent.getMessage().getHeaders().get("simpDestination");
    String simpSessionId =
        (String) sessionSubscribeEvent.getMessage().getHeaders().get("simpSessionId");
    if (subscribedChannel == null) {
      log.error("SUBSCRIBED TO NULL?? WAT?!");
      return;
    }
    simpSessionIdToSubscriptionId.put(simpSessionId, subscribedChannel);
    iOnlineOfflineService.addUserSubscribed(sessionSubscribeEvent.getUser(), subscribedChannel);
  }

  @EventListener
  public void handleUnSubscribeEvent(SessionUnsubscribeEvent unsubscribeEvent) {
    String simpSessionId = (String) unsubscribeEvent.getMessage().getHeaders().get("simpSessionId");
    String unSubscribedChannel = simpSessionIdToSubscriptionId.get(simpSessionId);
    iOnlineOfflineService.removeUserSubscribed(unsubscribeEvent.getUser(), unSubscribedChannel);
  }

  @EventListener
  public void handleConnectedEvent(SessionConnectedEvent sessionConnectedEvent) {
    iOnlineOfflineService.addOnlineUser(sessionConnectedEvent.getUser());
  }
}
