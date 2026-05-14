# =============================================================================
# Test Script: fase4-langchain4j-tool-calling
# Tool Calling con LangChain4j + Ollama
# Puerto: 8082
# =============================================================================

$BASE_URL = "http://localhost:8082"
$ENDPOINT = "$BASE_URL/api/tool-calling/chat"
$HEADERS = @{ "Content-Type" = "application/json" }

Write-Host "╔══════════════════════════════════════════════════════╗" -ForegroundColor Cyan
Write-Host "║   Test: Tool Calling con LangChain4j + Ollama       ║" -ForegroundColor Cyan
Write-Host "╚══════════════════════════════════════════════════════╝" -ForegroundColor Cyan
Write-Host ""

# Verificar conexión
Write-Host "🔍 Verificando conexión a $BASE_URL..." -ForegroundColor Yellow
try {
    $health = Invoke-RestMethod -Uri "$BASE_URL/actuator/health" -Method Get -ErrorAction Stop
    Write-Host "✅ Aplicación disponible" -ForegroundColor Green
} catch {
    Write-Host "❌ La aplicación no está corriendo en $BASE_URL" -ForegroundColor Red
    Write-Host "   Ejecuta: .\mvnw -pl fase4-langchain4j-tool-calling spring-boot:run" -ForegroundColor Yellow
    exit 1
}
Write-Host ""

# Test 1: Lab 14 - Calculadora
Write-Host "━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━" -ForegroundColor Gray
Write-Host "🧮 Test 1 - Lab 14: Calculadora (multiplicación)" -ForegroundColor Green
Write-Host "━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━" -ForegroundColor Gray
$body = '{"message": "¿Cuánto es 125 multiplicado por 37?"}'
Write-Host "📤 $body" -ForegroundColor Yellow
$response = Invoke-RestMethod -Uri $ENDPOINT -Method Post -Headers $HEADERS -Body $body
Write-Host "📥 $($response | ConvertTo-Json -Depth 5)" -ForegroundColor White
Write-Host ""

# Test 2: Lab 14 - Raíz cuadrada
Write-Host "━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━" -ForegroundColor Gray
Write-Host "🧮 Test 2 - Lab 14: Calculadora (raíz cuadrada)" -ForegroundColor Green
Write-Host "━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━" -ForegroundColor Gray
$body = '{"message": "¿Cuál es la raíz cuadrada de 144?"}'
Write-Host "📤 $body" -ForegroundColor Yellow
$response = Invoke-RestMethod -Uri $ENDPOINT -Method Post -Headers $HEADERS -Body $body
Write-Host "📥 $($response | ConvertTo-Json -Depth 5)" -ForegroundColor White
Write-Host ""

# Test 3: Lab 14 - Fecha actual
Write-Host "━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━" -ForegroundColor Gray
Write-Host "📅 Test 3 - Lab 14: fechaActual()" -ForegroundColor Green
Write-Host "━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━" -ForegroundColor Gray
$body = '{"message": "¿Qué fecha y hora es ahora?"}'
Write-Host "📤 $body" -ForegroundColor Yellow
$response = Invoke-RestMethod -Uri $ENDPOINT -Method Post -Headers $HEADERS -Body $body
Write-Host "📥 $($response | ConvertTo-Json -Depth 5)" -ForegroundColor White
Write-Host ""

# Test 4: Reto - consultarPais
Write-Host "━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━" -ForegroundColor Gray
Write-Host "🌍 Test 4 - Reto: consultarPais (Colombia)" -ForegroundColor Green
Write-Host "━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━" -ForegroundColor Gray
$body = '{"message": "Cuéntame sobre Colombia: capital, población e idiomas"}'
Write-Host "📤 $body" -ForegroundColor Yellow
$response = Invoke-RestMethod -Uri $ENDPOINT -Method Post -Headers $HEADERS -Body $body
Write-Host "📥 $($response | ConvertTo-Json -Depth 5)" -ForegroundColor White
Write-Host ""

# Test 5: Sin herramientas
Write-Host "━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━" -ForegroundColor Gray
Write-Host "💬 Test 5 - Pregunta general (sin tools)" -ForegroundColor Green
Write-Host "━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━" -ForegroundColor Gray
$body = '{"message": "Explica brevemente qué es Tool Calling en IA"}'
Write-Host "📤 $body" -ForegroundColor Yellow
$response = Invoke-RestMethod -Uri $ENDPOINT -Method Post -Headers $HEADERS -Body $body
Write-Host "📥 $($response | ConvertTo-Json -Depth 5)" -ForegroundColor White
Write-Host ""

Write-Host "╔══════════════════════════════════════════════════════╗" -ForegroundColor Cyan
Write-Host "║   ✅ Tests completados                              ║" -ForegroundColor Cyan
Write-Host "║   📚 Swagger UI: $BASE_URL/swagger-ui.html         ║" -ForegroundColor Cyan
Write-Host "╚══════════════════════════════════════════════════════╝" -ForegroundColor Cyan
