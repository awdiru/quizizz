package ru.programm_shcool.quizizz.initializers;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import ru.programm_shcool.quizizz.entity.User;
import ru.programm_shcool.quizizz.repository.TeacherRepository;
import ru.programm_shcool.quizizz.service.EncryptionService;

@Component
@RequiredArgsConstructor
public class AdminInitializer {
    private final TeacherRepository teacherRepository;
    private final EncryptionService encryptionService;

    @Value("${app.admin_login}")
    private String adminLogin;
    @Value("${app.admin_password}")
    private String adminPassword;
    @Value("${app.admin_email}")
    private String adminEmail;

    @EventListener(ContextRefreshedEvent.class)
    public void initAdmin() {
        if (teacherRepository.findByLogin(adminLogin).isEmpty()) {
            User admin = User.builder()
                    .login(adminLogin)
                    .password(encryptionService.encrypt(adminPassword, adminLogin))
                    .isAdmin(true)
                    .email(encryptionService.encrypt(adminEmail, adminLogin))
                    .build();

            teacherRepository.save(admin);
        }
    }
}