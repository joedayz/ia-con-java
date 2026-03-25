# Script para ejecutar los labs de Fase 2 - Prompt Engineering
# Uso: .\test-fase2.ps1 [numero-lab]

param(
    [int]$LabNumber = 0
)

$ErrorActionPreference = "Stop"

# Detectar directorio raíz del proyecto
$ScriptDir = Split-Path -Parent $MyInvocation.MyCommand.Path
$ProjectRoot = Split-Path -Parent $ScriptDir

# Si estamos en fase2, subir al directorio raíz
if ((Split-Path -Leaf $ScriptDir) -eq "fase2") {
    Set-Location $ProjectRoot
}

# Colores
function Write-ColorOutput {
    param(
        [string]$Message,
        [string]$Color = "White"
    )
    $colors = @{
        "Red" = "Red"
        "Green" = "Green"
        "Yellow" = "Yellow"
        "Blue" = "Cyan"
        "Cyan" = "Cyan"
    }
    Write-Host $Message -ForegroundColor $colors[$Color]
}

Write-ColorOutput "========================================" -Color Blue
Write-ColorOutput "  Fase 2 - Prompt Engineering" -Color Blue
Write-ColorOutput "========================================" -Color Blue
Write-Host ""

# Verificar que al menos una API key está configurada (env o .env)
$hasApiKeys = $false

# Primero verificar variables de entorno
if ($env:OPENAI_API_KEY -or $env:ANTHROPIC_API_KEY -or $env:GEMINI_API_KEY) {
    $hasApiKeys = $true
    Write-ColorOutput "[OK] API keys detectadas en variables de entorno" -Color Green
    Write-Host ""
}

# Si no hay en env, verificar archivo .env
if (-not $hasApiKeys) {
    if (Test-Path ".env") {
        $hasApiKeys = $true
        Write-ColorOutput "✓ Archivo .env encontrado" -Color Green
        Write-Host ""
    }
}

# Si no hay ninguna configuración, mostrar error
if (-not $hasApiKeys) {
    Write-ColorOutput "[ERROR] No se encontraron API keys configuradas" -Color Red
    Write-Host ""
    Write-ColorOutput "Opciones de configuración:" -Color Yellow
    Write-Host ""
    Write-Host "Opción 1 (Recomendado): Variables de entorno"
    Write-Host "  1. Crear archivo en tu perfil de PowerShell:"
    Write-Host "     notepad `$PROFILE"
    Write-Host ""
    Write-Host "  2. Agregar estas líneas:"
    Write-Host '     $env:OPENAI_API_KEY = "sk-..."'
    Write-Host '     $env:ANTHROPIC_API_KEY = "sk-ant-..."'
    Write-Host '     $env:GEMINI_API_KEY = "AIza..."'
    Write-Host ""
    Write-Host "  3. Recargar perfil:"
    Write-Host "     . `$PROFILE"
    Write-Host ""
    Write-Host "Opción 2: Variables de sesión (temporal)"
    Write-Host '  $env:OPENAI_API_KEY = "sk-..."'
    Write-Host '  $env:ANTHROPIC_API_KEY = "sk-ant-..."'
    Write-Host '  $env:GEMINI_API_KEY = "AIza..."'
    Write-Host ""
    Write-Host "Opción 3: Archivo .env en la raíz del proyecto"
    Write-Host "  1. Copiar plantilla:"
    Write-Host "     Copy-Item .env.example .env"
    Write-Host ""
    Write-Host "  2. Editar .env y agregar tus API keys"
    Write-Host ""
    exit 1
}

# Verificar Maven
if (-not (Get-Command mvn -ErrorAction SilentlyContinue)) {
    Write-ColorOutput "[ERROR] Maven no esta instalado" -Color Red
    Write-Host "Descarga Maven en: https://maven.apache.org/download.cgi"
    exit 1
}

