package ru.programm_shcool.quizizz.controller.list;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.programm_shcool.quizizz.controller.AbstractController;
import ru.programm_shcool.quizizz.dto.directory.DirectoryDto;
import ru.programm_shcool.quizizz.service.DirectoryService;

@RestController
@RequestMapping("/directory")
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class DirectoryController extends AbstractController {
    private final DirectoryService directoryService;

    @PostMapping("/create")
    public ResponseEntity<Object> createDirectory(@RequestBody DirectoryDto directoryDto) {
        try {
            directoryService.save(directoryDto);
            return getStandardResponse("Directory created");
        } catch (Exception e) {
            return getErrorResponse(e.getMessage(), 400);
        }
    }

    @GetMapping("/get")
    public ResponseEntity<Object> getDirectory(@RequestParam String path) {
        try {
            DirectoryDto directoryDto = directoryService.getDirectory(path);
            return getResponse(directoryDto);
        } catch (Exception e) {
            return getErrorResponse(e.getMessage(), 400);
        }
    }

    @DeleteMapping("/remove")
    public ResponseEntity<Object> removeDirectory(@RequestParam String path) {
        try {
            directoryService.removeDirectory(path);
            return getStandardResponse("Directory removed");
        } catch (Exception e) {
            return getErrorResponse(e.getMessage(), 400);
        }
    }
}
