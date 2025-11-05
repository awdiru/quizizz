package ru.programm_shcool.quizizz.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import ru.programm_shcool.quizizz.entity.Question;

@Repository
public interface QuestionRepository extends JpaRepository<Question, Long> {
    @Modifying
    @Query(value = """
            delete from questions where test_id = :testId
            """, nativeQuery = true)
    void deleteQuestionsByTestId(@Param("testId") Long testId);
}
