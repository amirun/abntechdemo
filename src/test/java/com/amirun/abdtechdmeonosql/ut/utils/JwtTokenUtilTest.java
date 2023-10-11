package com.amirun.abdtechdmeonosql.ut.utils;

import com.amirun.abdtechdmeonosql.utils.JwtTokenUtil;
import io.jsonwebtoken.ExpiredJwtException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.security.core.userdetails.User;

import java.util.Collections;
import java.util.Date;

import static org.junit.jupiter.api.Assertions.*;

class JwtTokenUtilTest {

    private String secret = "0i9G6rG5Lo09OoD0parOHN4u0ag4bWC7zcJPOIz1zhg0MDar9Iw3cltTjOmDwO_g6zF";
    private long jwtTokenValidityMillis = 1800000;
    private User user = new User("testuser", "password", Collections.EMPTY_LIST);
    private JwtTokenUtil jwtTokenUtil;

    @Nested
    class NestedTests {
        @BeforeEach
        public void setUp() {
            jwtTokenUtil = new JwtTokenUtil(jwtTokenValidityMillis, secret);
        }

        @Test
        void getUsernameFromToken() {
            String token = jwtTokenUtil.generateToken(user);
            String username = jwtTokenUtil.getUsernameFromToken(token);
            assertEquals(user.getUsername(), username);
        }

        @Test
        void getExpirationDateFromToken() {
            String token = jwtTokenUtil.generateToken(user);
            Date expirationDate = jwtTokenUtil.getExpirationDateFromToken(token);
            assertTrue(expirationDate.after(new Date(System.currentTimeMillis())));
        }

        @Test
        void generateToken() {
            String token = jwtTokenUtil.generateToken(user);
            assertNotNull(token);
        }

        @Test
        void testValidateToken() {
            String token = jwtTokenUtil.generateToken(user);
            boolean isValid = jwtTokenUtil.validateToken(token, user);
            assertTrue(isValid);
        }
    }

    @Test
    void testValidateTokenExpired() {
        // Given an expired token and matching user details
        JwtTokenUtil util = new JwtTokenUtil(1, secret);
        String token = util.generateToken(user);
        assertThrows(ExpiredJwtException.class, () -> util.validateToken(token, user));
    }

}