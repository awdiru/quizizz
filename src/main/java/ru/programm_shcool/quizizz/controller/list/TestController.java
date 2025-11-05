package ru.programm_shcool.quizizz.controller.list;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.programm_shcool.quizizz.controller.AbstractController;
import ru.programm_shcool.quizizz.dto.elements.RenameElementDto;
import ru.programm_shcool.quizizz.dto.elements.test.TestDto;
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
            return getResponse(e.getMessage(), 400);
        }
    }

    @GetMapping("/get")
    public ResponseEntity<Object> getTest(@RequestParam String path) {
        try {
            return getOkResponse(testService.getTest(path));
        } catch (Exception e) {
            return getResponse(e.getMessage(), 400);
        }
    }

    @DeleteMapping("/remove")
    public ResponseEntity<Object> removeTest(@RequestParam String path) {
        try {
            testService.removeTest(path);
            return getStandardResponse("Test deleted");
        } catch (Exception e) {
            return getResponse(e.getMessage(), 400);
        }
    }

    @PatchMapping("/rename")
    public ResponseEntity<Object> renameTest(@RequestBody RenameElementDto testDto) {
        try {
            testService.renameTest(testDto);
            return getStandardResponse("Test renamed");
        } catch (Exception e) {
            return getResponse(e.getMessage(), 400);
        }
    }
}
