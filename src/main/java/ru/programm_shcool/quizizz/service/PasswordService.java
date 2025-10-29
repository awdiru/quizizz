package ru.programm_shcool.quizizz.service;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.programm_shcool.quizizz.entity.Password;
import ru.programm_shcool.quizizz.repository.PasswordRepository;

import java.util.List;

@Service
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class PasswordService {
    private static final String STANDARD_PASSWORD = "StandardPassword";
    private final PasswordRepository passwordRepository;
    private final EncryptionService encryptionService;

    public String getPassword() {
        List<Password> passwords = passwordRepository.findAll();
        if (passwords.isEmpty()) setPassword(STANDARD_PASSWORD);
        String encryptedPass = (passwordRepository.findAll().getLast().getPassword());
        return encryptionService.decrypt(encryptedPass);
    }

    public void setPassword(String password) {
        String encryptedPassword = encryptionService.encrypt(password);
        passwordRepository.save(Password.builder()
                .password(encryptedPassword)
                .build());
    }
}
