FROM armv7/armhf-java8
MAINTAINER Yep Choon <yc.wearn@gmail.com>

ENV JAVA_OPTS="-Xmx512m -Xms128m"

COPY ["./qemu-arm-static", "/usr/bin/"]

VOLUME /tmp
ARG DEPENDENCY=target/dependency
COPY ${DEPENDENCY}/BOOT-INF/lib /app/lib
COPY ${DEPENDENCY}/META-INF /app/META-INF
COPY ${DEPENDENCY}/BOOT-INF/classes /app

RUN rm -rf /usr/bin/qemu-arm-static

EXPOSE 8080

ENTRYPOINT java ${JAVA_OPTS} -cp "/app:/app/lib/*" "com.carrotlicious.iot.slackbot.Application"