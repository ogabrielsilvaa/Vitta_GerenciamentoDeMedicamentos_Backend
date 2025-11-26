########################################################
# 2) Build do MOBILE (Android SDK + Gradle "Local")    #
########################################################
FROM ubuntu:22.04 AS mobile-build

WORKDIR /vittaFrontend

# 2.1) Instalar dependências (incluindo o Java 17)
RUN apt-get update && apt-get install -y \
    openjdk-17-jdk \
    curl \
    unzip \
    git \
    && curl -fsSL https://deb.nodesource.com/setup_20.x | bash - \
    && apt-get install -y nodejs \
    && rm -rf /var/lib/apt/lists/*

# --- CORREÇÃO PRINCIPAL AQUI ---
# Define explicitamente onde o Java está instalado no Ubuntu
ENV JAVA_HOME=/usr/lib/jvm/java-17-openjdk-amd64
ENV PATH=$JAVA_HOME/bin:$PATH
# -------------------------------

# 2.2) Configurar Variáveis de Ambiente do Android
ENV ANDROID_HOME=/opt/android-sdk
ENV PATH=${PATH}:${ANDROID_HOME}/cmdline-tools/latest/bin:${ANDROID_HOME}/platform-tools

# 2.3) Baixar e Instalar Android Command Line Tools
RUN mkdir -p ${ANDROID_HOME}/cmdline-tools \
    && curl -o commandlinetools.zip https://dl.google.com/android/repository/commandlinetools-linux-11076708_latest.zip \
    && unzip commandlinetools.zip -d ${ANDROID_HOME}/cmdline-tools \
    && mv ${ANDROID_HOME}/cmdline-tools/cmdline-tools ${ANDROID_HOME}/cmdline-tools/latest \
    && rm commandlinetools.zip

# 2.4) Aceitar Licenças e Instalar SDKs
RUN yes | sdkmanager --licenses \
    && sdkmanager "platform-tools" "platforms;android-34" "build-tools;34.0.0"

# 2.5) Copiar o projeto
COPY vittaFrontend/package.json vittaFrontend/package-lock.json* ./
RUN npm install

COPY vittaFrontend/ .

# 2.6) Prebuild (Gera a pasta android)
RUN npx expo prebuild --platform android --clean

# 2.7) Rodar o Gradle (Build Nativo)
WORKDIR /vittaFrontend/android
RUN chmod +x gradlew

# --- COMANDO DE BUILD AJUSTADO ---
# 1. Adicionamos -Dorg.gradle.java.home para forçar o uso do Java instalado
# 2. Mantemos -PnewArchEnabled=false se precisar desativar a nova arquitetura
# 3. Mantemos limits de memória
RUN ./gradlew clean assembleDebug \
    -Dorg.gradle.java.home=/usr/lib/jvm/java-17-openjdk-amd64 \
    -Dorg.gradle.jvmargs="-Xmx4g -XX:MaxMetaspaceSize=512m" \
    -PnewArchEnabled=false

#########################################
# 3) Imagem FINAL (Junta tudo)          #
#########################################
FROM amazoncorretto:21-alpine

WORKDIR /app

# Pega o JAR do backend
COPY --from=backend-build /vittaBackend/target/*.jar app.jar

# Cria pasta apk
RUN mkdir -p /app/apk

# Pega o APK gerado
COPY --from=mobile-build /vittaFrontend/android/app/build/outputs/apk/debug/app-debug.apk /app/apk/vitta-app.apk

EXPOSE 8407

CMD ["java", "-jar", "/app/app.jar"]