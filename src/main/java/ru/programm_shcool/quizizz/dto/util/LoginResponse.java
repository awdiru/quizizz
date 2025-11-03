package ru.programm_shcool.quizizz.dto.util;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@ToString
@EqualsAndHashCode
public class LoginResponse {
    private int status;
    private String message;
    private String username;
    private String token;
}
