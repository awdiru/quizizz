package ru.programm_shcool.quizizz.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ru.programm_shcool.quizizz.entity.Element;
import ru.programm_shcool.quizizz.entity.ElementType;

import java.util.Optional;

public interface ElementRepository extends JpaRepository<Element, Long> {
    Optional<Element> findFirstByNameAndType(String name, ElementType type);
    void deleteById(Long id);
}
