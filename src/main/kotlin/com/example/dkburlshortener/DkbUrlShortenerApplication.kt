package com.example.dkburlshortener

import io.klogging.config.DEFAULT_CONSOLE
import io.klogging.config.loggingConfiguration
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class DkbUrlShortenerApplication

fun main(args: Array<String>) {
    loggingConfiguration { DEFAULT_CONSOLE() }
    runApplication<DkbUrlShortenerApplication>(*args)
}
