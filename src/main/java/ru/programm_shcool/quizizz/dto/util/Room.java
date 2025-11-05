package ru.programm_shcool.quizizz.dto.util;

import lombok.*;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@ToString
@EqualsAndHashCode
public class Room {
    private Long id;
    private List<Guest> guests;
}
