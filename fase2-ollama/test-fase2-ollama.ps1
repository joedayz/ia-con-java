# Script para ejecutar los labs de fase2-ollama
# Uso: .\test-fase2-ollama.ps1 [numero-lab]

param(
    [int]$LabNumber = 0
)

$ErrorActionPreference = "Stop"

$ScriptDir = Split-Path -Parent $MyInvocation.MyCommand.Path
$ProjectRoot = Split-Path -Parent $ScriptDir

if ((Split-Path -Leaf $ScriptDir) -eq "fase2-ollama") {
    Set-Location $ProjectRoot
}

function Write-Info {
    param([string]$Message)
    Write-Host $Message -ForegroundColor Cyan
}

$ollamaUrl = if ($env:OLLAMA_BASE_URL) { $env:OLLAMA_BASE_URL } else { "http://localhost:11434" }

try {
    $null = Invoke-WebRequest -Uri "$ollamaUrl/api/version" -UseBasicParsing -TimeoutSec 3
}
catch {
    Write-Host "Error: Ollama no responde en $ollamaUrl" -ForegroundColor Red
    Write-Host "Inicia Ollama con: ollama serve"
    exit 1
}

if (-not (Get-Command mvn -ErrorAction SilentlyContinue)) {
    Write-Host "Error: Maven no esta instalado" -ForegroundColor Red
    exit 1
}

function Select-Model {
    Write-Info "Modelo (enter para default / OLLAMA_MODEL)"
    return (Read-Host "Modelo")
}

function Invoke-Lab {
    param(
        [string]$ClassName,
        [string]$Description
    )

    Write-Info "Ejecutando: $Description"
    $model = Select-Model

    if ($model) {
        mvn -pl fase2-ollama exec:java `
            "-Dexec.mainClass=com.joedayz.ia.fase2.ollama.$ClassName" `
            "-Dexec.args=--model=$model"
    }
    else {
        mvn -pl fase2-ollama exec:java `
            "-Dexec.mainClass=com.joedayz.ia.fase2.ollama.$ClassName"
    }
}

if ($LabNumber -gt 0) {
    switch ($LabNumber) {
        1 { Invoke-Lab "PromptEngineering" "Lab 5: Chatbot Interactivo" }
        2 { Invoke-Lab "ClasificadorSentimiento" "Lab 6: Clasificador Sentimientos" }
        3 { Invoke-Lab "ChainOfThought" "Bonus: Chain of Thought" }
        4 { Invoke-Lab "SalidaEstructurada" "Bonus: Salida Estructurada" }
        5 { Invoke-Lab "ComparacionZeroShotVsFewShot" "Demo: Zero-Shot vs Few-Shot" }
        default {
            Write-Host "Opcion invalida" -ForegroundColor Red
            exit 1
        }
    }
    exit 0
}

while ($true) {
    Write-Info "Selecciona un lab"
    Write-Host "  1. Lab 5: Chatbot Interactivo"
    Write-Host "  2. Lab 6: Clasificador Sentimientos"
    Write-Host "  3. Bonus: Chain of Thought"
    Write-Host "  4. Bonus: Salida Estructurada"
    Write-Host "  5. Demo: Zero-Shot vs Few-Shot"
    Write-Host "  0. Salir"

    $choice = Read-Host "Opcion"

    switch ($choice) {
        "1" { Invoke-Lab "PromptEngineering" "Lab 5: Chatbot Interactivo" }
        "2" { Invoke-Lab "ClasificadorSentimiento" "Lab 6: Clasificador Sentimientos" }
        "3" { Invoke-Lab "ChainOfThought" "Bonus: Chain of Thought" }
        "4" { Invoke-Lab "SalidaEstructurada" "Bonus: Salida Estructurada" }
        "5" { Invoke-Lab "ComparacionZeroShotVsFewShot" "Demo: Zero-Shot vs Few-Shot" }
        "0" { exit 0 }
        default { Write-Host "Opcion invalida" -ForegroundColor Red }
    }

    Read-Host "Presiona Enter para continuar"
}

