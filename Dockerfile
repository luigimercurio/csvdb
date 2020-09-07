FROM openjdk:8
WORKDIR /
RUN addgroup -S spring && adduser -S spring -G spring
USER spring:spring
COPY build/libs/*.jar /app.jar
ENTRYPOINT ["java","-jar","/app.jar"]
