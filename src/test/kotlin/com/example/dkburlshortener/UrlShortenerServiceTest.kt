package com.example.dkburlshortener

import io.mockk.every
import io.mockk.justRun
import io.mockk.mockk
import io.mockk.verify
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

class UrlShortenerServiceTest {

    private val redisService = mockk<RedisService>()
    private val key = "testKey"

    private val longUrl = "https://www.example.com?query=someQueryHere"

    private val service = UrlShortenerService(redisService)

    @BeforeEach
    fun setUp() {
        every { redisService.uniqueKey() } returns key
        every { redisService.readValue(key) } returns longUrl
        justRun { redisService.writeValue(key, longUrl) }
    }

    @Test
    fun shorten() {
        service.shortenToKey(longUrl).also {
            verify { redisService.uniqueKey() }
            verify { redisService.writeValue(key, longUrl) }

            assertThat(it).isEqualTo(key)
        }
    }

    @Test
    fun expand() {
        service.expandKey(key).also {
            verify { redisService.readValue(key) }

            assertThat(it).isEqualTo(longUrl)
        }
    }
}