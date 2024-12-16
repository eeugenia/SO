# Pasul 1: Folosim o imagine de bază cu JDK pentru a rula Java
FROM openjdk:17-jdk-slim

# Pasul 2: Setăm directorul de lucru în container
WORKDIR /app

# Pasul 3: Copiem sursa programului Java în container
COPY . /app

# Pasul 4: Compilăm programul Java
RUN javac Main.java

# Pasul 5: Expunem portul 8001
EXPOSE 8001

# Pasul 6: Rulăm programul Java
CMD ["java", "Main"]
