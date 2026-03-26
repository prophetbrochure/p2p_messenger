
FROM eclipse-temurin:21-jdk-alpine AS build
WORKDIR /app

# Копируем исходники
COPY src ./src

# Находим все java файлы

RUN mkdir bin && \
    find src -name "*.java" > sources.txt && \
    javac -encoding UTF-8 -d bin @sources.txt


FROM eclipse-temurin:21-jre-alpine
WORKDIR /app


COPY --from=build /app/bin ./bin

# Указываем точку входа по умолчанию (Main)
# Если нужно запустить конкретный тест, определяем при запуске
ENTRYPOINT ["java", "-cp", "bin"]
CMD ["Main"]