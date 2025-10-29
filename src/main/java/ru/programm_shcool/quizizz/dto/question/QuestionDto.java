package ru.programm_shcool.quizizz.dto.question;

import lombok.*;
import ru.programm_shcool.quizizz.dto.answer.AnswerDto;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@ToString
@EqualsAndHashCode
public class QuestionDto {
    private String question;
    private List<AnswerDto> answers;
}
