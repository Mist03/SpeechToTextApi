package ru.unlimmitted.speechtotextapi.controller

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.multipart.MultipartFile
import ru.unlimmitted.speechtotextapi.service.VideoService

@RestController
@RequestMapping("/api/v1")
class ApiController(
    @Autowired val videoService: VideoService,
) {

    @PostMapping("/video-to-text", consumes = ["multipart/form-data"])
    fun convertVideoToText(@RequestParam("file") file: MultipartFile): ResponseEntity<String> {
        val text = videoService.convertVideoToText(file)
        return ResponseEntity.ok(text)
    }
}