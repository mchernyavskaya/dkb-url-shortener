package com.example.dkburlshortener

import io.klogging.NoCoLogging
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.redis.connection.RedisConnectionFactory
import org.springframework.data.redis.core.StringRedisTemplate
import org.springframework.data.redis.support.atomic.RedisAtomicLong
import org.springframework.stereotype.Service


interface UrlShortener {
    /**
     * Get the short key based on the long url
     */
    fun shortenToKey(longUrl: String): String

    /**
     * Get the long URL based on the short key
     */
    fun expandKey(key: String): String?
}

@Service
class UrlShortenerService(
    @Autowired private val redisService: RedisService
) : UrlShortener {
    override fun shortenToKey(longUrl: String): String {
        val key = redisService.uniqueKey()
        redisService.writeValue(key, longUrl)
        return key
    }

    override fun expandKey(key: String): String? {
        return redisService.readValue(key)
    }
}

@Service
class RedisService(
    @Autowired private val connectionFactory: RedisConnectionFactory,
    @Autowired private val redisTemplate: StringRedisTemplate
) {
    fun uniqueKey(): String {
        val atomicLong = RedisAtomicLong(ID_KEY, connectionFactory)
        return atomicLong.incrementAndGet().toString(KEY_RADIX)
    }

    fun writeValue(key: String, value: String) {
        redisTemplate.opsForValue().set(key, value)
    }

    fun readValue(key: String): String? {
        return redisTemplate.opsForValue().get(key)
    }

    companion object : NoCoLogging {
        const val ID_KEY = "all:urls:unique_id:key"
        const val KEY_RADIX = 32
    }
}