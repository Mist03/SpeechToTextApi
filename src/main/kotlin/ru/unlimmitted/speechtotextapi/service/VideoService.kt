package ru.unlimmitted.speechtotextapi.service

import com.github.kokorin.jaffree.StreamType
import com.github.kokorin.jaffree.ffmpeg.FFmpeg
import com.github.kokorin.jaffree.ffmpeg.UrlInput
import com.github.kokorin.jaffree.ffmpeg.UrlOutput
import edu.cmu.sphinx.api.Configuration
import edu.cmu.sphinx.api.StreamSpeechRecognizer
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service
import org.springframework.util.ResourceUtils
import org.springframework.web.multipart.MultipartFile
import java.io.File
import java.nio.file.Path
import java.nio.file.Paths

@Service
class VideoService(
    @Value("\${ffmpeg.path}") private val ffmpegPath: String,
) {
    fun convertVideoToText(file: MultipartFile): String {
        val tempFile = File.createTempFile("temp", ".mp4")
        file.transferTo(tempFile)
        val audioFile = extractAudio(tempFile)
        val text = transcribeAudio(audioFile)
        tempFile.delete()
        audioFile.delete()
        return text
    }

    fun extractAudio(videoFile: File): File {
        val ffmpegPath: Path = Paths.get(ffmpegPath)
        val audioFile = File(videoFile.parentFile, "${videoFile.nameWithoutExtension}.wav")

        FFmpeg.atPath(ffmpegPath)
            .addInput(UrlInput.fromPath(videoFile.toPath()))
            .addOutput(
                UrlOutput.toPath(audioFile.toPath())
                    .setCodec(StreamType.AUDIO, "pcm_s16le")
                    .addArguments("-ar", "16000")
                    .addArguments("-af", "highpass=f=200, lowpass=f=3000")
                    .addArguments("-ac", "1")
                    .disableStream(StreamType.VIDEO)
            )
            .execute()

        return audioFile
    }

    private fun transcribeAudio(audioFile: File): String {
        val configuration = Configuration().apply {
            acousticModelPath = "classpath:models/zero_ru"
            dictionaryPath = "classpath:models/ru.dic"
            languageModelPath = "classpath:models/ru.lm"
        }

        val recognizer = StreamSpeechRecognizer(configuration)
        val inputStream = audioFile.inputStream()

        recognizer.startRecognition(inputStream)
        val result = buildString {
            while (true) {
                val hypothesis = recognizer.result ?: break
                append(hypothesis.hypothesis).append(" ")
            }
        }
        recognizer.stopRecognition()
        return result.trim()
    }
}