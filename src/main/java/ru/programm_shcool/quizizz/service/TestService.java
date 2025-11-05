package ru.programm_shcool.quizizz.service;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.programm_shcool.quizizz.dto.answer.AnswerDto;
import ru.programm_shcool.quizizz.dto.elements.RenameElementDto;
import ru.programm_shcool.quizizz.dto.elements.test.TestDto;
import ru.programm_shcool.quizizz.dto.question.QuestionDto;
import ru.programm_shcool.quizizz.entity.*;
import ru.programm_shcool.quizizz.repository.AnswerRepository;
import ru.programm_shcool.quizizz.repository.ElementRepository;
import ru.programm_shcool.quizizz.repository.QuestionRepository;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class TestService {
    private final QuestionRepository questionRepository;
    private final AnswerRepository answerRepository;
    private final ElementRepository elementRepository;

    private final ElementService elementService;

    public void createTest(TestDto testDto) {
        Directory directory = elementService.getParent(testDto.getPath());
        Test test = Test.builder()
                .name(elementService.getName(testDto.getPath()))
                .parent(directory)
                .type(ElementType.FILE)
                .created(LocalDateTime.now())
                .build();
        Test saved = elementRepository.save(test);

        for (QuestionDto questionDto : testDto.getQuestions())
            saveQuestion(saved, questionDto);
    }

    public TestDto getTest(String path) {
        Test test = getTestToPath(path);
        List<QuestionDto> questions = test.getQuestions().stream()
                .map(q ->
                        QuestionDto.builder()
                                .question(q.getQuestion())
                                .answers(q.getAnswers().stream()
                                        .map(a ->
                                                AnswerDto.builder()
                                                        .answer(a.getAnswer())
                                                        .isRight(a.getIsRight())
                                                        .build())
                                        .toList())
                                .build())
                .toList();

        return TestDto.builder()
                .path(path)
                .questions(questions)
                .build();
    }

    @Transactional
    public void removeTest(String path) {
        Test test = getTestToPath(path);
        removeTest(test);
    }

    @Transactional
    public void removeTest(Test test) {
        Long testId = test.getId();

        Directory parent = (Directory) test.getParent();
        if (parent != null) parent.getChildren().remove(test);

        test.setParent(null);
        elementRepository.saveAndFlush(test);

        answerRepository.deleteAnswersByTestId(testId);
        questionRepository.deleteQuestionsByTestId(testId);
        elementRepository.deleteById(testId);
    }

    @Transactional
    public void renameTest(RenameElementDto renameTestDto) {
        Test test = getTestToPath(elementService.getName(renameTestDto.getPath()));
        test.setName(renameTestDto.getNewName());
        elementRepository.save(test);
    }

    private Test getTestToPath(String path) {
        Element element = elementService.get(path);
        if (!(element instanceof Test test))
            throw new IllegalArgumentException("Element " + path + " is not a Test");
        return test;
    }

    private void saveQuestion(Test test, QuestionDto questionDto) {
        Question question = Question.builder()
                .test(test)
                .question(questionDto.getQuestion())
                .build();

        Question saved = questionRepository.save(question);

        for (AnswerDto answerDto : questionDto.getAnswers())
            saveAnswer(saved, answerDto);
    }

    private void saveAnswer(Question question, AnswerDto answerDto) {
        Answer answer = Answer.builder()
                .answer(answerDto.getAnswer())
                .question(question)
                .isRight(answerDto.isRight())
                .build();

        answerRepository.save(answer);
    }
}
