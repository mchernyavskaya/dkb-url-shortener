package com.example.dkburlshortener

import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

class UrlShortenerControllerTest {

    private val properties = mockk<ApplicationProperties>()
    private val service = mockk<UrlShortenerService>()
    private val key = "testKey"
    private val customHost = "https://short.ly"
    private val shortUrl = "${customHost}/${key}"

    private val longUrl = "https://www.example.com?query=someQueryHere"

    private val controller = UrlShortenerController(service, properties)

    @BeforeEach
    fun setUp() {
        every { service.shortenToKey(longUrl) } returns key
        every { service.expandKey(key) } returns longUrl
        every { properties.host } returns customHost
    }

    @Test
    fun shorten() {
        controller.shorten(longUrl).also {
            verify { service.shortenToKey(longUrl) }

            assertThat(it.shortUrl).isEqualTo(shortUrl)
            assertThat(it.longUrl).isEqualTo(longUrl)
        }
    }

    @Test
    fun expand() {
        controller.expand(key).also {
            verify { service.expandKey(key) }

            assertThat(it.body!!.longUrl).isEqualTo(longUrl)
            assertThat(it.body!!.shortUrl).isEqualTo(shortUrl)
        }
    }
}