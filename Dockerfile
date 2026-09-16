# ==========================================
# Stage 1: Build Stage
# ==========================================
FROM maven:3.9-eclipse-temurin-17 AS builder

WORKDIR /build

# Önce bağımlılıkları indirip katman önbelleklemesini (layer caching) etkinleştiriyoruz
COPY pom.xml .
RUN mvn dependency:go-offline -B

# Kaynak kodları kopyalayıp paketi oluşturuyoruz
COPY src ./src
RUN mvn clean package -DskipTests

# ==========================================
# Stage 2: Runtime Stage
# ==========================================
FROM eclipse-temurin:17-jre-jammy

WORKDIR /app

# Güvenlik için non-root kullanıcı oluşturuyoruz
RUN groupadd -r spring && useradd -r -g spring spring
USER spring:spring

# Derlenen jar dosyasını kopyalıyoruz
COPY --from=builder /build/target/*.jar app.jar

# Uygulama portu
EXPOSE 8080

# Ortam değişkeni ile JVM opsiyonlarını yönetebilme esnekliği
ENV JAVA_OPTS=""

ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -jar app.jar"]
