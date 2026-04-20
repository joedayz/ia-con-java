# =============================================================================
# Test Script: fase4-spring-ai-tool-calling
# Tool Calling con Spring AI + Ollama
# Compatible con Windows PowerShell 5.1 (ASCII-safe)
# Puerto: 8081
# =============================================================================

$BASE_URL = "http://localhost:8081"
$ENDPOINT = "$BASE_URL/api/tool-calling/chat"

function Invoke-ChatTest {
    param(
        [Parameter(Mandatory = $true)] [string] $Title,
        [Parameter(Mandatory = $true)] [string] $Message
    )

    Write-Host "------------------------------------------------------" -ForegroundColor Gray
    Write-Host $Title -ForegroundColor Green
    Write-Host "------------------------------------------------------" -ForegroundColor Gray

    $bodyObject = @{ message = $Message }
    $bodyJson = $bodyObject | ConvertTo-Json -Compress

    Write-Host "REQUEST: $bodyJson" -ForegroundColor Yellow
    $response = Invoke-RestMethod -Uri $ENDPOINT -Method Post -ContentType "application/json" -Body $bodyJson
    Write-Host "RESPONSE: $($response | ConvertTo-Json -Depth 6)" -ForegroundColor White
    Write-Host ""
}

Write-Host "======================================================" -ForegroundColor Cyan
Write-Host "  Test: Tool Calling con Spring AI + Ollama" -ForegroundColor Cyan
Write-Host "======================================================" -ForegroundColor Cyan
Write-Host ""

# Verificar conexión
Write-Host "Verificando conexion a $BASE_URL..." -ForegroundColor Yellow
try {
    $null = Invoke-RestMethod -Uri "$BASE_URL/actuator/health" -Method Get -ErrorAction Stop
    Write-Host "OK: Aplicacion disponible" -ForegroundColor Green
} catch {
    Write-Host "ERROR: La aplicacion no esta corriendo en $BASE_URL" -ForegroundColor Red
    Write-Host "Ejecuta: mvn -pl fase4-spring-ai-tool-calling spring-boot:run" -ForegroundColor Yellow
    exit 1
}
Write-Host ""

# Test 1: Lab 13 - obtenerClima
Invoke-ChatTest -Title "Test 1 - Lab 13: obtenerClima (Lima)" -Message "Como esta el clima en Lima?"

# Test 2: Lab 13 - obtenerClima otra ciudad
Invoke-ChatTest -Title "Test 2 - Lab 13: obtenerClima (Madrid)" -Message "Que temperatura hace en Madrid?"

# Test 3: Reto - consultarPais (API real)
Invoke-ChatTest -Title "Test 3 - Reto: consultarPais (Japon)" -Message "Cuentame sobre Japon: capital, poblacion e idiomas"

# Test 4: consultarPais con alias en espanol
Invoke-ChatTest -Title "Test 4 - Alias: consultarPais (Alemania)" -Message "Dame datos de Alemania: capital e idiomas"

# Test 5: consultarPais no encontrado (fallback)
Invoke-ChatTest -Title "Test 5 - Fallback: pais no encontrado" -Message "Cuentame sobre Wakanda: capital y poblacion"

# Test 6: Sin herramientas
Invoke-ChatTest -Title "Test 6 - Pregunta general (sin tools)" -Message "Explica brevemente que es Tool Calling en IA"

Write-Host "======================================================" -ForegroundColor Cyan
Write-Host "Tests completados" -ForegroundColor Cyan
Write-Host "Swagger UI: $BASE_URL/swagger-ui.html" -ForegroundColor Cyan
Write-Host "======================================================" -ForegroundColor Cyan
