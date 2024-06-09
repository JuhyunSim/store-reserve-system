package com.zerobase.domain.redis;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;

@Service
@RequiredArgsConstructor
@Slf4j
public class RedisClient {

    private final RedisTemplate<String, Object> redisTemplate;
    private final ObjectMapper objectMapper;

    public <T> T get(Long key, Class<T> classType){
        return get(key.toString(), classType);
    }

    private <T> T get(String key, Class<T> classType){
        String redisValue = (String) redisTemplate.opsForValue().get(key);
        if (ObjectUtils.isEmpty(redisValue)) {
            return null;
        } else {
            try {
                return objectMapper.readValue(redisValue, classType);
            } catch (JsonProcessingException e) {
                log.info("parsing error: {}", e.getMessage());
                return null;
            }
        }
    }

    public void put (Long key, Waiting waiting){
        put(key.toString(), waiting);
    }

    private void put (String key, Waiting waiting){
        try {
            redisTemplate.opsForValue().set(key, objectMapper.writeValueAsString(waiting));
        } catch (JsonProcessingException e) {
            throw new RuntimeException("대기 등록에 실패했습니다.");
        }
    }

}
