package ru.programm_shcool.quizizz.dto.test;

import lombok.*;
import ru.programm_shcool.quizizz.dto.question.QuestionDto;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@ToString
@EqualsAndHashCode
public class TestDto {
    private String path;
    private List<QuestionDto> questions;
}
