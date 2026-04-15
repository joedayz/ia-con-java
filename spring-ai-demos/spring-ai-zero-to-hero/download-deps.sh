#!/usr/bin/env bash

# Function to pull images using available engine
pull_images() {
  local engine
  if command -v docker &>/dev/null; then
    engine="docker"
  elif command -v podman &>/dev/null; then
    engine="podman"
  else
    echo "❌ No container engine (docker/podman) found to pull images."
    return 1
  fi

  echo "Using $engine to pull images..."
  if [[ "$engine" == "podman" ]] && command -v podman-compose &>/dev/null; then
    podman-compose -f docker/postgres/docker-compose.yaml pull
    podman-compose -f docker/observability-stack/docker-compose.yaml pull
  else
    $engine compose -f docker/postgres/docker-compose.yaml pull
    $engine compose -f docker/observability-stack/docker-compose.yaml pull
  fi
}

ollama pull llama3.2
ollama pull llava
ollama pull mxbai-embed-large

pull_images

./mvnw clean package
