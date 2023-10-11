package com.amirun.abdtechdmeonosql.dto;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@RequiredArgsConstructor
public class TokenResponse {
    private static final long serialVersionUID = -8091879091924046844L;
    private final String jwttoken;
}
