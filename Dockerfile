# --- Java 17 JDK 기반 이미지 ---
FROM eclipse-temurin:17-jdk

# --- 작업 디렉토리 설정 ---
WORKDIR /app

# --- jar 파일 복사 ---
COPY build/libs/*.jar app.jar

# --- Backend 포트 ---
EXPOSE 8080

# --- 실행 명령 ---
ENTRYPOINT ["java", "-jar", "app.jar"]
