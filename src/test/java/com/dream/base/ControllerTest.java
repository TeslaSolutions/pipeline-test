package com.dream.base;

import com.dream.jwt.JwtRequestFilter;
import com.google.common.base.Strings;
import org.junit.Before;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.json.JacksonJsonParser;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.springframework.test.web.servlet.setup.DefaultMockMvcBuilder;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import org.springframework.web.context.WebApplicationContext;

import java.util.Arrays;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Abstract controller test
 */
@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
@EnableTransactionManagement
public abstract class ControllerTest {

    @Autowired
    protected TestRestTemplate restTemplate;


    protected HttpHeaders authorizedHeaders() {
        HttpHeaders headers = new HttpHeaders();
        headers.add("Authorization", "Bearer " + obtainToken("simic", "redstar"));
        return headers;
    }

    protected HttpHeaders authorizedActionNotPermittedHeaders() {
        HttpHeaders headers = new HttpHeaders();
        headers.add("Authorization", "Bearer " + obtainToken("goran", "redstar"));
        return headers;
    }

    protected HttpHeaders authorizedHeadersWithBearer() {
        HttpHeaders headers = authorizedHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setAccept(Arrays.asList(MediaType.APPLICATION_JSON));
        return headers;
    }

    protected HttpHeaders authorizedHeadersDefault() {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setAccept(Arrays.asList(MediaType.APPLICATION_JSON));
        return headers;
    }

    @Autowired
    private WebApplicationContext webApplicationContext;

    @Autowired
    private JwtRequestFilter authenticationFilter;

    protected MockMvc mockMvc;

    private String existingToken;

    @Before
    public void init() {
        DefaultMockMvcBuilder builder = MockMvcBuilders.webAppContextSetup(this.webApplicationContext)
                .alwaysDo(MockMvcResultHandlers.print());
        this.mockMvc = builder.addFilters(authenticationFilter).build();
    }

    private String obtainToken(String username, String password) {
        if (!Strings.isNullOrEmpty(existingToken)) {
            return existingToken;
        }
        try {
            ResultActions result = mockMvc.perform(post("/v0/auth/authenticate")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content("{ \"password\": \"" + password + "\", \"username\": \"" + username + "\"}")
                    .accept("application/json;charset=UTF-8"))
                    .andExpect(status().isOk())
                    .andExpect(content().contentType("application/json;charset=UTF-8"));
            String resultString = result.andReturn().getResponse().getContentAsString();
            JacksonJsonParser jsonParser = new JacksonJsonParser();
            existingToken = jsonParser.parseMap(resultString).get("jwttoken").toString();
            return existingToken;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "NO_TOKEN";
    }

    protected String substringResponse(String response) {
        return response.substring(1, response.length() - 1);
    }
}