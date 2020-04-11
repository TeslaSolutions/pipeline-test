package com.dream.controllers;

import com.dream.configs.JwtTokenUtil;
import com.dream.dtos.UserDTO;
import com.dream.jwt.JwtRequest;
import com.dream.jwt.JwtResponse;
import com.dream.jwt.JwtUserDetailsService;
import com.dream.utils.Endpoints;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RequestMapping(Endpoints.GENERAL)
@RestController
public class AuthenticationRestController {


  @Autowired
  private AuthenticationManager authenticationManager;

  @Autowired
  private JwtTokenUtil jwtTokenUtil;

  @Autowired
  private JwtUserDetailsService userDetailsService;

  @PostMapping(value = Endpoints.AUTHENTICATE)
  public ResponseEntity<JwtResponse> createAuthenticationToken(@RequestBody JwtRequest authenticationRequest) throws Exception {

    authenticate(authenticationRequest.getUsername(), authenticationRequest.getPassword());

    final UserDetails userDetails = userDetailsService
            .loadUserByUsername(authenticationRequest.getUsername());

    final String token = jwtTokenUtil.generateToken(userDetails);

    return ResponseEntity.ok(new JwtResponse(token));
  }

  @PostMapping(value = Endpoints.REGISTER)
  public ResponseEntity<?> saveUser(@RequestBody UserDTO user) throws Exception {
    return ResponseEntity.ok(userDetailsService.save(user));
  }

  private void authenticate(String username, String password) throws Exception {
    try {
      Authentication authentication = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(username, password));
      SecurityContextHolder.getContext().setAuthentication(authentication);
    } catch (DisabledException e) {
      throw new Exception("USER_DISABLED", e);
    } catch (BadCredentialsException e) {
      throw new Exception("INVALID_CREDENTIALS", e);
    }
  }
}
