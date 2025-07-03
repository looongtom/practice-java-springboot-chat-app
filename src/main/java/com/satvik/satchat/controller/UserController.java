package com.satvik.satchat.controller;

import com.satvik.satchat.model.UserResponse;
import com.satvik.satchat.service.IOnlineOfflineService;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@CrossOrigin(origins = "*")
@RequestMapping("/api/users")
public class UserController {
  private final IOnlineOfflineService iOnlineOfflineService;

  public UserController(IOnlineOfflineService iOnlineOfflineService) {
    this.iOnlineOfflineService = iOnlineOfflineService;
  }

  @GetMapping("/online")
  @PreAuthorize("hasAuthority('ADMIN')")
  List<UserResponse> getOnlineUsers() {
    return iOnlineOfflineService.getOnlineUsers();
  }

  @GetMapping("/subscriptions")
  @PreAuthorize("hasAuthority('ADMIN')")
  Map<String, Set<String>> getSubscriptions() {
    return iOnlineOfflineService.getUserSubscribed();
  }
}
