package com.example.dkburlshortener

import io.klogging.NoCoLogging
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.redis.connection.RedisConnectionFactory
import org.springframework.data.redis.core.StringRedisTemplate
import org.springframework.data.redis.support.atomic.RedisAtomicLong
import org.springframework.stereotype.Service


interface UrlShortener {
    fun shorten(url: String): String
}

@Service
class UrlShortenerService : UrlShortener {
    override fun shorten(url: String): String {
        return "http://localhost:8080/shortenedUrl"
    }
}

@Service
class RedisService(
    @Autowired val connectionFactory: RedisConnectionFactory,
    @Autowired val redisTemplate: StringRedisTemplate
) {
    fun uniqueKey(): String {
        val atomicLong = RedisAtomicLong(ID_KEY, connectionFactory)
        return atomicLong.incrementAndGet().toString(32)
    }

    fun writeValue(key: String, value: String) {
        redisTemplate.opsForValue().set(key, value)
    }

    fun readValue(key: String): String? {
        return redisTemplate.opsForValue().get(key)
    }

    companion object : NoCoLogging {
        const val ID_KEY = "all:urls:unique_id:key"
    }
}