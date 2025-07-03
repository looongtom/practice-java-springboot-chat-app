package com.satvik.satchat.controller;

import com.satvik.satchat.model.ChatMessage;
import com.satvik.satchat.service.IChatService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.stereotype.Controller;

@Controller
@Slf4j
public class ChatController {

  private final IChatService iChatService;

  @Autowired
  public ChatController(IChatService chatService) {
    this.iChatService = chatService;
  }

  @MessageMapping("/chat/sendMessage/{convId}")
  public ChatMessage sendMessageToConvId(
      @Payload ChatMessage chatMessage,
      SimpMessageHeaderAccessor headerAccessor,
      @DestinationVariable("convId") String conversationId) {
    iChatService.sendMessageToConvId(chatMessage, conversationId, headerAccessor);
    return chatMessage;
  }
}
