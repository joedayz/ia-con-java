param(
    [string]$BaseUrl = "http://localhost:8080",
    [switch]$SkipServerCheck
)

$ErrorActionPreference = "Stop"

$SupportsSkipHttpErrorCheck = (Get-Command Invoke-WebRequest).Parameters.ContainsKey("SkipHttpErrorCheck")

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
        Uri    = $Url
        Method = $Method
    }

    if ($SupportsSkipHttpErrorCheck) {
        $params.SkipHttpErrorCheck = $true
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
        $statusCode = 0
        $errorContent = $_.Exception.Message

        # PowerShell 5.1 throws on non-2xx and stores HTTP details in Exception.Response.
        if ($_.Exception.Response) {
            try {
                $statusCode = [int]$_.Exception.Response.StatusCode
            }
            catch {
                $statusCode = 0
            }

            try {
                $stream = $_.Exception.Response.GetResponseStream()
                if ($stream) {
                    $reader = New-Object System.IO.StreamReader($stream)
                    $raw = $reader.ReadToEnd()
                    if ($raw) {
                        $errorContent = $raw
                    }
                }
            }
            catch {
                # Keep default exception message if response body cannot be read.
            }
        }

        return [pscustomobject]@{
            Success    = $false
            StatusCode = $statusCode
            Content    = $errorContent
        }
    }
}

Write-Host "================================================="
Write-Host " Fase 1 Quarkus Start API Tests"
Write-Host "================================================="

if (-not $SkipServerCheck) {
    $healthCheck = Invoke-Api -Method GET -Url "$BaseUrl/api/chat/health"
    if (-not $healthCheck.Success) {
        Write-Host "Server not reachable at $BaseUrl" -ForegroundColor Red
        Write-Host "Start with: mvn quarkus:dev"
        exit 1
    }
}

Write-Section "1) Health check"
$health = Invoke-Api -Method GET -Url "$BaseUrl/api/chat/health"
Write-Host $health.Content
Write-TestResult -Name "GET /api/chat/health" -Passed $health.Success

Write-Section "2) GET simple"
$getChat = Invoke-Api -Method GET -Url "$BaseUrl/api/chat?message=Hola,%20como%20estas?"
Write-Host $getChat.Content
Write-TestResult -Name "GET /api/chat" -Passed $getChat.Success

Write-Section "3) POST simple"
$postSimple = Invoke-Api -Method POST -Url "$BaseUrl/api/chat" -Body @{ message = "Explica que es un LLM en una frase" }
Write-Host $postSimple.Content
Write-TestResult -Name "POST /api/chat simple" -Passed $postSimple.Success

Write-Section "4) Technical Java question"
$postJava = Invoke-Api -Method POST -Url "$BaseUrl/api/chat" -Body @{ message = "Cual es la diferencia entre una interface y una clase abstracta en Java?" }
Write-Host $postJava.Content
Write-TestResult -Name "POST /api/chat technical question" -Passed $postJava.Success

Write-Host ""
Write-Host "Done." -ForegroundColor Green

