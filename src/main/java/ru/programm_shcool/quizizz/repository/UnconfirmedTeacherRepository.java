package ru.programm_shcool.quizizz.repository;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;
import ru.programm_shcool.quizizz.dto.teacher.UnconfirmedTeacher;
import ru.programm_shcool.quizizz.entity.Teacher;

import java.time.Duration;
import java.util.concurrent.TimeUnit;

@Repository
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class UnconfirmedTeacherRepository {
    private final RedisTemplate<String, String> redisTemplate;
    private static final String KEY_PREFIX = "confirmed_token:";
    private final ObjectMapper objectMapper = new ObjectMapper();

    public void saveUnconfirmedTeacher(Teacher teacher, String token, Duration ttl) throws JsonProcessingException {
        String key = KEY_PREFIX + teacher.getLogin();
        UnconfirmedTeacher unconfirmedTeacher = UnconfirmedTeacher.builder()
                .temporaryConfirmationToken(token)
                .teacher(teacher)
                .build();
        String json = objectMapper.writeValueAsString(unconfirmedTeacher);
        redisTemplate.opsForValue().set(key, json, ttl.toMinutes(), TimeUnit.MINUTES);
    }

    public UnconfirmedTeacher getUnconfirmedTeacher(String username) throws JsonProcessingException {
        String key = KEY_PREFIX + username;
        String json = redisTemplate.opsForValue().get(key);
        return objectMapper.readValue(json, UnconfirmedTeacher.class);
    }
}
