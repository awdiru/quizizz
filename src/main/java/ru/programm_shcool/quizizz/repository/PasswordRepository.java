package ru.programm_shcool.quizizz.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.programm_shcool.quizizz.entity.Password;

@Repository
public interface PasswordRepository extends JpaRepository<Password, Long> {
}
