package com.satvik.satchat.service;

import com.satvik.satchat.config.UserDetailsImpl;
import com.satvik.satchat.model.ChatMessage;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;

public interface IChatService {
  public void sendMessageToConvId(
      ChatMessage chatMessage, String conversationId, SimpMessageHeaderAccessor headerAccessor);

  public void populateContext(ChatMessage chatMessage, UserDetailsImpl userDetails);

  public UserDetailsImpl getUser();
}
