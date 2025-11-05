package ru.programm_shcool.quizizz.dto.elements;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@ToString
@EqualsAndHashCode
public class RenameElementDto {
    private String path;
    private String newName;
}
