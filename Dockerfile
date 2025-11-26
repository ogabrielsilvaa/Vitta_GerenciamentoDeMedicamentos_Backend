############################
# 1) Build do BACKEND Java #
############################
FROM maven:3.9.9-amazoncorretto-21-alpine AS backend-build
WORKDIR /vittaBackend
COPY vittaBackend/pom.xml .
RUN mvn dependency:go-offline
COPY vittaBackend/src ./src
RUN mvn clean package -DskipTests

##############################################
# 2) Build do MOBILE (Via EAS Cloud Manager) #
##############################################
FROM node:20-alpine AS mobile-build

WORKDIR /vittaFrontend

# Instala o EAS CLI globalmente
RUN npm install -g eas-cli

# Copia os arquivos do projeto mobile
COPY vittaFrontend/package.json vittaFrontend/yarn.lock* vittaFrontend/package-lock.json* ./
RUN npm install

# Copia o código fonte do mobile
COPY vittaFrontend/ .

# ARGUMENTO DE BUILD: O Token precisa ser passado na hora do build
ARG EXPO_TOKEN
ENV EXPO_TOKEN=$EXPO_TOKEN

# O COMANDO MÁGICO:
# 1. --profile preview: Usa seu perfil de APK
# 2. --platform android: Só Android
# 3. --non-interactive: Não faz perguntas
# 4. --wait: O Docker fica parado esperando o build acabar
# 5. --output: Quando acabar, SALVA O ARQUIVO nesta pasta com este nome
RUN eas build --platform android --profile preview --non-interactive --output ./app-release.apk

#########################################
# 3) Imagem FINAL (Junta tudo)          #
#########################################
FROM amazoncorretto:21-alpine

WORKDIR /app

# Pega o JAR do backend
COPY --from=backend-build /vittaBackend/target/*.jar app.jar

# Cria pasta apk
RUN mkdir -p /app/apk

# Pega o APK que foi baixado no estágio 2
COPY --from=mobile-build /vittaFrontend/app-release.apk /app/apk/app-release.apk

EXPOSE 8407

CMD ["java", "-jar", "/app/app.jar"]