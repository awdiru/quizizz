package ru.programm_shcool.quizizz.dto.util;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@ToString
@EqualsAndHashCode
public class LoginDto {
    private String login;
    private String password;
    private String email;
    private Boolean isAdmin;
}
