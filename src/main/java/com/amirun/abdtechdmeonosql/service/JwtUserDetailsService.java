package com.amirun.abdtechdmeonosql.service;

import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.ArrayList;

/**
 * The {@code JwtUserDetailsService} class is a service responsible for loading user details during authentication.
 * It implements the {@link UserDetailsService} interface and provides user information based on the provided username.
 */
@Service
@RequiredArgsConstructor
public class JwtUserDetailsService implements UserDetailsService {

    private final Logger logger = LoggerFactory.getLogger(JwtUserDetailsService.class);

    @Value("${security.dummy.password}")
    private String dummyPassword;


    /**
     * Load user details based on the provided username.
     * <b>NOTE:</b> This is a dummy implementation. Requires a user repository for full functionality. Password is fetched from application.properties.
     * @param username The username for which user details are to be loaded.
     * @return A UserDetails object representing the user, typically including the username, password, and authorities.
     * @throws UsernameNotFoundException If the user with the provided username is not found.
     */
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        logger.debug("Finding user: {}", username);
        return new User(username, dummyPassword, new ArrayList<>());
    }
}
