FROM eclipse-temurin:21-jdk-alpine as build
WORKDIR /workspace/app

# Copy Maven wrapper and pom.xml
COPY mvnw .
COPY .mvn .mvn
COPY pom.xml .
RUN chmod +x ./mvnw

# Cache Maven dependencies
RUN ./mvnw dependency:go-offline -B

# Copy source code
COPY src src

# Build the application
RUN ./mvnw package -DskipTests
RUN mkdir -p target/dependency && (cd target/dependency; jar -xf ../*.jar)

# Production stage
FROM eclipse-temurin:21-jre-alpine
VOLUME /tmp
ARG DEPENDENCY=/workspace/app/target/dependency

# Copy the dependencies
COPY --from=build ${DEPENDENCY}/BOOT-INF/lib /app/lib
COPY --from=build ${DEPENDENCY}/META-INF /app/META-INF
COPY --from=build ${DEPENDENCY}/BOOT-INF/classes /app

# Set environment variables
ENV AWS_REGION=us-east-1
ENV AWS_ACCESS_KEY=${AWS_ACCESS_KEY_ID}
ENV AWS_SECRET_KEY=${AWS_SECRET_ACCESS_KEY}
ENV PRODUCT_QUEUE_NAME=guhesanv-products
ENV WIDGET_QUEUE_NAME=guhesanv-widgets

# Set the entry point
ENTRYPOINT ["java","-cp","app:app/lib/*","com.company.inventory.InventoryApplication"]

# Expose port 8080
EXPOSE 8080