# Use official OpenJDK image
FROM openjdk:17-jdk-slim

# Set working directory
WORKDIR /app

# Install Maven
RUN apt update && apt install -y maven
# Copy the project files

COPY . .

# Build the project using Maven
RUN mvn clean package -DskipTests

 #Copy the built JAR from target folder
RUN cp target/*.jar app.jar


# Run the built JAR file
CMD ["java", "-jar", "target/marketplace-service-0.0.1-SNAPSHOT.jar"]



