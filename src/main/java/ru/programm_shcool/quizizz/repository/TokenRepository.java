package ru.programm_shcool.quizizz.repository;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;

import java.time.Duration;
import java.util.concurrent.TimeUnit;

@Repository
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class TokenRepository {
    private final RedisTemplate<String, String> redisTemplate;
    private static final String KEY_PREFIX = "auth_token:";

    public void saveToken(String username, String token, Duration ttl) {
        String key = KEY_PREFIX + username;
        redisTemplate.opsForValue().set(key, token, ttl.toMinutes(), TimeUnit.MINUTES);
    }

    public String getToken(String username) {
        String key = KEY_PREFIX + username;
        String token = redisTemplate.opsForValue().get(key);
        if (token == null)
            throw new IllegalArgumentException("Unconfirmed user");
        return token;
    }

    public boolean validateToken(String username, String token) {
        String storedToken = getToken(username);
        return storedToken != null && storedToken.equals(token);
    }
}