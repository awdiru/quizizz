package ru.programm_shcool.quizizz.dto.answer;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@ToString
@EqualsAndHashCode
public class AnswerDto {
    private String answer;
    private boolean isRight;
}
