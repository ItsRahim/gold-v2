#!/bin/bash

if ! command -v python &>/dev/null; then
    echo "Python is not installed. Installing Python..."

    if [[ "$OSTYPE" == "linux-gnu"* ]]; then
        sudo apt update
        sudo apt install -y python3 python3-pip
    elif [[ "$OSTYPE" == "darwin"* ]]; then
        brew install python
    elif [[ "$OSTYPE" == "msys" || "$OSTYPE" == "win32" ]]; then
        echo "Please install Python manually from https://www.python.org/downloads/"
        exit 1
    else
        echo "Unsupported OS. Please install Python manually."
        exit 1
    fi
else
    echo "Python is already installed"
fi

if ! command -v pre-commit &>/dev/null; then
    echo "pre-commit is not installed. Installing pre-commit..."
    python -m pip install --user pre-commit
else
    echo "pre-commit is already installed"
fi

echo "Running pre-commit install..."
pre-commit install

echo "Setup completed successfully!"
