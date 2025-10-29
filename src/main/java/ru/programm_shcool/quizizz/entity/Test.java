package ru.programm_shcool.quizizz.entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@ToString
@Entity
@Table(name = "tests",
        uniqueConstraints = {
                @UniqueConstraint(columnNames = {"name", "directory_id"})
        })
public class Test {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "name")
    private String name;

    @ManyToOne
    @JoinColumn(name = "directory_id")
    private Directory directory;

    @OneToMany(mappedBy = "test", cascade = CascadeType.ALL)
    @Builder.Default
    @ToString.Exclude
    private List<Question> questions = new ArrayList<>();
}
