package com.example.dkburlshortener

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertDoesNotThrow
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.data.redis.core.StringRedisTemplate
import org.testcontainers.containers.GenericContainer

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class RedisServiceIntegrationTest {
    @Autowired
    private lateinit var redisService: RedisService

    @Autowired
    private lateinit var redisTemplate: StringRedisTemplate

    private val key = "TEST_KEY"
    private val value = "TEST_VALUE"

    @BeforeEach
    fun setUp() {
        // cleanup all keys
        redisTemplate.delete(redisTemplate.keys("*"))
    }

    @Test
    fun uniqueKey() {
        redisService.uniqueKey().also {
            println("Generated ID: $it")
            assertThat(it).isNotNull
        }
    }

    @Test
    fun writeValue() {
        assertDoesNotThrow { redisService.writeValue(key, value) }
    }

    @Test
    fun readValue() {
        redisService.readValue(key).also {
            assertThat(it).isNull()
            redisService.writeValue(key, value).also {
                assertThat(redisService.readValue(key)).isEqualTo(value)
            }
        }
    }

    companion object {
        @JvmStatic
        @BeforeAll
        fun init() {
            val redis = GenericContainer("redis:alpine")
                .withExposedPorts(6379)
            redis.start()
            System.setProperty("spring.data.redis.host", redis.host)
            System.setProperty("spring.data.redis.port", redis.getMappedPort(6379).toString())
        }
    }
}