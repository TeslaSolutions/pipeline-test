package com.dream;

import com.dream.base.ControllerTest;
import com.dream.dtos.UserDTO;
import com.dream.jwt.JwtRequest;
import com.dream.jwt.JwtResponse;
import com.dream.utils.Endpoints;
import org.apache.commons.lang3.StringUtils;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

public class AuthControllerTest extends ControllerTest {

    @Test
    public void authenticateValidTest() {
        final String authUrl = StringUtils.join(Endpoints.GENERAL, Endpoints.AUTHENTICATE);
        JwtRequest request = new JwtRequest("simic", "redstar");
        ResponseEntity<JwtResponse> response =
                restTemplate.exchange(authUrl, HttpMethod.POST, new HttpEntity<>(request), JwtResponse.class);
        Assert.assertEquals("Wrong response status", HttpStatus.OK.value(), response.getStatusCodeValue());
        Assert.assertNotNull("Jwt token cannot be null", response.getBody().getJwttoken());
    }

    @Test
    public void authenticateInValidTest() {
        final String authUrl = StringUtils.join(Endpoints.GENERAL, Endpoints.AUTHENTICATE);
        JwtRequest request = new JwtRequest("simic", "redstar90");
        ResponseEntity<JwtResponse> response =
                restTemplate.exchange(authUrl, HttpMethod.POST, new HttpEntity<>(request), JwtResponse.class);
        Assert.assertEquals("Wrong response status", HttpStatus.FORBIDDEN.value(), response.getStatusCodeValue());
    }

    @Test
    public void authenticateDisabledUserTest() {
        final String authUrl = StringUtils.join(Endpoints.GENERAL, Endpoints.AUTHENTICATE);
        JwtRequest request = new JwtRequest("mioc", "redstar");
        ResponseEntity<JwtResponse> response =
                restTemplate.exchange(authUrl, HttpMethod.POST, new HttpEntity<>(request), JwtResponse.class);
        Assert.assertEquals("Wrong response status", HttpStatus.FORBIDDEN.value(), response.getStatusCodeValue());
    }

    @Test
    public void registerUserValidTest() {
        final String registerUrl = StringUtils.join(Endpoints.GENERAL + Endpoints.REGISTER);
        UserDTO request = UserDTO.builder()
                .username("simicgoran")
                .password("redstar90")
                .email("simic95@live.com")
                .build();
        ResponseEntity<String> response =
                restTemplate.exchange(registerUrl, HttpMethod.POST, new HttpEntity<>(request), String.class);
        Assert.assertEquals("Wrong response status", HttpStatus.OK.value(), response.getStatusCodeValue());
    }

}
