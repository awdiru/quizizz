package ru.programm_shcool.quizizz.service;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.programm_shcool.quizizz.dto.answer.AnswerDto;
import ru.programm_shcool.quizizz.dto.question.QuestionDto;
import ru.programm_shcool.quizizz.dto.test.TestDto;
import ru.programm_shcool.quizizz.entity.Answer;
import ru.programm_shcool.quizizz.entity.Directory;
import ru.programm_shcool.quizizz.entity.Question;
import ru.programm_shcool.quizizz.entity.Test;
import ru.programm_shcool.quizizz.repository.AnswerRepository;
import ru.programm_shcool.quizizz.repository.QuestionRepository;
import ru.programm_shcool.quizizz.repository.TestRepository;

import java.util.List;

@Service
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class TestService {
    private final TestRepository testRepository;
    private final QuestionRepository questionRepository;
    private final AnswerRepository answerRepository;
    private final DirectoryService directoryService;

    public void createTest(TestDto testDto) {
        Directory directory = directoryService.getParent(testDto.getPath(), directoryService.getStartDirectory());
        Test test = Test.builder()
                .name(directoryService.getName(testDto.getPath()))
                .directory(directory)
                .build();
        Test saved = testRepository.save(test);

        for (QuestionDto questionDto : testDto.getQuestions())
            saveQuestion(saved, questionDto);
    }

    public TestDto getTest(String path) {
        Directory directory = directoryService.getParent(path, directoryService.getStartDirectory());

        Test test = testRepository.findByNameAndDirectory(directoryService.getName(path), directory)
                .orElseThrow(() -> new RuntimeException("Test not found"));

        List<QuestionDto> questions = test.getQuestions().stream()
                .map(q ->
                        QuestionDto.builder()
                                .question(q.getQuestion())
                                .answers(q.getAnswers().stream()
                                        .map(a ->
                                                AnswerDto.builder()
                                                        .number(a.getNumber())
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
                .number(answerDto.getNumber())
                .answer(answerDto.getAnswer())
                .question(question)
                .isRight(answerDto.isRight())
                .build();

        answerRepository.save(answer);
    }
}
