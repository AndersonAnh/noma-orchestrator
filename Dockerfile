ARG DOCKER_REGISTRY=registry.redhat.io
ARG JAVA_OPTS='java - jar'
FROM ${DOCKER_REGISTRY}/openjdk-17-opentelemetry:2.1.0
COPY noma-orchestrator-backend/target/*.jar noma-orchestrator.jar
RUN chmod 770 /home/jboss
ENTRYPOINT java $JAVA_OPTS -jar noma-orchestrator.jar
EXPOSE 8080