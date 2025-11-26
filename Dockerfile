############################
# 1) Build do BACKEND Java #
############################
FROM maven:3.9.9-amazoncorretto-21-alpine AS backend-build
WORKDIR /vittaBackend
COPY vittaBackend/pom.xml .
RUN mvn dependency:go-offline
COPY vittaBackend/src ./src
RUN mvn clean package -DskipTests

########################################################
# 2) Build do MOBILE (Android SDK + Gradle "Local")    #
########################################################
# Usamos Ubuntu pois Alpine é ruim para Android SDK (glibc)
FROM ubuntu:22.04 AS mobile-build

WORKDIR /vittaFrontend

# 2.1) Instalar dependências básicas (Java, Curl, Zip, Node.js)
RUN apt-get update && apt-get install -y \
    openjdk-17-jdk \
    curl \
    unzip \
    git \
    && curl -fsSL https://deb.nodesource.com/setup_20.x | bash - \
    && apt-get install -y nodejs \
    && rm -rf /var/lib/apt/lists/*

# 2.2) Configurar Variáveis de Ambiente do Android
ENV ANDROID_HOME=/opt/android-sdk
ENV PATH=${PATH}:${ANDROID_HOME}/cmdline-tools/latest/bin:${ANDROID_HOME}/platform-tools

# 2.3) Baixar e Instalar Android Command Line Tools
# (Verifique sempre a versão mais recente no site do Android Studio se quebrar no futuro)
RUN mkdir -p ${ANDROID_HOME}/cmdline-tools \
    && curl -o commandlinetools.zip https://dl.google.com/android/repository/commandlinetools-linux-11076708_latest.zip \
    && unzip commandlinetools.zip -d ${ANDROID_HOME}/cmdline-tools \
    && mv ${ANDROID_HOME}/cmdline-tools/cmdline-tools ${ANDROID_HOME}/cmdline-tools/latest \
    && rm commandlinetools.zip

# 2.4) Aceitar Licenças e Instalar SDKs necessários
# Ajuste "platforms;android-34" e "build-tools;34.0.0" conforme seu build.gradle exige
RUN yes | sdkmanager --licenses \
    && sdkmanager "platform-tools" "platforms;android-34" "build-tools;34.0.0"

# 2.5) Copiar o projeto
COPY vittaFrontend/package.json vittaFrontend/package-lock.json* ./
RUN npm install

COPY vittaFrontend/ .

# 2.6) Gerar a pasta android (Prebuild)
# Isso cria a pasta /android baseada no seu app.json
RUN npx expo prebuild --platform android --clean

# 2.7) Rodar o Gradle (Build Nativo)
WORKDIR /vittaFrontend/android
RUN chmod +x gradlew
# Aqui geramos o APK de DEBUG (assembleDebug) como você testou.
# O arquivo sairá em: android/app/build/outputs/apk/debug/app-debug.apk
# 2.7) Rodar o Gradle (Build Nativo)
WORKDIR /vittaFrontend/android
RUN chmod +x gradlew

# ALTERAÇÃO AQUI:
# Adicionamos -PnewArchEnabled=false para forçar o Reanimated a aceitar a arquitetura antiga
# Mantemos o limite de memória do Java (-Xmx4g)
RUN ./gradlew assembleDebug -PnewArchEnabled=false -Dorg.gradle.jvmargs="-Xmx4g -XX:MaxMetaspaceSize=512m"

#########################################
# 3) Imagem FINAL (Junta tudo)          #
#########################################
FROM amazoncorretto:21-alpine

WORKDIR /app

# Pega o JAR do backend
COPY --from=backend-build /vittaBackend/target/*.jar app.jar

# Cria pasta apk
RUN mkdir -p /app/apk

# Pega o APK gerado pelo Gradle
# ATENÇÃO: O caminho do assembleDebug é diferente do EAS
COPY --from=mobile-build /vittaFrontend/android/app/build/outputs/apk/debug/app-debug.apk /app/apk/vitta-app.apk

EXPOSE 8407

CMD ["java", "-jar", "/app/app.jar"]