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
FROM ubuntu:22.04 AS mobile-build

WORKDIR /vittaFrontend

# 2.1) Instalar dependências (Essencial: Compiladores C++ para a Nova Arquitetura)
RUN apt-get update && apt-get install -y \
    openjdk-17-jdk \
    curl \
    unzip \
    git \
    build-essential \
    cmake \
    ninja-build \
    && curl -fsSL https://deb.nodesource.com/setup_20.x | bash - \
    && apt-get install -y nodejs \
    && rm -rf /var/lib/apt/lists/*

# 2.2) Configurar Variável JAVA_HOME
ENV JAVA_HOME=/usr/lib/jvm/java-17-openjdk-amd64
ENV PATH=$JAVA_HOME/bin:$PATH

# 2.3) Configurar Variáveis de Ambiente do Android
ENV ANDROID_HOME=/opt/android-sdk
ENV PATH=${PATH}:${ANDROID_HOME}/cmdline-tools/latest/bin:${ANDROID_HOME}/platform-tools

# 2.4) Baixar e Instalar Android Command Line Tools
RUN mkdir -p ${ANDROID_HOME}/cmdline-tools \
    && curl -o commandlinetools.zip https://dl.google.com/android/repository/commandlinetools-linux-11076708_latest.zip \
    && unzip commandlinetools.zip -d ${ANDROID_HOME}/cmdline-tools \
    && mv ${ANDROID_HOME}/cmdline-tools/cmdline-tools ${ANDROID_HOME}/cmdline-tools/latest \
    && rm commandlinetools.zip

# 2.5) Aceitar Licenças e Instalar SDKs
RUN yes | sdkmanager --licenses \
    && sdkmanager "platform-tools" "platforms;android-34" "build-tools;34.0.0"

# 2.6) Copiar o projeto
COPY vittaFrontend/package.json vittaFrontend/package-lock.json* ./
RUN npm install

COPY vittaFrontend/ .

# 2.7) Prebuild
RUN npx expo prebuild --platform android --clean

# 2.8) Rodar o Gradle (Build Nativo) com ESTRATÉGIA DE SOBREVIVÊNCIA
WORKDIR /vittaFrontend/android
RUN chmod +x gradlew

# --- AQUI ESTÁ A MÁGICA ---
# -PnewArchEnabled=true: Ativamos o que as bibliotecas pedem.
# --max-workers=1: OBRIGATÓRIO. Impede o pico de memória. Um arquivo por vez.
# -Xmx2g: Limitamos o Java a 2GB para sobrar RAM para o compilador C++ (que roda fora do Java).
RUN ./gradlew assembleDebug \
    --no-daemon \
    --max-workers=1 \
    -Dorg.gradle.java.home=/usr/lib/jvm/java-17-openjdk-amd64 \
    -Dorg.gradle.jvmargs="-Xmx2g -XX:MaxMetaspaceSize=512m" \
    -PnewArchEnabled=true

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