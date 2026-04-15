#!/bin/bash

# Function to check if Java is installed and print its version
check_java() {
    echo "============================"
    echo "Checking Java installation:"
    echo "============================"
    if command -v java &> /dev/null
    then
        echo "✅ Java is installed. Version details:"
        java -version
    else
        echo "❌ Java is not installed."
    fi
    echo ""
}

# Function to check if Ollama is installed and print its version
check_ollama() {
    echo "==============================="
    echo "Checking Ollama installation:"
    echo "==============================="
    if command -v ollama &> /dev/null
    then
        echo "✅ Ollama is installed. Version details:"
        ollama --version
    else
        echo "❌ Ollama is not installed."
    fi
    echo ""
}

# Function to check if the llama3.2 model is pulled for Ollama
check_llama3_model() {
    echo "========================================"
    echo "Checking if llama3.2 model is pulled:"
    echo "========================================"
    if command -v ollama &> /dev/null
    then
        if ollama list | grep -q "llama3.2"
        then
            echo "✅ llama3.2 model is pulled and available."
        else
            echo "❌ llama3.2 model is not pulled. Please pull it using 'ollama pull llama3.2'."
        fi
    else
        echo "❌ Ollama is not installed, so the llama3.2 model cannot be checked."
    fi
    echo ""
}

# Function to check if the mxbai-embed-large model is pulled for Ollama
check_mxbai_embed_large_model() {
    echo "========================================"
    echo "Checking if mxbai-embed-large model is pulled:"
    echo "========================================"
    if command -v ollama &> /dev/null
    then
        if ollama list | grep -q "mxbai-embed-large"
        then
            echo "✅ mxbai-embed-large model is pulled and available."
        else
            echo "❌ mxbai-embed-large model is not pulled. Please pull it using 'ollama pull mxbai-embed-large'."
        fi
    else
        echo "❌ Ollama is not installed, so the mxbai-embed-large model cannot be checked."
    fi
    echo ""
}

# Function to check if the llava model is pulled for Ollama
check_llava_model() {
    echo "========================================"
    echo "Checking if llava model is pulled:"
    echo "========================================"
    if command -v ollama &> /dev/null
    then
        if ollama list | grep -q "llava"
        then
            echo "✅ llava model is pulled and available."
        else
            echo "❌ llava model is not pulled. Please pull it using 'ollama pull llava'."
        fi
    else
        echo "❌ Ollama is not installed, so the llava model cannot be checked."
    fi
    echo ""
}


# Function to check if Docker or Podman is installed and print its version
check_container_engine() {
    echo "=========================================="
    echo "Checking Container Engine (Docker/Podman):"
    echo "=========================================="
    if command -v docker &> /dev/null
    then
        echo "✅ Docker is installed. Version details:"
        docker --version
    elif command -v podman &> /dev/null
    then
        echo "✅ Podman is installed. Version details:"
        podman --version
    else
        echo "❌ Neither Docker nor Podman is installed."
    fi
    echo ""
}

# Function to check if a Docker/Podman image is pulled
check_container_image() {
    local image=$1
    local engine="docker"
    command -v docker &> /dev/null || engine="podman"

    echo "Checking image: $image (using $engine)"
    if command -v $engine &> /dev/null
    then
        if $engine images --format "{{.Repository}}:{{.Tag}}" | grep -q "$image"
        then
            echo "✅ Image $image is pulled."
        else
            echo "❌ Image $image is not pulled. Please pull it using '$engine pull $image'."
        fi
    else
        echo "❌ No container engine found to check image $image."
    fi
    echo ""
}

# Function to check if HTTPie is installed and print its version
check_httpie() {
    echo "==============================="
    echo "Checking HTTPie installation:"
    echo "==============================="
    if command -v http &> /dev/null
    then
        echo "✅ HTTPie is installed. Version details:"
        http --version
    else
        echo "❌ HTTPie is not installed."
    fi
    echo ""
}

# Run the functions to check for each software, the llama3 model, and Docker images
check_java
check_ollama
check_llama3_model
check_mxbai_embed_large_model
check_llava_model
check_container_engine
check_container_image "pgvector/pgvector:pg17"
check_container_image "dpage/pgadmin4:9.8.0"
check_httpie
