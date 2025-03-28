FROM openjdk:21-jdk-slim

WORKDIR /app

COPY ./build/libs/*.jar /app/pdfpedia.jar

ENTRYPOINT ["java", "-jar", "pdfpedia.jar"]
