package com.amirun.abdtechdmeonosql.it;

import com.amirun.abdtechdmeonosql.TestAbnTechDemoNosqlApplication;
import com.amirun.abdtechdmeonosql.dto.AuthRequest;
import com.amirun.abdtechdmeonosql.utils.JwtTokenUtil;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.testcontainers.shaded.com.fasterxml.jackson.databind.ObjectMapper;

import java.util.Collections;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest(classes = TestAbnTechDemoNosqlApplication.class, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
class AuthControllerIT {

    /** @noinspection SpringJavaInjectionPointsAutowiringInspection*/
    @Autowired
    private MockMvc mockMvc;

    private final ObjectMapper mapper = new ObjectMapper();

    @Autowired
    private JwtTokenUtil jwtTokenUtil;

    @Test
    void testCreateAuthenticationToken() throws Exception {
        AuthRequest authRequest = new AuthRequest("testUser", "pAssw0rd");
        UserDetails userDetails = new User(
                "testUser",
                "pAssw0rd",
                Collections.EMPTY_LIST
        );

        MvcResult result = mockMvc.perform(post("/authenticate")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(authRequest)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.jwttoken").isNotEmpty())
                .andReturn();

        String token = mapper.readTree(result.getResponse().getContentAsString()).get("jwttoken").asText();
        assertNotNull(token);
        assertTrue(jwtTokenUtil.validateToken(token, userDetails));
    }

}