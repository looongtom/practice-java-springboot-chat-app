package com.satvik.satchat.service;

import com.satvik.satchat.entity.ConversationEntity;
import com.satvik.satchat.model.ChatMessage;
import com.satvik.satchat.model.MessageDeliveryStatusEnum;
import com.satvik.satchat.model.UnseenMessageCountResponse;
import com.satvik.satchat.model.UserConnection;
import java.util.List;
import java.util.UUID;

public interface IConversationService {
  public List<UserConnection> getUserFriends();

  public List<UnseenMessageCountResponse> getUnseenMessageCount();

  public List<ChatMessage> getUnseenMessages(UUID fromUserId);

  public void updateMessageDelivery(
      UUID user,
      List<ConversationEntity> entities,
      MessageDeliveryStatusEnum messageDeliveryStatusEnum);

  public List<ChatMessage> setReadMessages(List<ChatMessage> chatMessages);
}
