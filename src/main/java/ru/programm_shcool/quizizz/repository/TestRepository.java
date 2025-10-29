package ru.programm_shcool.quizizz.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.programm_shcool.quizizz.entity.Directory;
import ru.programm_shcool.quizizz.entity.Test;

import java.util.List;
import java.util.Optional;

@Repository
public interface TestRepository extends JpaRepository<Test, Long> {
    Optional<Test> findByNameAndDirectory(String name, Directory directory);
    List<Test> findAllByDirectory(Directory directory);
}
