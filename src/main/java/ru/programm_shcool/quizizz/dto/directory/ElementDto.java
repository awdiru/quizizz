package ru.programm_shcool.quizizz.dto.directory;

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
    private boolean isDirectory;
}
