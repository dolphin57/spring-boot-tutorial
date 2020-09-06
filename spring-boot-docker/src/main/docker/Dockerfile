FROM frolvlad/alpine-oraclejdk8:slim
VOLUME /tmp
ADD spring-boot-docker-0.0.1-SNAPSHOT.jar spring-boot-docker.jar
RUN sh -c 'touch/spring-boot-docker.jar'
ENV JAVA_OPTS=""
ENTRYPOINT [ "sh", "-c", "java $JAVA_OPTS -Djava.security.egd=file:/dev/./urandom -jar /spring-boot-docker.jar" ]