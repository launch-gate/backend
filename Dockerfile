FROM eclipse-temurin:25-jre

WORKDIR /opt/launch-gate

RUN useradd --system --uid 1001 spring

COPY app.jar app.jar

EXPOSE 8090

USER spring

ENTRYPOINT ["java", "-jar", "/opt/launch-gate/app.jar"]
