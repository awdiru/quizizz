package ru.programm_shcool.quizizz.controller.list;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.programm_shcool.quizizz.controller.AbstractController;
import ru.programm_shcool.quizizz.dto.util.LoginDto;

@RestController
@RequestMapping("/login")
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class LoginController extends AbstractController {

    @PostMapping()
    public ResponseEntity<Object> login(@RequestBody LoginDto loginDto) {
        return getResponse(loginDto);
    }
}
