package com.amirun.abdtechdmeonosql.dto;

import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class ErrorDTO {
    String description;
    String errorDetails;
}
