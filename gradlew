#!/bin/bash
# Use Gradle Wrapper from gradle directory

./gradlew --version || {
    echo "Using gradle wrapper directly..."
    java -cp gradle/wrapper/gradle-wrapper.jar org.gradle.wrapper.GradleWrapperMain "$@"
}