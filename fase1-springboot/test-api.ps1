param(
    [string]$BaseUrl = "http://localhost:8080",
    [switch]$SkipServerCheck
)

$ErrorActionPreference = "Stop"

function Write-Section {
    param([string]$Text)
    Write-Host ""
    Write-Host "=== $Text ===" -ForegroundColor Cyan
}

function Write-TestResult {
    param(
        [string]$Name,
        [bool]$Passed
    )

    if ($Passed) {
        Write-Host "[OK] $Name" -ForegroundColor Green
    }
    else {
        Write-Host "[FAIL] $Name" -ForegroundColor Red
    }
}

function Invoke-Api {
    param(
        [string]$Method,
        [string]$Url,
        [object]$Body = $null
    )

    $params = @{
        Uri                = $Url
        Method             = $Method
        SkipHttpErrorCheck = $true
    }

    if ($null -ne $Body) {
        $params.ContentType = "application/json"
        $params.Body = $Body | ConvertTo-Json -Depth 8 -Compress
    }

    try {
        $response = Invoke-WebRequest @params
        return [pscustomobject]@{
            Success    = ($response.StatusCode -ge 200 -and $response.StatusCode -lt 300)
            StatusCode = [int]$response.StatusCode
            Content    = [string]$response.Content
        }
    }
    catch {
        return [pscustomobject]@{
            Success    = $false
            StatusCode = 0
            Content    = $_.Exception.Message
        }
    }
}

Write-Host "==================================="
Write-Host " Fase 1 Spring Boot API Tests"
Write-Host "==================================="

if (-not $SkipServerCheck) {
    $healthCheck = Invoke-Api -Method GET -Url "$BaseUrl/api/chat/health"
    if (-not $healthCheck.Success) {
        Write-Host "Server not reachable at $BaseUrl" -ForegroundColor Red
        Write-Host "Start with: mvn spring-boot:run"
        exit 1
    }
}

Write-Section "1) Health check"
$health = Invoke-Api -Method GET -Url "$BaseUrl/api/chat/health"
Write-Host $health.Content
Write-TestResult -Name "GET /api/chat/health" -Passed $health.Success

Write-Section "2) GET simple"
$getChat = Invoke-Api -Method GET -Url "$BaseUrl/api/chat?message=Di%20hola%20en%20una%20palabra"
Write-Host $getChat.Content
Write-TestResult -Name "GET /api/chat" -Passed $getChat.Success

Write-Section "3) POST without system prompt"
$postSimple = Invoke-Api -Method POST -Url "$BaseUrl/api/chat" -Body @{ message = "Que es Java en 10 palabras?" }
Write-Host $postSimple.Content
Write-TestResult -Name "POST /api/chat simple" -Passed $postSimple.Success

Write-Section "4) POST with system prompt"
$postSystem = Invoke-Api -Method POST -Url "$BaseUrl/api/chat" -Body @{ message = "Explica que es un LLM"; system_prompt = "Eres un profesor de IA que explica de forma muy simple" }
Write-Host $postSystem.Content
Write-TestResult -Name "POST /api/chat with system_prompt" -Passed $postSystem.Success

Write-Host ""
Write-Host "Done." -ForegroundColor Green

