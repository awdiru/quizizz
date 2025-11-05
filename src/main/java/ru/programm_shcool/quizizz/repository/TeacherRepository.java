package ru.programm_shcool.quizizz.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.programm_shcool.quizizz.entity.User;

import java.util.List;
import java.util.Optional;

public interface TeacherRepository extends JpaRepository<User, Long> {
    Optional<User> findByLogin(String login);
    List<User> findAllByIsAdminTrue();
}
