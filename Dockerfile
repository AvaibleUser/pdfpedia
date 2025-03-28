FROM gcr.io/distroless/java21-debian12

WORKDIR /app

COPY ./build/libs/*.jar /app/pdfpedia.jar

ENTRYPOINT ["java", "-jar", "pdfpedia.jar"]
