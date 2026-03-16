FROM eclipse-temurin:17-jdk
WORKDIR /app
COPY src/ ./src/
COPY static/ ./static/
RUN mkdir out && javac src/*.java -d out/
EXPOSE 8080
CMD ["java", "-cp", "out", "Main"]
