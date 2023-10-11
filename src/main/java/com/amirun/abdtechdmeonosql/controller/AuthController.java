package com.amirun.abdtechdmeonosql.controller;

import com.amirun.abdtechdmeonosql.dto.AuthRequest;
import com.amirun.abdtechdmeonosql.dto.ErrorDTO;
import com.amirun.abdtechdmeonosql.dto.TokenResponse;
import com.amirun.abdtechdmeonosql.service.AuthenticationServiceImpl;
import com.amirun.abdtechdmeonosql.service.JwtUserDetailsService;
import com.amirun.abdtechdmeonosql.utils.JwtTokenUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

/**
 * The {@code AuthController} class is a REST controller responsible for handling user authentication requests.
 *
 * This controller allows clients to request authentication by providing valid user credentials. Upon successful
 * authentication, it generates a JSON Web Token (JWT) and sends it back to the client for further authorization.
 */
@CrossOrigin
@RequiredArgsConstructor
@RestController
@RequestMapping("/authenticate")
public class AuthController {

    private final JwtTokenUtil jwtTokenUtil;
    private final JwtUserDetailsService userDetailsService;
    private final AuthenticationServiceImpl authenticationService;

    /**
     * POST endpoint for user authentication. Receives user credentials in the request body and performs authentication.
     * If authentication is successful, it generates a JWT token and returns it in the response.
     * <b>NOTE:</b> This is a dummy implementation. Requires a user repository for full functionality. Password is fetched from application.properties.
     * @param user The authentication request containing user credentials.
     * @return A ResponseEntity containing a JWT token wrapped in a TokenResponse object if authentication is successful.
     */

    @Operation(description = "Endpoint to authenticate user. This is a dummy endpoint to generate a token. Use any value for userName and value of password is always 'string'")
    @ApiResponses(
            value = {
                    @ApiResponse(responseCode = "200",
                            description = "Returns a jwt token",
                            content = {@Content(mediaType = "application/json",
                                    array = @ArraySchema(schema = @Schema(implementation = AuthRequest.class)))}),
                    @ApiResponse(responseCode = "400",
                            description = "Invalid input",
                            content = {@Content(mediaType = "application/json",
                                    schema = @Schema(implementation = ErrorDTO.class))})
            }
    )
    @PostMapping
    public ResponseEntity<TokenResponse> createAuthenticationToken(@RequestBody @Valid AuthRequest user) {

        authenticationService.authenticate(user);
        final UserDetails userDetails = userDetailsService.loadUserByUsername(user.getUserName());
        final String token = jwtTokenUtil.generateToken(userDetails);

        return ResponseEntity.ok(new TokenResponse(token));
    }

}