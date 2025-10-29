package ru.programm_shcool.quizizz.repository;

import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;

import java.time.Duration;
import java.util.concurrent.TimeUnit;

@Repository
public class TokenRepository {

    private final RedisTemplate<String, String> redisTemplate;
    private static final String KEY_PREFIX = "auth_token:";

    public TokenRepository(RedisTemplate<String, String> redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    public void saveToken(String username, String token, Duration ttl) {
        String key = KEY_PREFIX + username;
        redisTemplate.opsForValue().set(key, token, ttl.toMinutes(), TimeUnit.MINUTES);
    }

    public String getToken(String username) {
        String key = KEY_PREFIX + username;
        return redisTemplate.opsForValue().get(key);
    }

    public void deleteToken(String username) {
        String key = KEY_PREFIX + username;
        redisTemplate.delete(key);
    }

    public boolean existsToken(String username) {
        String key = KEY_PREFIX + username;
        return Boolean.TRUE.equals(redisTemplate.hasKey(key));
    }

    public boolean validateToken(String username, String token) {
        String storedToken = getToken(username);
        return storedToken != null && storedToken.equals(token);
    }
}