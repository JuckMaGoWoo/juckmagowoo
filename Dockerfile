# 1단계: 빌드 단계
FROM eclipse-temurin:17-jdk AS builder
WORKDIR /app
COPY . .
RUN ./gradlew build -x test

# 2단계: 실행 단계
FROM eclipse-temurin:17-jdk AS runtime
WORKDIR /app
COPY --from=builder /app/build/libs/*.jar app.jar
ENTRYPOINT ["java", "-jar", "app.jar"]