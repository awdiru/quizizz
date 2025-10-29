package ru.programm_shcool.quizizz.controller.list;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.programm_shcool.quizizz.annotation.PublicEndpoint;
import ru.programm_shcool.quizizz.controller.AbstractController;
import ru.programm_shcool.quizizz.dto.util.LoginDto;
import ru.programm_shcool.quizizz.dto.util.LoginResponse;
import ru.programm_shcool.quizizz.repository.TokenRepository;
import ru.programm_shcool.quizizz.service.EncryptionService;
import ru.programm_shcool.quizizz.service.PasswordService;

import javax.security.auth.login.LoginException;
import java.time.Duration;

@RestController
@RequestMapping("/login")
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class LoginController extends AbstractController {
    private final PasswordService passwordService;
    private final EncryptionService encryptionService;
    private final TokenRepository tokenRepository;

    @PostMapping()
    @PublicEndpoint
    public ResponseEntity<Object> login(@RequestBody LoginDto loginDto) {
        try {
            String currentPassword = passwordService.getPassword();
            if (currentPassword.equals(loginDto.getPassword())) {
                String token = encryptionService.generateRandomString();

                // Сохраняем токен в Redis на 24 часа с ключом = username
                tokenRepository.saveToken(loginDto.getName(), token, Duration.ofHours(24));

                return getResponse(LoginResponse.builder()
                        .status(200)
                        .message("Success")
                        .token(token)
                        .build());
            }
            throw new LoginException("Invalid password");
        } catch (Exception e) {
            return getErrorResponse(e.getMessage(), 401);
        }
    }
}