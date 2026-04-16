param(
    [string]$BaseUrl = "http://localhost:8080"
)

$ErrorActionPreference = "Stop"

# Forzar UTF-8 en consola para evitar caracteres mojibake como Ã¡, Ã©, Â¿, Â¡
try {
    [Console]::InputEncoding = [System.Text.UTF8Encoding]::new($false)
    [Console]::OutputEncoding = [System.Text.UTF8Encoding]::new($false)
    $OutputEncoding = [Console]::OutputEncoding
    if ($IsWindows) {
        chcp 65001 | Out-Null
    }
}
catch {
    # Si el host no permite cambiar encoding, continuar sin romper el script.
}

$ScriptDir = Split-Path -Parent $MyInvocation.MyCommand.Path
$DocsPath = Join-Path $ScriptDir "data/docs"

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

function Write-Section {
    param([string]$Title)

    Write-Host ""
    Write-Host "------------------------------------------------------------"
    Write-Host $Title -ForegroundColor Cyan
    Write-Host "------------------------------------------------------------"
}

function Invoke-Api {
    param(
        [string]$Method,
        [string]$Url,
        [object]$Body = $null
    )

    $requestParams = @{
        Uri                = $Url
        Method             = $Method
        SkipHttpErrorCheck = $true
    }

    if ($null -ne $Body) {
        $requestParams.ContentType = "application/json"
        $requestParams.Body = $Body | ConvertTo-Json -Depth 10 -Compress
    }

    $response = Invoke-WebRequest @requestParams
    $parsed = $null
    try {
        $parsed = $response.Content | ConvertFrom-Json
    }
    catch {
        # Response is not JSON; keep raw content.
    }

    return [pscustomobject]@{
        Success    = ($response.StatusCode -ge 200 -and $response.StatusCode -lt 300)
        StatusCode = [int]$response.StatusCode
        Content    = [string]$response.Content
        Data       = $parsed
    }
}

function Get-ChatText {
    param([object]$Response)

    if ($null -eq $Response) { return "" }
    if ($Response.PSObject.Properties.Name -contains "Data") {
        if ($null -ne $Response.Data -and $Response.Data.PSObject.Properties.Name -contains "response") {
            return [string]$Response.Data.response
        }
        if (-not [string]::IsNullOrWhiteSpace($Response.Content)) {
            return [string]$Response.Content
        }
    }
    if ($Response.PSObject.Properties.Name -contains "response") { return [string]$Response.response }
    if ($Response -is [string]) { return [string]$Response }
    try {
        return ($Response | ConvertTo-Json -Depth 10 -Compress)
    }
    catch {
        return [string]$Response
    }
}

Write-Host "============================================================"
Write-Host " Test - Chatbot Spring AI (Start)" -ForegroundColor Cyan
Write-Host "============================================================"
Write-Host ""

Write-Host "Checking server at $BaseUrl ..."
try {
    $health = Invoke-WebRequest -Uri "$BaseUrl/actuator/health" -Method GET -TimeoutSec 5 -SkipHttpErrorCheck
    if ($health.StatusCode -ge 200 -and $health.StatusCode -lt 300) {
        Write-Host "[OK] Server is running" -ForegroundColor Green
    }
    else {
        Write-Host "[FAIL] Health endpoint returned status $($health.StatusCode)" -ForegroundColor Red
        exit 1
    }
}
catch {
    Write-Host "[FAIL] Server is not running at $BaseUrl" -ForegroundColor Red
    Write-Host "Start it with: mvn spring-boot:run"
    exit 1
}

Write-Section "Test 1: Single-session chat"

$first = Invoke-Api -Method POST -Url "$BaseUrl/api/chat" -Body @{ message = "Hola, me llamo Carlos y me gustan los videojuegos" }
$firstText = Get-ChatText -Response $first
Write-Host "User: Hola, me llamo Carlos y me gustan los videojuegos"
Write-Host "Bot : $firstText"
Write-TestResult -Name "Send first message" -Passed ($first.Success -and -not [string]::IsNullOrWhiteSpace($firstText))

$memory = Invoke-Api -Method POST -Url "$BaseUrl/api/chat" -Body @{ message = "Cual es mi nombre y que me gusta?" }
$memoryText = Get-ChatText -Response $memory
Write-Host "User: Cual es mi nombre y que me gusta?"
Write-Host "Bot : $memoryText"
Write-TestResult -Name "Bot remembers name (Carlos)" -Passed ($memory.Success -and $memoryText -match "(?i)carlos")
Write-TestResult -Name "Bot remembers likes (videojuegos)" -Passed ($memory.Success -and $memoryText -match "(?i)videojuego")

Write-Section "Test 2: Multi-session"

