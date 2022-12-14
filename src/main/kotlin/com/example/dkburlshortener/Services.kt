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
        var currentId = atomicLong.incrementAndGet()
        if (currentId == 1L) {
            // if it's just now been created, set to initial value
            atomicLong.set(START_KEY)
            currentId = START_KEY
        }
        return currentId.toString(KEY_RADIX)
    }

    fun writeValue(key: String, value: String) {
        redisTemplate.opsForValue().set(key, value)
    }

    fun readValue(key: String): String? {
        return redisTemplate.opsForValue().get(key)
    }

    companion object : NoCoLogging {
        private const val ID_KEY = "all:urls:unique_id:key"
        private const val KEY_RADIX = 32
        const val START_KEY = 100000L
    }
}