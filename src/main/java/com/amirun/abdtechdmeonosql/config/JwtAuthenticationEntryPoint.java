package com.amirun.abdtechdmeonosql.config;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.io.Serializable;

@Component
public class JwtAuthenticationEntryPoint implements AuthenticationEntryPoint, Serializable {

    /**
     * This method is called when an unauthenticated user tries to access a protected resource.
     * It sends an "Unauthorized" response to the client with a status code of 401 (HTTP Status Code for Unauthorized).
     *
     * @param request       The incoming HTTP request.
     * @param response      The HTTP response to be sent to the client.
     * @param authException The exception representing the authentication failure (e.g., missing or invalid token).
     * @throws IOException  If an I/O error occurs while sending the response.
     */
    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response,
                         AuthenticationException authException) throws IOException {
        response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Unauthorized");
    }
}