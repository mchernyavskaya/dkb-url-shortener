package com.example.dkburlshortener

import io.klogging.config.DEFAULT_CONSOLE
import io.klogging.config.loggingConfiguration
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.boot.runApplication


@SpringBootApplication
@EnableConfigurationProperties(
    ApplicationProperties::class
)
class UrlShortenerApplication

@ConfigurationProperties("application")
data class ApplicationProperties(
    val host: String
)


fun main(args: Array<String>) {
    loggingConfiguration { DEFAULT_CONSOLE() }
    runApplication<UrlShortenerApplication>(*args)
}
