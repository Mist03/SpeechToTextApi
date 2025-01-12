FROM gradle:8.7.0-jdk17-alpine AS gradle
COPY --chown=gradle:gradle . /home/gradle/
RUN apk add --no-cache file
WORKDIR /home/gradle/src/main/resources/models
RUN wget "https://drive.usercontent.google.com/download?id=1_gHCwBvBC8JUhvDJzw3VE8xDul_alTPH&export=download&authuser=0&confirm=t&uuid=b201dfb2-7c8c-4e44-a241-3b964e29b456&at=AIrpjvPIm6U2e7DHp9_2zVBeryL8:1736625568498" -O ru.lm && \
                                                                                                                                                                                                                                       file ru.lm

WORKDIR /home/gradle
RUN gradle bootJar

FROM alpine/java:17-jdk AS java
ENV FFMPEG_PATH="/usr/bin"
WORKDIR /home/java/
COPY --from=gradle /home/gradle/build/libs/*.jar /home/java/SpeechToTextApi.jar
RUN apk add --no-cache ffmpeg
CMD ["java", "-jar", "SpeechToTextApi.jar"]
