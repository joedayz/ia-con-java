# Verifica que Ollama y los modelos esten listos para fase2-ollama.

$ErrorActionPreference = "Stop"

$ollamaUrl = if ($env:OLLAMA_BASE_URL) { $env:OLLAMA_BASE_URL } else { "http://localhost:11434" }

function Write-Ok {
    param([string]$Message)
    Write-Host "OK $Message" -ForegroundColor Green
}

function Write-Warn {
    param([string]$Message)
    Write-Host "Aviso: $Message" -ForegroundColor Yellow
}

function Write-Fail {
    param([string]$Message)
    Write-Host "Error: $Message" -ForegroundColor Red
}

Write-Host "Verificando Ollama para fase2-ollama..."
Write-Host ""

$hasOllama = $null -ne (Get-Command ollama -ErrorAction SilentlyContinue)
if ($hasOllama) {
    Write-Ok "ollama instalado"
}
else {
    Write-Warn "comando ollama no esta en PATH"
}

try {
    $null = Invoke-WebRequest -Uri "$ollamaUrl/api/version" -TimeoutSec 3
    Write-Ok "servidor disponible en $ollamaUrl"
}
catch {
    Write-Fail "servidor no disponible en $ollamaUrl"
    Write-Host "Ejecuta: ollama serve"
    exit 1
}

if ($hasOllama) {
    $listOutput = ollama list
    $modelLines = $listOutput | Select-Object -Skip 1 | Where-Object { -not [string]::IsNullOrWhiteSpace($_) }
    $modelCount = ($modelLines | Measure-Object).Count

    if ($modelCount -gt 0) {
        Write-Ok "modelos instalados: $modelCount"
        $listOutput
    }
    else {
        Write-Fail "no hay modelos instalados"
        Write-Host "Ejecuta: ollama pull mistral"
        exit 1
    }
}

Write-Host ""
Write-Ok "Listo. Puedes ejecutar:"
Write-Host "  mvn -pl fase2-ollama exec:java"

