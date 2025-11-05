package ru.programm_shcool.quizizz.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import ru.programm_shcool.quizizz.entity.Answer;

@Repository
public interface AnswerRepository extends JpaRepository<Answer, Long> {
    @Modifying
    @Query(value = """
            delete from answers 
            where question_id in (select id from questions where test_id = :testId)
            """, nativeQuery = true)
    void deleteAnswersByTestId(@Param("testId") Long testId);
}