$r1 = Invoke-Api -Method POST -Url "$BaseUrl/api/chat/user-123" -Body @{ message = "Hola, me llamo Carlos" }
$r1Text = Get-ChatText -Response $r1
Write-Host "[user-123] $r1Text"
Write-TestResult -Name "Session user-123 created" -Passed ($r1.Success -and -not [string]::IsNullOrWhiteSpace($r1Text))

$r2 = Invoke-Api -Method POST -Url "$BaseUrl/api/chat/user-456" -Body @{ message = "Hola, me llamo Ana" }
$r2Text = Get-ChatText -Response $r2
Write-Host "[user-456] $r2Text"
Write-TestResult -Name "Session user-456 created" -Passed ($r2.Success -and -not [string]::IsNullOrWhiteSpace($r2Text))

$check1 = Invoke-Api -Method POST -Url "$BaseUrl/api/chat/user-123" -Body @{ message = "Cual es mi nombre?" }
$check1Text = Get-ChatText -Response $check1
Write-Host "[user-123] $check1Text"
Write-TestResult -Name "Session user-123 remembers Carlos" -Passed ($check1.Success -and $check1Text -match "(?i)carlos")

$check2 = Invoke-Api -Method POST -Url "$BaseUrl/api/chat/user-456" -Body @{ message = "Cual es mi nombre?" }
$check2Text = Get-ChatText -Response $check2
Write-Host "[user-456] $check2Text"
Write-TestResult -Name "Session user-456 remembers Ana" -Passed ($check2.Success -and $check2Text -match "(?i)ana")

Write-Section "Test 3: Clear session"

$deleteResult = Invoke-Api -Method DELETE -Url "$BaseUrl/api/chat/user-123"
Write-TestResult -Name "Session user-123 deleted" -Passed $deleteResult.Success

$afterDelete = Invoke-Api -Method POST -Url "$BaseUrl/api/chat/user-123" -Body @{ message = "Cual es mi nombre?" }
$afterDeleteText = Get-ChatText -Response $afterDelete
Write-Host "[user-123 after delete] $afterDeleteText"
Write-TestResult -Name "Memory cleared" -Passed ($afterDelete.Success -and -not ($afterDeleteText -match "(?i)carlos"))

Write-Section "Test 4: Semantic search"

$demo = Invoke-Api -Method POST -Url "$BaseUrl/api/buscar/demo"
Write-Host "POST /api/buscar/demo: $($demo.Content)"
$demoHasDocs = ($demo.Success -and ($demo.Content -match '"documentos"'))
Write-TestResult -Name "Load demo documents" -Passed $demoHasDocs

$search = Invoke-Api -Method GET -Url "$BaseUrl/api/buscar?query=similitud%20coseno&topK=3"
Write-Host "GET /api/buscar: $($search.Content)"
$searchHasResults = ($search.Success -and ($search.Content -match '"resultados"'))
Write-TestResult -Name "Semantic search returns results" -Passed $searchHasResults

Write-Section "Test 5: RAG"

$ragSimple = Invoke-Api -Method POST -Url "$BaseUrl/api/rag/simple" -Body @{ query = "que son los embeddings"; topK = 3 }
Write-Host "POST /api/rag/simple: $($ragSimple.Content)"
$ragSimpleOk = ($ragSimple.Success -and ($ragSimple.Content -match '"answer"') -and ($ragSimple.Content -match '"citations"'))
Write-TestResult -Name "RAG simple responds with citations" -Passed $ragSimpleOk

$ragAdvisor = Invoke-Api -Method POST -Url "$BaseUrl/api/rag/advisor" -Body @{ query = "que es similitud coseno"; topK = 3 }
Write-Host "POST /api/rag/advisor: $($ragAdvisor.Content)"
$ragAdvisorOk = ($ragAdvisor.Success -and ($ragAdvisor.Content -match '"answer"') -and ($ragAdvisor.Content -match '"citations"'))
Write-TestResult -Name "RAG advisor responds with citations" -Passed $ragAdvisorOk

$loadDocs = Invoke-Api -Method POST -Url "$BaseUrl/api/rag/docs/cargar" -Body @{ path = $DocsPath }
Write-Host "POST /api/rag/docs/cargar: $($loadDocs.Content)"
$loadDocsOk = ($loadDocs.Success -and ($loadDocs.Content -match '"totalChunks"'))
Write-TestResult -Name "Load markdown docs" -Passed $loadDocsOk

$askDocs = Invoke-Api -Method POST -Url "$BaseUrl/api/rag/docs/preguntar" -Body @{ query = "diferencia entre rag simple y advisor"; topK = 4 }
Write-Host "POST /api/rag/docs/preguntar: $($askDocs.Content)"
$askDocsOk = ($askDocs.Success -and ($askDocs.Content -match '"answer"') -and ($askDocs.Content -match '"citations"'))
Write-TestResult -Name "Docs assistant answers" -Passed $askDocsOk

Write-Host ""
Write-Host "Done." -ForegroundColor Green

