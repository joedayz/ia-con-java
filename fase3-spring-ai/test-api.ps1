param(
    [string]$BaseUrl = "http://localhost:8080",
    [string]$PdfPath = "/Users/josediaz/Projects/JoeDayz/ia-con-java/docs/01-AI_Developer_Blueprint.pdf"
)

$ErrorActionPreference = "Stop"

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

function To-CompactString {
    param([object]$Value)

    if ($null -eq $Value) { return "" }
    if ($Value -is [string]) { return $Value }
    if ($Value.PSObject.Properties.Name -contains "Data") {
        if ($null -ne $Value.Data -and $Value.Data.PSObject.Properties.Name -contains "response") {
            return [string]$Value.Data.response
        }
        if (-not [string]::IsNullOrWhiteSpace($Value.Content)) {
            return [string]$Value.Content
        }
    }
    if ($Value.PSObject.Properties.Name -contains "response") { return [string]$Value.response }

    try {
        return ($Value | ConvertTo-Json -Depth 10 -Compress)
    }
    catch {
        return [string]$Value
    }
}

Write-Host "============================================================"
Write-Host " Test - Chatbot Spring AI (Solution)" -ForegroundColor Cyan
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

$status = Invoke-Api -Method GET -Url "$BaseUrl/api/chat/status"
Write-Host "Status: $(To-CompactString -Value $status)"

Write-Section "Test 1: Single-session chat"

$first = Invoke-Api -Method POST -Url "$BaseUrl/api/chat" -Body @{ message = "Hola, me llamo Carlos y me gustan los videojuegos" }
$firstText = To-CompactString -Value $first
Write-Host "Bot : $firstText"
Write-TestResult -Name "Send first message" -Passed ($first.Success -and -not [string]::IsNullOrWhiteSpace($firstText))

$memory = Invoke-Api -Method POST -Url "$BaseUrl/api/chat" -Body @{ message = "Cual es mi nombre y que me gusta?" }
$memoryText = To-CompactString -Value $memory
Write-Host "Bot : $memoryText"
Write-TestResult -Name "Bot remembers name (Carlos)" -Passed ($memory.Success -and $memoryText -match "(?i)carlos")
Write-TestResult -Name "Bot remembers likes (videojuegos)" -Passed ($memory.Success -and $memoryText -match "(?i)videojuego")

Write-Section "Test 2: Multi-session"

$r1 = Invoke-Api -Method POST -Url "$BaseUrl/api/chat/user-123" -Body @{ message = "Hola, me llamo Carlos" }
$r2 = Invoke-Api -Method POST -Url "$BaseUrl/api/chat/user-456" -Body @{ message = "Hola, me llamo Ana" }
Write-TestResult -Name "Session user-123 created" -Passed ($r1.Success -and -not [string]::IsNullOrWhiteSpace((To-CompactString -Value $r1)))
Write-TestResult -Name "Session user-456 created" -Passed ($r2.Success -and -not [string]::IsNullOrWhiteSpace((To-CompactString -Value $r2)))

$check1 = Invoke-Api -Method POST -Url "$BaseUrl/api/chat/user-123" -Body @{ message = "Cual es mi nombre?" }
$check2 = Invoke-Api -Method POST -Url "$BaseUrl/api/chat/user-456" -Body @{ message = "Cual es mi nombre?" }
Write-TestResult -Name "Session user-123 remembers Carlos" -Passed ($check1.Success -and (To-CompactString -Value $check1) -match "(?i)carlos")
Write-TestResult -Name "Session user-456 remembers Ana" -Passed ($check2.Success -and (To-CompactString -Value $check2) -match "(?i)ana")

Write-Section "Test 3: Clear session"

$deleteResult = Invoke-Api -Method DELETE -Url "$BaseUrl/api/chat/user-123"
$afterDelete = Invoke-Api -Method POST -Url "$BaseUrl/api/chat/user-123" -Body @{ message = "Cual es mi nombre?" }
$afterDeleteText = To-CompactString -Value $afterDelete
Write-TestResult -Name "Session user-123 deleted" -Passed $deleteResult.Success
Write-TestResult -Name "Memory cleared" -Passed ($afterDelete.Success -and -not ($afterDeleteText -match "(?i)carlos"))

Write-Section "Test 4: Semantic search"

$seed = Invoke-Api -Method POST -Url "$BaseUrl/api/buscar/demo"
$seedText = To-CompactString -Value $seed
Write-Host "Seed: $seedText"
Write-TestResult -Name "Seed docs indexed" -Passed ($seed.Success -and -not [string]::IsNullOrWhiteSpace($seedText))

$search = Invoke-Api -Method GET -Url "$BaseUrl/api/buscar?query=similitud%20coseno&topK=3"
$searchText = To-CompactString -Value $search
Write-Host "Search: $searchText"
Write-TestResult -Name "Search returns cosine-related result" -Passed ($search.Success -and $searchText -match "(?i)coseno")

Write-Section "Test 5: Basic RAG"

$rag = Invoke-Api -Method POST -Url "$BaseUrl/api/rag" -Body @{ question = "Explica la relacion entre embeddings y similitud coseno"; topK = 4 }
$ragText = To-CompactString -Value $rag
Write-Host "RAG: $ragText"
Write-TestResult -Name "RAG endpoint returns answer" -Passed ($rag.Success -and $ragText -match "(?i)answer")

Write-Section "Test 6: Optional PDF ingestion"

if (Test-Path $PdfPath) {
    $pdf = Invoke-Api -Method POST -Url "$BaseUrl/api/buscar/pdf" -Body @{ path = $PdfPath; sourceId = "ai-blueprint" }
    $pdfText = To-CompactString -Value $pdf
    Write-Host "PDF: $pdfText"
    Write-TestResult -Name "PDF indexed with TikaDocumentReader" -Passed ($pdf.Success -and -not [string]::IsNullOrWhiteSpace($pdfText))
}
else {
    Write-Host "[WARN] PDF not found at $PdfPath (optional test skipped)" -ForegroundColor Yellow
}

Write-Host ""
Write-Host "Done." -ForegroundColor Green

