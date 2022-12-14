package com.example.dkburlshortener

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/")
class UrlShortenerController(
    @Autowired val service: UrlShortenerService,
    @Autowired val properties: ApplicationProperties
) {
    @PostMapping
    fun shorten(@RequestBody longUrl: String = ""): ShortenerResponse {
        val key = service.shortenToKey(longUrl)
        val shortUrl = "${properties.host}/${key}"
        return ShortenerResponse(longUrl = longUrl, shortUrl = shortUrl)
    }

    @GetMapping("/{key}")
    fun expand(@PathVariable key: String): ResponseEntity<ShortenerResponse> {
        val shortUrl = "${properties.host}/${key}"
        val longUrl = service.expandKey(key) ?: return ResponseEntity(HttpStatus.NOT_FOUND)
        return ResponseEntity(ShortenerResponse(longUrl = longUrl, shortUrl = shortUrl), HttpStatus.OK)
    }

    data class ShortenerResponse(val longUrl: String, val shortUrl: String)
}