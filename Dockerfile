FROM gradle:8.7.0-jdk17-alpine AS gradle
COPY --chown=gradle:gradle . /home/gradle/
RUN apk add --no-cache file
WORKDIR /home/gradle/src/main/resources/models
RUN wget "https://dl.ravel57.ru/0HjiIyeGW5VLnuGRtB!aObF!Cw-KGpppsVZ!K-1N" -O ru.lm && \
                                                                                    file ru.lm
WORKDIR /home/gradle
RUN gradle bootJar

FROM alpine/java:17-jdk AS java
WORKDIR /home/java/
COPY --from=gradle /home/gradle/build/libs/*.jar /home/java/SpeechToTextApi.jar
RUN apk add --no-cache ffmpeg
CMD ["java", "-jar", "SpeechToTextApi.jar"]
