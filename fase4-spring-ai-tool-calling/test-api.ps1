# =============================================================================
# Test Script: fase4-spring-ai-tool-calling
# Tool Calling con Spring AI + Ollama
# Puerto: 8081
# =============================================================================

$BASE_URL = "http://localhost:8081"
$ENDPOINT = "$BASE_URL/api/tool-calling/chat"
$HEADERS = @{ "Content-Type" = "application/json" }

Write-Host "╔══════════════════════════════════════════════════════╗" -ForegroundColor Cyan
Write-Host "║   Test: Tool Calling con Spring AI + Ollama         ║" -ForegroundColor Cyan
Write-Host "╚══════════════════════════════════════════════════════╝" -ForegroundColor Cyan
Write-Host ""

# Verificar conexión
Write-Host "🔍 Verificando conexión a $BASE_URL..." -ForegroundColor Yellow
try {
    $health = Invoke-RestMethod -Uri "$BASE_URL/actuator/health" -Method Get -ErrorAction Stop
    Write-Host "✅ Aplicación disponible" -ForegroundColor Green
} catch {
    Write-Host "❌ La aplicación no está corriendo en $BASE_URL" -ForegroundColor Red
    Write-Host "   Ejecuta: .\mvnw -pl fase4-spring-ai-tool-calling spring-boot:run" -ForegroundColor Yellow
    exit 1
}
Write-Host ""

# Test 1: Lab 13 - obtenerClima
Write-Host "━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━" -ForegroundColor Gray
Write-Host "🌤️  Test 1 - Lab 13: obtenerClima (Lima)" -ForegroundColor Green
Write-Host "━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━" -ForegroundColor Gray
$body = '{"message": "¿Cómo está el clima en Lima?"}'
Write-Host "📤 $body" -ForegroundColor Yellow
$response = Invoke-RestMethod -Uri $ENDPOINT -Method Post -Headers $HEADERS -Body $body
Write-Host "📥 $($response | ConvertTo-Json -Depth 5)" -ForegroundColor White
Write-Host ""

# Test 2: Lab 13 - obtenerClima otra ciudad
Write-Host "━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━" -ForegroundColor Gray
Write-Host "🌤️  Test 2 - Lab 13: obtenerClima (Madrid)" -ForegroundColor Green
Write-Host "━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━" -ForegroundColor Gray
$body = '{"message": "¿Qué temperatura hace en Madrid?"}'
Write-Host "📤 $body" -ForegroundColor Yellow
$response = Invoke-RestMethod -Uri $ENDPOINT -Method Post -Headers $HEADERS -Body $body
Write-Host "📥 $($response | ConvertTo-Json -Depth 5)" -ForegroundColor White
Write-Host ""

# Test 3: Reto - consultarPais (API real)
Write-Host "━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━" -ForegroundColor Gray
Write-Host "🌍 Test 3 - Reto: consultarPais (Japón)" -ForegroundColor Green
Write-Host "━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━" -ForegroundColor Gray
$body = '{"message": "Cuéntame sobre Japón: capital, población e idiomas"}'
Write-Host "📤 $body" -ForegroundColor Yellow
$response = Invoke-RestMethod -Uri $ENDPOINT -Method Post -Headers $HEADERS -Body $body
Write-Host "📥 $($response | ConvertTo-Json -Depth 5)" -ForegroundColor White
Write-Host ""

# Test 4: Sin herramientas
Write-Host "━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━" -ForegroundColor Gray
Write-Host "💬 Test 4 - Pregunta general (sin tools)" -ForegroundColor Green
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
