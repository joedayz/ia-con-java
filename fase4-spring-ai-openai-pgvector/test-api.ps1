# Test script para Fase 4 - Spring AI + OpenAI + PgVector (Windows PowerShell)
# Prerequisitos: Docker/Podman (PostgreSQL+pgvector en puerto 5433), OPENAI_API_KEY

$BASE_URL = "http://localhost:8080"
$headers = @{ "Content-Type" = "application/json" }

Write-Host "============================================" -ForegroundColor Cyan
Write-Host "  Fase 4: Spring AI + OpenAI + PgVector"     -ForegroundColor Cyan
Write-Host "============================================" -ForegroundColor Cyan

Write-Host "`n--- 1. Estado del chat ---" -ForegroundColor Yellow
try {
    Invoke-RestMethod -Uri "$BASE_URL/api/chat/status" | ConvertTo-Json -Depth 5
} catch { Write-Host "ERROR: $($_.Exception.Message)" -ForegroundColor Red }

Write-Host "`n--- 2. Estado del vector store (PgVector) ---" -ForegroundColor Yellow
try {
    Invoke-RestMethod -Uri "$BASE_URL/api/buscar/status" | ConvertTo-Json -Depth 5
} catch { Write-Host "ERROR: $($_.Exception.Message)" -ForegroundColor Red }

Write-Host "`n--- 3. Chat simple ---" -ForegroundColor Yellow
try {
    $body = @{ message = "¿Qué es PgVector?" } | ConvertTo-Json
    Invoke-RestMethod -Method Post -Uri "$BASE_URL/api/chat" -Headers $headers -Body $body | ConvertTo-Json -Depth 5
} catch { Write-Host "ERROR: $($_.Exception.Message)" -ForegroundColor Red }

Write-Host "`n--- 4. Cargar documentos demo en PgVector ---" -ForegroundColor Yellow
try {
    Invoke-RestMethod -Method Post -Uri "$BASE_URL/api/buscar/demo" | ConvertTo-Json -Depth 5
} catch { Write-Host "ERROR: $($_.Exception.Message)" -ForegroundColor Red }

Write-Host "`n--- 5. Búsqueda semántica en PgVector ---" -ForegroundColor Yellow
try {
    Invoke-RestMethod -Uri "$BASE_URL/api/buscar?query=embeddings vectores&topK=3" | ConvertTo-Json -Depth 5
} catch { Write-Host "ERROR: $($_.Exception.Message)" -ForegroundColor Red }

Write-Host "`n--- 6. RAG simple ---" -ForegroundColor Yellow
try {
    $body = @{ query = "¿Qué son los embeddings?"; topK = 3 } | ConvertTo-Json
    Invoke-RestMethod -Method Post -Uri "$BASE_URL/api/rag/simple" -Headers $headers -Body $body | ConvertTo-Json -Depth 10
} catch { Write-Host "ERROR: $($_.Exception.Message)" -ForegroundColor Red }

Write-Host "`n--- 7. RAG con QuestionAnswerAdvisor ---" -ForegroundColor Yellow
try {
    $body = @{ query = "¿Cómo funciona la búsqueda por similitud?"; topK = 4 } | ConvertTo-Json
    Invoke-RestMethod -Method Post -Uri "$BASE_URL/api/rag/advisor" -Headers $headers -Body $body | ConvertTo-Json -Depth 10
} catch { Write-Host "ERROR: $($_.Exception.Message)" -ForegroundColor Red }

Write-Host "`n--- 8. Cargar documentos Markdown ---" -ForegroundColor Yellow
try {
    $body = @{ path = "./data/docs" } | ConvertTo-Json
    Invoke-RestMethod -Method Post -Uri "$BASE_URL/api/rag/docs/cargar" -Headers $headers -Body $body | ConvertTo-Json -Depth 5
} catch { Write-Host "ERROR: $($_.Exception.Message)" -ForegroundColor Red }

Write-Host "`n--- 9. Preguntar sobre docs cargados ---" -ForegroundColor Yellow
try {
    $body = @{ query = "¿Qué es Spring AI y cómo se integra con RAG?"; topK = 5 } | ConvertTo-Json
    Invoke-RestMethod -Method Post -Uri "$BASE_URL/api/rag/docs/preguntar" -Headers $headers -Body $body | ConvertTo-Json -Depth 10
} catch { Write-Host "ERROR: $($_.Exception.Message)" -ForegroundColor Red }

Write-Host "`n============================================" -ForegroundColor Cyan
Write-Host "  Swagger UI: $BASE_URL/swagger-ui.html"      -ForegroundColor Cyan
Write-Host "  NOTA: Los vectores persisten en PostgreSQL"  -ForegroundColor Cyan
Write-Host "============================================" -ForegroundColor Cyan
