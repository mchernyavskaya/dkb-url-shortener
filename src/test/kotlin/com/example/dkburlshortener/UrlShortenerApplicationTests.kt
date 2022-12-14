package com.example.dkburlshortener

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.web.server.LocalServerPort

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class UrlShortenerApplicationTests {
    @LocalServerPort
    private val port: Int? = null

    @Autowired
    private lateinit var controller: UrlShortenerController

    @Test
    fun contextLoads() {
        assertThat(controller).isNotNull
    }
}
