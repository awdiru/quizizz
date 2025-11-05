package ru.programm_shcool.quizizz.dto.elements.directory;

import lombok.*;
import ru.programm_shcool.quizizz.dto.elements.ElementDto;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@ToString
@EqualsAndHashCode
public class DirectoryDto {
    private String path;
    private List<ElementDto> children;
}
