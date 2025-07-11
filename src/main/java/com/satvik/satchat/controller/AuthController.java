package com.satvik.satchat.controller;

import com.satvik.satchat.config.UserDetailsImpl;
import com.satvik.satchat.entity.RoleEntity;
import com.satvik.satchat.entity.UserEntity;
import com.satvik.satchat.model.ERole;
import com.satvik.satchat.model.JwtResponse;
import com.satvik.satchat.model.LoginRequest;
import com.satvik.satchat.model.MessageResponse;
import com.satvik.satchat.model.SignupRequest;
import com.satvik.satchat.repository.RoleRepository;
import com.satvik.satchat.repository.UserRepository;
import com.satvik.satchat.utils.JWTUtils;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@CrossOrigin(origins = "*")
@RestController
@RequestMapping("/api/auth")
public class AuthController {
  public static final String ERROR_ROLE_IS_NOT_FOUND = "Error: Role is not found.";
  private final AuthenticationManager authenticationManager;

  private final UserRepository userRepository;

  private final RoleRepository roleRepository;

  private final PasswordEncoder encoder;

  private final JWTUtils jwtUtils;

  public AuthController(
      AuthenticationManager authenticationManager,
      UserRepository userRepository,
      RoleRepository roleRepository,
      PasswordEncoder passwordEncoder,
      JWTUtils jwtUtils) {
    this.authenticationManager = authenticationManager;
    this.userRepository = userRepository;
    this.roleRepository = roleRepository;
    this.encoder = passwordEncoder;
    this.jwtUtils = jwtUtils;
  }

  @PostMapping("/login")
  public ResponseEntity<JwtResponse> authenticateUser(
      @RequestBody LoginRequest loginRequest, HttpServletResponse response) {

    Authentication authentication =
        authenticationManager.authenticate(
            new UsernamePasswordAuthenticationToken(
                loginRequest.getUsername(), loginRequest.getPassword()));

    SecurityContextHolder.getContext().setAuthentication(authentication);
    String jwt = jwtUtils.generateJwtToken(authentication);

    UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
    List<String> roles =
        userDetails.getAuthorities().stream().map(GrantedAuthority::getAuthority).toList();

    JwtResponse jwtResponse =
        JwtResponse.builder()
            .token(jwt)
            .id(userDetails.getId())
            .username(userDetails.getUsername())
            .email(userDetails.getEmail())
            .roles(roles)
            .build();

    response.addCookie(new Cookie("access_token", jwt));
    return ResponseEntity.ok(jwtResponse);
  }

  @PostMapping("/register")
  public ResponseEntity<MessageResponse> registerUser(@RequestBody SignupRequest signUpRequest) {
    if (Boolean.TRUE.equals(userRepository.existsByUsername(signUpRequest.getUsername()))) {
      return ResponseEntity.badRequest()
          .body(new MessageResponse("Error: Username is already taken!"));
    }

    if (Boolean.TRUE.equals(userRepository.existsByEmail(signUpRequest.getEmail()))) {
      return ResponseEntity.badRequest()
          .body(new MessageResponse("Error: Email is already in use!"));
    }

    UserEntity userEntity =
        UserEntity.builder()
            .id(UUID.randomUUID())
            .username(signUpRequest.getUsername())
            .email(signUpRequest.getEmail())
            .password(encoder.encode(signUpRequest.getPassword()))
            .build();

    Set<String> strRoles = signUpRequest.getRole();
    Set<RoleEntity> roles = new HashSet<>();

    if (strRoles == null) {
      RoleEntity userRole =
          roleRepository
              .findByName(ERole.USER)
              .orElseThrow(() -> new RuntimeException(ERROR_ROLE_IS_NOT_FOUND));
      roles.add(userRole);
    } else {
      strRoles.forEach(
          role -> {
            switch (role) {
              case "ADMIN":
                RoleEntity adminRole =
                    roleRepository
                        .findByName(ERole.ADMIN)
                        .orElseThrow(() -> new RuntimeException(ERROR_ROLE_IS_NOT_FOUND));
                roles.add(adminRole);

                break;
              case "MODERATOR":
                RoleEntity modRole =
                    roleRepository
                        .findByName(ERole.MODERATOR)
                        .orElseThrow(() -> new RuntimeException(ERROR_ROLE_IS_NOT_FOUND));
                roles.add(modRole);

                break;
              default:
                RoleEntity userRole =
                    roleRepository
                        .findByName(ERole.USER)
                        .orElseThrow(() -> new RuntimeException(ERROR_ROLE_IS_NOT_FOUND));
                roles.add(userRole);
            }
          });
    }

    userEntity.setRoles(roles);
    userRepository.save(userEntity);

    return ResponseEntity.ok(new MessageResponse("User registered successfully!"));
  }
}
