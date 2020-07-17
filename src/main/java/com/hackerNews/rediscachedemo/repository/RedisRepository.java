package com.hackerNews.rediscachedemo.repository;

import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.concurrent.TimeUnit;

@Repository
public class RedisRepository {

    private HashOperations hashOperations;

    private RedisTemplate redisTemplate;

    public RedisRepository(RedisTemplate redisTemplate){
        this.redisTemplate = redisTemplate;
        this.hashOperations = this.redisTemplate.opsForHash();
    }

    public void saveWithExpiry(String key, Object hashKey, Object hahValue){
        hashOperations.put(key, hashKey ,hahValue );
        this.redisTemplate.expire(key,10, TimeUnit.MINUTES);
    }

    public void save(String key, Object hashKey, Object hahValue){
        hashOperations.put(key, hashKey ,hahValue );
    }

    public List findAll(String key){
        return hashOperations.values(key);
    }



}
