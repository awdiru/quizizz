package ru.programm_shcool.quizizz.dto.users;

import lombok.*;
import ru.programm_shcool.quizizz.entity.User;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@ToString
@EqualsAndHashCode
public class UnconfirmedUserDto {
    private User user;
    private String temporaryConfirmationToken;
}
