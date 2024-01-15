# BUILD STAGE
FROM amazoncorretto:21 as build
LABEL author="Marco Cetica"

# Prepare working environment
WORKDIR /workspace/app
COPY mvnw .
COPY .mvn .mvn
COPY pom.xml .
COPY src src

# Set environment variables for unit testing
ARG SERVER_PORT="3000"
ARG SPRING_DATASOURCE_URL="jdbc:h2:mem:testdb"
ARG SPRING_DATASOURCE_DRIVERCLASSNAME="org.h2.Driver"
ARG SPRING_DATASOURCE_USERNAME="sa"
ARG SPRING_DATASOURCE_PASSWORD="password"

# Build the jar file and execute the unit tests
RUN ./mvnw package

# RUN STAGE
FROM amazoncorretto:21 as run

# Configure working environment
VOLUME /tmp
ARG BUILD=/workspace/app/target

# Copy jar file
COPY --from=build ${BUILD}/*.jar app.jar

# Run the application
ENTRYPOINT ["java", "-jar", "app.jar"]
