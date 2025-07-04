# Use an official OpenJDK runtime as a parent image
FROM openjdk:17-jdk-slim

# Set the working directory to /walletapp
WORKDIR /walletService

# Copy the current directory contents into the container at /walletService
COPY . .

RUN apt-get update && apt-get install -y telnet netcat

# Install Maven
RUN apt update && apt install -y maven

# Build the projects with Maven
RUN mvn clean package -DskipTests


ENTRYPOINT ["java" ,"-jar","target/walletService-0.0.1.jar"]