# Función para seleccionar proveedor
function Select-Provider {
    Write-ColorOutput "Selecciona el proveedor de IA:" -Color Cyan
    Write-Host ""
    Write-Host "  1. OpenAI (GPT-3.5)"
    Write-Host "  2. Anthropic (Claude 3 Haiku)"
    Write-Host "  3. Google Gemini 2.5 Flash"
    Write-Host "  4. Usar configuración por defecto"
    Write-Host ""
    
    $choice = Read-Host "Proveedor"
    Write-Host ""
    
    switch ($choice) {
        "1" { return "openai" }
        "2" { return "anthropic" }
        "3" { return "gemini" }
        "4" { return "" }
        "" { return "" }
        default {
            Write-ColorOutput "[ERROR] Opcion invalida, usando configuracion por defecto" -Color Red
            return ""
        }
    }
}

# Función para mostrar el menú
function Show-Menu {
    Write-ColorOutput "Selecciona un lab:" -Color Green
    Write-Host ""
    Write-Host "  1. Lab 5: Chatbot Interactivo (PromptEngineering)"
    Write-Host "  2. Lab 6: Clasificador de Sentimientos (Few-Shot)"
    Write-Host "  3. Bonus: Chain of Thought (Razonamiento paso a paso)"
    Write-Host "  4. Bonus: Salida Estructurada (JSON)"
    Write-Host "  5. Demo: Comparación Zero-Shot vs Few-Shot"
    Write-Host ""
    Write-Host "  0. Salir"
    Write-Host ""
}

# Función para ejecutar un lab
function Invoke-Lab {
    param(
        [string]$LabName,
        [string]$Description,
        [string]$ClassName
    )
    
    Write-ColorOutput "▶ Ejecutando: $Description" -Color Yellow
    Write-Host ""
    
    # Seleccionar proveedor
    $provider = Select-Provider
    
    if ($provider) {
        Write-ColorOutput "Usando proveedor: $provider" -Color Blue
        Write-Host ""
        mvn -pl fase2 exec:java `
            "-Dexec.mainClass=com.joedayz.ia.fase2.$ClassName" `
            "-Dexec.args=--provider=$provider"
    }
    else {
        Write-ColorOutput "Usando proveedor configurado en variables de entorno o .env" -Color Blue
        Write-Host ""
        mvn -pl fase2 exec:java `
            "-Dexec.mainClass=com.joedayz.ia.fase2.$ClassName"
    }
    
    Write-Host ""
    Write-ColorOutput "[OK] Lab completado" -Color Green
    Write-Host ""
}

# Si se pasa un argumento, ejecutar directamente
if ($LabNumber -gt 0) {
    switch ($LabNumber) {
        1 {
            Invoke-Lab "Lab 5" "Chatbot Interactivo" "PromptEngineering"
        }
        2 {
            Invoke-Lab "Lab 6" "Clasificador Sentimientos" "ClasificadorSentimiento"
        }
        3 {
            Invoke-Lab "Bonus" "Chain of Thought" "ChainOfThought"
        }
        4 {
            Invoke-Lab "Bonus" "Salida Estructurada" "SalidaEstructurada"
        }
        5 {
            Invoke-Lab "Demo" "Zero-Shot vs Few-Shot" "ComparacionZeroShotVsFewShot"
        }
        default {
            Write-ColorOutput "[ERROR] Opcion invalida" -Color Red
            exit 1
        }
    }
    exit 0
}

# Si no hay argumentos, mostrar menú interactivo
while ($true) {
    Show-Menu
    $choice = Read-Host "Opción"
    Write-Host ""
    
    switch ($choice) {
        "1" {
            Invoke-Lab "Lab 5" "Chatbot Interactivo" "PromptEngineering"
        }
        "2" {
            Invoke-Lab "Lab 6" "Clasificador Sentimientos" "ClasificadorSentimiento"
        }
        "3" {
            Invoke-Lab "Bonus" "Chain of Thought" "ChainOfThought"
        }
        "4" {
            Invoke-Lab "Bonus" "Salida Estructurada" "SalidaEstructurada"
        }
        "5" {
            Invoke-Lab "Demo" "Zero-Shot vs Few-Shot" "ComparacionZeroShotVsFewShot"
        }
        "0" {
            Write-ColorOutput "Hasta pronto!" -Color Green
            exit 0
        }
        default {
            Write-ColorOutput "[ERROR] Opcion invalida" -Color Red
            Write-Host ""
        }
    }
    
    Write-Host "Presiona Enter para continuar..."
    Read-Host
    Write-Host ""
}
