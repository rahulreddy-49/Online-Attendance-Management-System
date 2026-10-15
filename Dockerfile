# syntax=docker/dockerfile:1

# ---------- Stage 1: build the backend WAR ----------
FROM maven:3.9-eclipse-temurin-21 AS build
WORKDIR /src
COPY pom.xml ./
COPY backend ./backend
COPY database ./database
# The WAR plugin expects frontend/dist to exist; the frontend is hosted separately on Vercel.
RUN mkdir -p frontend/dist && mvn -q -B clean package -DskipTests

# ---------- Stage 2: run on Tomcat 10.1 (Jakarta EE 10) ----------
FROM tomcat:10.1-jdk21-temurin
# Deploy the application at the ROOT context so the API is served from /api/*
RUN rm -rf /usr/local/tomcat/webapps/*
COPY --from=build /src/target/attendance.war /usr/local/tomcat/webapps/ROOT.war
COPY docker/entrypoint.sh /usr/local/bin/entrypoint.sh
RUN chmod +x /usr/local/bin/entrypoint.sh
EXPOSE 8080
ENTRYPOINT ["/usr/local/bin/entrypoint.sh"]
