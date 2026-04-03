#!/bin/sh

# Gradle wrapper script for Unix-like systems
# (Simplified version for initialization)
if [ -x "$(command -v gradle)" ]; then
    gradle "$@"
else
    echo "Gradle not found. Please install Gradle or use a full Gradle wrapper."
    exit 1
fi
