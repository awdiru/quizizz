package ru.programm_shcool.quizizz.repository;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;
import ru.programm_shcool.quizizz.dto.users.UnconfirmedUserDto;
import ru.programm_shcool.quizizz.entity.User;

import java.time.Duration;
import java.util.concurrent.TimeUnit;

@Repository
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class UnconfirmedTeacherRepository {
    private final RedisTemplate<String, String> redisTemplate;
    private static final String KEY_PREFIX = "confirmed_token:";
    private final ObjectMapper objectMapper = new ObjectMapper();

    public void saveUnconfirmedTeacher(User user, String token, Duration ttl) throws JsonProcessingException {
        String key = KEY_PREFIX + user.getLogin();
        UnconfirmedUserDto unconfirmedUserDto = UnconfirmedUserDto.builder()
                .temporaryConfirmationToken(token)
                .user(user)
                .build();
        String json = objectMapper.writeValueAsString(unconfirmedUserDto);
        redisTemplate.opsForValue().set(key, json, ttl.toMinutes(), TimeUnit.MINUTES);
    }

    public UnconfirmedUserDto getUnconfirmedTeacher(String username) throws JsonProcessingException {
        String key = KEY_PREFIX + username;
        String json = redisTemplate.opsForValue().get(key);
        return objectMapper.readValue(json, UnconfirmedUserDto.class);
    }

    public void deleteToken(String username) {
        String key = KEY_PREFIX + username;
        redisTemplate.delete(key);
    }
}
