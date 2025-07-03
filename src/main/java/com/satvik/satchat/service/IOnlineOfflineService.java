package com.satvik.satchat.service;

import com.satvik.satchat.config.UserDetailsImpl;
import com.satvik.satchat.entity.ConversationEntity;
import com.satvik.satchat.model.MessageDeliveryStatusEnum;
import com.satvik.satchat.model.UserResponse;

import java.security.Principal;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

public interface IOnlineOfflineService {
    public void addOnlineUser(Principal user);

    public void removeOnlineUser(Principal user);

    public boolean isUserOnline(UUID userId);

    public UserDetailsImpl getUserDetails(Principal principal);

    public List<UserResponse> getOnlineUsers();

    public void addUserSubscribed(Principal user, String subscribedChannel);

    public void removeUserSubscribed(Principal user, String subscribedChannel);

    public boolean isUserSubscribed(UUID username, String subscription);

    public Map<String, Set<String>> getUserSubscribed();

    public void notifySender(
            UUID senderId,
            List<ConversationEntity> entities,
            MessageDeliveryStatusEnum messageDeliveryStatusEnum);
}
