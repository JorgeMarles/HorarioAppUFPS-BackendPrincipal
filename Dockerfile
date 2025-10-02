### STAGE 1:BUILD ###
FROM maven:3-amazoncorretto-17-alpine AS build

# Create app directory
WORKDIR /app

# Copy the pom.xml file
COPY pom.xml .

# Download the dependencies
RUN mvn dependency:go-offline -B

# Copy the source code
COPY src ./src

# Build the application
RUN mvn clean package -DskipTests


### STAGE 2:DEPLOY ###
FROM amazoncorretto:17-alpine AS deploy

# Create app directory
WORKDIR /app

# Copy the built jar file
COPY --from=build /app/target/*.jar app.jar

# Expose the port
EXPOSE 9091

# Run the application
ENTRYPOINT ["java", "-jar", "app.jar"]