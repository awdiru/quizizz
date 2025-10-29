package ru.programm_shcool.quizizz.controller.list;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.programm_shcool.quizizz.controller.AbstractController;
import ru.programm_shcool.quizizz.dto.test.TestDto;
import ru.programm_shcool.quizizz.service.TestService;

@RestController
@RequestMapping("/tests")
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class TestController extends AbstractController {
    private final TestService testService;

    @PostMapping("/create")
    public ResponseEntity<Object> createTest(@RequestBody TestDto testDto) {
        try {
            testService.createTest(testDto);
            return getStandardResponse("Test created");
        } catch (Exception e) {
            return getErrorResponse(e.getMessage(), 400);
        }
    }

    @GetMapping("/get")
    public ResponseEntity<Object> getTest(@RequestParam String path) {
        try {
            return getResponse(testService.getTest(path));
        } catch (Exception e) {
            return getErrorResponse(e.getMessage(), 400);
        }
    }
}
