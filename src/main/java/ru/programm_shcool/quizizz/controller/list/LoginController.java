package ru.programm_shcool.quizizz.controller.list;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.programm_shcool.quizizz.annotation.PublicEndpoint;
import ru.programm_shcool.quizizz.controller.AbstractController;
import ru.programm_shcool.quizizz.dto.util.LoginDto;
import ru.programm_shcool.quizizz.dto.util.LoginResponse;
import ru.programm_shcool.quizizz.repository.TokenRepository;
import ru.programm_shcool.quizizz.service.EncryptionService;
import ru.programm_shcool.quizizz.service.RegisterService;

import javax.security.auth.login.LoginException;
import java.time.Duration;

@RestController
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class LoginController extends AbstractController {
    private final RegisterService registerService;
    private final EncryptionService encryptionService;
    private final TokenRepository tokenRepository;

    @PostMapping("/login")
    @PublicEndpoint
    public ResponseEntity<Object> login(@RequestBody LoginDto loginDto) {
        try {
            if (!registerService.validateTeacher(loginDto.getLogin(), loginDto.getPassword()))
                throw new LoginException("Invalid password");

            String token = encryptionService.generateRandomString();
            tokenRepository.saveToken(loginDto.getLogin(), token, Duration.ofHours(24));

            return getOkResponse(LoginResponse.builder()
                    .status(200)
                    .message("Login successful")
                    .username(loginDto.getLogin())
                    .token(token)
                    .build());

        } catch (Exception e) {
            return getResponse(e.getMessage(), 401);
        }
    }

    @PostMapping("/register")
    @PublicEndpoint
    public ResponseEntity<Object> register(@RequestBody LoginDto loginDto) {
        try {
            registerService.registerTeacher(loginDto);
            return getStandardResponse("Register successful. Wait for the employer's confirmation");
        } catch (Exception e) {
            return getResponse(e.getMessage(), 401);
        }
    }

    @GetMapping("/confirmed")
    @PublicEndpoint
    public ResponseEntity<Object> confirmRegister(@RequestParam String login,
                                                  @RequestParam String token) {
        try {
            registerService.saveTeacher(login, token);
            return getStandardResponse("User registration " + login + " has been successfully completed");
        } catch (Exception e) {
            return getResponse(e.getMessage(), 401);
        }
    }

    @PostMapping("/update")
    @PublicEndpoint
    public ResponseEntity<Object> confirmedRegister(@RequestBody LoginDto loginDto) {
        try {
            registerService.updateTeacher(loginDto);
            return getStandardResponse("Updating successful");
        } catch (Exception e) {
            return getResponse(e.getMessage(), 401);
        }
    }
}