param(
    [string]$BaseUrl = "http://localhost:8080"
)

$ErrorActionPreference = "Continue"

Write-Host "=============================================" -ForegroundColor Cyan
Write-Host "🧪 Pruebas de Fase 1 Spring Boot Ollama" -ForegroundColor Cyan
Write-Host "=============================================" -ForegroundColor Cyan
Write-Host ""

function Test-Endpoint {
    param(
        [string]$Title,
        [string]$Method,
        [string]$Endpoint,
        [hashtable]$Body
    )

    Write-Host $Title -ForegroundColor Blue
    Write-Host "$Method $Endpoint" -ForegroundColor Gray
    Write-Host ""

    try {
        $params = @{
            Uri             = $Endpoint
            Method          = $Method
            UseBasicParsing = $true
        }

        if ($Body) {
            $params.ContentType = "application/json; charset=utf-8"
            $params.Body = [System.Text.Encoding]::UTF8.GetBytes(($Body | ConvertTo-Json))
        }

        $response = Invoke-WebRequest @params
        $json = $response.Content | ConvertFrom-Json
        $json | ConvertTo-Json | Write-Host -ForegroundColor Green
    }
    catch {
        Write-Host "❌ Error: $_" -ForegroundColor Red
    }

    Write-Host ""
    Write-Host ""
}

# 1. Health Check
Test-Endpoint -Title "1️⃣ Health Check" `
    -Method GET `
    -Endpoint "$BaseUrl/api/chat/health"

# 2. GET Simple
Test-Endpoint -Title "2️⃣ GET Simple - Hola" `
    -Method GET `
    -Endpoint "$BaseUrl/api/chat?message=Hola"

# 3. GET con Pregunta
Test-Endpoint -Title "3️⃣ GET - Pregunta" `
    -Method GET `
    -Endpoint "$BaseUrl/api/chat?message=¿Cuál%20es%20la%20capital%20de%20Francia?"

# 4. POST sin system prompt
Test-Endpoint -Title "4️⃣ POST - Sin System Prompt" `
    -Method POST `
    -Endpoint "$BaseUrl/api/chat" `
    -Body @{
        message = "Escribe un haiku sobre la programación"
    }

# 5. POST con system prompt
Test-Endpoint -Title "5️⃣ POST - Con System Prompt" `
    -Method POST `
    -Endpoint "$BaseUrl/api/chat" `
    -Body @{
        message = "¿Qué es Java?"
        system_prompt = "Eres un profesor experto en programación. Explica de forma clara y concisa"
    }

# 6. POST generación de código
Test-Endpoint -Title "6️⃣ POST - Generación de Código" `
    -Method POST `
    -Endpoint "$BaseUrl/api/chat" `
    -Body @{
        message = "Escribe una función en Python que sume dos números"
        system_prompt = "Eres un programador experto"
    }

Write-Host "=============================================" -ForegroundColor Green
Write-Host "✅ Pruebas completadas" -ForegroundColor Green
Write-Host "=============================================" -ForegroundColor Green
Write-Host ""
Write-Host "💡 Tips:" -ForegroundColor Yellow
Write-Host "   - Si la conexión falla, verifica que:" -ForegroundColor Gray
Write-Host "     1. ollama serve esté ejecutándose" -ForegroundColor Gray
Write-Host "     2. mvn spring-boot:run esté ejecutándose" -ForegroundColor Gray
Write-Host "     3. El modelo esté instalado: ollama pull llama3.2" -ForegroundColor Gray

