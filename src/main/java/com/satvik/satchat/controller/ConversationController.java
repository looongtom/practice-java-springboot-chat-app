package com.satvik.satchat.controller;

import com.satvik.satchat.model.ChatMessage;
import com.satvik.satchat.model.UnseenMessageCountResponse;
import com.satvik.satchat.model.UserConnection;
import com.satvik.satchat.service.IConversationService;
import java.util.List;
import java.util.UUID;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@CrossOrigin("*")
@RequestMapping(("/api/conversation"))
public class ConversationController {

  private final IConversationService iConversationService;

  @Autowired
  public ConversationController(IConversationService iConversationService) {
    this.iConversationService = iConversationService;
  }

  @GetMapping("/friends")
  public List<UserConnection> getUserFriends() {
    return iConversationService.getUserFriends();
  }

  @GetMapping("/unseenMessages")
  public List<UnseenMessageCountResponse> getUnseenMessageCount() {
    return iConversationService.getUnseenMessageCount();
  }

  @GetMapping("/unseenMessages/{fromUserId}")
  public List<ChatMessage> getUnseenMessages(@PathVariable("fromUserId") UUID fromUserId) {
    return iConversationService.getUnseenMessages(fromUserId);
  }

  @PutMapping("/setReadMessages")
  public List<ChatMessage> setReadMessages(@RequestBody List<ChatMessage> chatMessages) {
    return iConversationService.setReadMessages(chatMessages);
  }
}
