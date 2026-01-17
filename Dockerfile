FROM mosipdev/openjdk-21-jdk

WORKDIR /app
COPY /target/*.jar /app/application.jar

EXPOSE 8080:8080

ENTRYPOINT ["java","-jar","application.jar"]