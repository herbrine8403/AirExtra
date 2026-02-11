#!/bin/bash
# Use Gradle Wrapper from gradle directory

./gradlew --version 2>&1 || {
    echo "gradlew failed, trying gradle wrapper directly..."
    java -cp gradle/wrapper/gradle-wrapper.jar org.gradle.wrapper.GradleWrapperMain --version 2>&1 || echo "Both gradlew and gradle wrapper failed"
}