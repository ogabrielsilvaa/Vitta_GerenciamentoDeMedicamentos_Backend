############################
# 1) Build do BACKEND Java #
############################
FROM maven:3.9.9-amazoncorretto-21-alpine AS backend-build

WORKDIR /vittaBackend

# Copia e baixa dependências (cache)
COPY vittaBackend/pom.xml .
RUN mvn dependency:go-offline

# Copia código fonte e compila
COPY vittaBackend/src ./src
RUN mvn clean package -DskipTests

#########################################
# 2) Imagem final de runtime do backend #
#########################################
FROM amazoncorretto:21-alpine

WORKDIR /app

# Copia o JAR do backend gerado na etapa 1
COPY --from=backend-build /vittaBackend/target/*.jar app.jar

# NOTA: A parte que copiava o APK foi removida conforme solicitado.

EXPOSE 8407

CMD ["java", "-jar", "/app/app.jar"]