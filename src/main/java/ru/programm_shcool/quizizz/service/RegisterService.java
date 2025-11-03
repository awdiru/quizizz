package ru.programm_shcool.quizizz.service;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import ru.programm_shcool.quizizz.dto.teacher.UnconfirmedTeacher;
import ru.programm_shcool.quizizz.dto.util.LoginDto;
import ru.programm_shcool.quizizz.entity.Teacher;
import ru.programm_shcool.quizizz.repository.RegisterRepository;
import ru.programm_shcool.quizizz.repository.UnconfirmedTeacherRepository;

import java.time.Duration;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class RegisterService {
    private final EncryptionService encryptionService;
    private final RegisterRepository registerRepository;
    private final UnconfirmedTeacherRepository unconfirmedTeacherRepository;
    private final JavaMailSender mailSender;

    @Value("${app.address}")
    private String serverAddress;

    @Value("${spring.mail.username}")
    private String fromEmail;

    public boolean validateTeacher(String login, String password) {
        Teacher teacher = registerRepository.findByLogin(login)
                .orElseThrow(() -> new IllegalArgumentException("Invalid login"));

        String decryptedPassword = encryptionService.decrypt(teacher.getPassword(), login);
        return decryptedPassword.equals(password);
    }

    public void registerTeacher(LoginDto loginDto) {
        try {
            if (loginDto.getLogin().length() < 2 || loginDto.getLogin().length() > 31)
                throw new IllegalArgumentException("Login length must be between 2 and 30 characters");

            Teacher teacher = Teacher.builder()
                    .login(loginDto.getLogin())
                    .password(encryptionService.encrypt(loginDto.getPassword(), loginDto.getLogin()))
                    .email(encryptionService.encrypt(loginDto.getEmail(), loginDto.getLogin()))
                    .build();

            String temporaryConfirmationToken = encryptionService.generateRandomString();
            unconfirmedTeacherRepository.saveUnconfirmedTeacher(teacher, temporaryConfirmationToken, Duration.ofHours(24));

            List<String> adminEmails = registerRepository.findAllByIsAdminTrue()
                    .stream()
                    .map(t -> encryptionService.decrypt(t.getEmail(), t.getLogin()))
                    .toList();

            if (!adminEmails.isEmpty()) {
                sendConfirmationEmailsToAdmins(adminEmails, loginDto.getLogin(), temporaryConfirmationToken);
            }

        } catch (Exception e) {
            throw new IllegalArgumentException("Registration error: " + e.getMessage(), e);
        }
    }

    @Async
    protected void sendConfirmationEmailsToAdmins(List<String> adminEmails, String teacherLogin, String token) {
        String confirmationUrl = serverAddress + "/register/registration_confirmed.html?login=" + teacherLogin + "&token=" + token;

        String subject = "Подтверждение регистрации нового учителя";
        String text = "Для подтверждения регистрации пользователя " + teacherLogin +
                " перейдите по ссылке: " + confirmationUrl +
                "\n\nСсылка действует в течение 24 часов." +
                "\nЕсли вы не хотите подтверждать регистрацию пользователя - игнорируйте это письмо.";

        for (String adminEmail : adminEmails) {
            try {
                SimpleMailMessage message = new SimpleMailMessage();
                message.setFrom(fromEmail);
                message.setTo(adminEmail);
                message.setSubject(subject);
                message.setText(text);

                mailSender.send(message);

            } catch (Exception e) {
                System.err.println("Failed to send email to " + adminEmail + ": " + e.getMessage());
            }
        }
    }

    public void saveTeacher(String login, String token) {
        try {
            UnconfirmedTeacher unconfirmedTeacher = unconfirmedTeacherRepository.getUnconfirmedTeacher(login);
            if (unconfirmedTeacher == null)
                throw new IllegalArgumentException("Invalid login");
            if (!unconfirmedTeacher.getTemporaryConfirmationToken().equals(token))
                throw new IllegalArgumentException("Invalid token");

            unconfirmedTeacherRepository.deleteToken(login);
            Teacher teacher = unconfirmedTeacher.getTeacher();
            registerRepository.save(teacher);
        } catch (Exception e) {
            throw new IllegalArgumentException("User Save error: " + e.getMessage(), e);
        }
    }

    public void updateTeacher(LoginDto loginDto) {
        Teacher teacher = registerRepository.findByLogin(loginDto.getLogin())
                .orElse(Teacher.builder().login(loginDto.getLogin()).build());

        teacher.setPassword(encryptionService.encrypt(loginDto.getPassword(), loginDto.getLogin()));
        teacher.setEmail(encryptionService.encrypt(loginDto.getEmail(), loginDto.getLogin()));
        teacher.setIsAdmin(loginDto.getIsAdmin());

        registerRepository.save(teacher);
    }
}
