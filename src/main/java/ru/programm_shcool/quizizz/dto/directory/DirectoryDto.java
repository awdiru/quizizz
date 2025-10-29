package ru.programm_shcool.quizizz.dto.directory;

import lombok.*;

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
