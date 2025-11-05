package ru.programm_shcool.quizizz.entity;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@SuperBuilder
@ToString
@Entity
@DiscriminatorValue("FILE")
public class Test extends Element {
    @OneToMany(mappedBy = "test", cascade = CascadeType.ALL)
    @Builder.Default
    @ToString.Exclude
    private List<Question> questions = new ArrayList<>();
}