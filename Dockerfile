FROM openjdk:17-jdk-slim

WORKDIR /app

# Copy all files
COPY . .

# Compile the Java code
RUN javac -d out src/com/teenupi/*.java src/com/teenupi/model/*.java src/com/teenupi/service/*.java

# Run the application
CMD ["java", "-cp", "out", "com.teenupi.TeenPayApp"]
