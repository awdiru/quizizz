package ru.programm_shcool.quizizz.dto.teacher;

import lombok.*;
import ru.programm_shcool.quizizz.entity.Teacher;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@ToString
@EqualsAndHashCode
public class UnconfirmedTeacher {
    private Teacher teacher;
    private String temporaryConfirmationToken;
}
