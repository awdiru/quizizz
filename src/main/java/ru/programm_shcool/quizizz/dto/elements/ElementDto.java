package ru.programm_shcool.quizizz.dto.elements;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@ToString
@EqualsAndHashCode
public class ElementDto {
    private String name;
    private String created;
    private boolean isDirectory;
}
