# Start with a base image containing Java runtime (JDK 17)
FROM openjdk:17-oracle

# Add Maintainer Info
LABEL maintainer="your.email@example.com"

# Add a volume pointing to /tmp
VOLUME /tmp

# Make port 8080 available to the world outside this container
EXPOSE 8080

# The application's jar file
ARG JAR_FILE=target/management-0.0.1-SNAPSHOT.jar

# Add the application's jar to the container
ADD ${JAR_FILE} management-app.jar

# Run the jar file
ENTRYPOINT ["java","-Djava.security.egd=file:/dev/./urandom","-jar","/management-app.jar"]
