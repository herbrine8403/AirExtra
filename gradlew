#!/bin/bash
# Simple gradlew wrapper

echo "Using gradle wrapper..."
if [ ! -f gradle/wrapper/gradle-wrapper.jar ]; then
    echo "Downloading Gradle Wrapper JAR..."
    wget -q https://raw.githubusercontent.com/gradle/gradle/master/gradle/wrapper/gradle-wrapper.jar -O gradle/wrapper/gradle-wrapper.jar
fi
java -cp gradle/wrapper/gradle-wrapper.jar org.gradle.wrapper.GradleWrapperMain "$@"