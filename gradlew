#!/bin/bash
# Simple gradlew wrapper

echo "Using gradle wrapper..."
java -cp gradle/wrapper/gradle-wrapper.jar org.gradle.wrapper.GradleWrapperMain "$@"