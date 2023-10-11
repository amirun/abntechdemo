package com.amirun.abdtechdmeonosql.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@ToString
public class AuthRequest {
    @NotBlank
    String userName;
    String password;
}
