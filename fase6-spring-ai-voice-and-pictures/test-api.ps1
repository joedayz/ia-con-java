# =============================================
# test-api.ps1 — Fase 6: Spring AI Voice & Pictures
# =============================================
# Uso: .\test-api.ps1

$BASE_URL = "http://localhost:8080"

Write-Host "======================================" -ForegroundColor Cyan
Write-Host " Fase 6 — Spring AI Voice & Pictures " -ForegroundColor Cyan
Write-Host "======================================" -ForegroundColor Cyan
Write-Host ""

# ---- TTS ----
Write-Host ">>> [1/5] Text-to-Speech: convirtiendo texto a audio..." -ForegroundColor Yellow
$ttsUrl = "$BASE_URL/api/voice/tts?text=Hola%2C+bienvenido+a+la+fase+6+de+IA+con+Java&voice=nova"
Invoke-WebRequest -Uri $ttsUrl -Method POST -OutFile "$env:TEMP\fase6-speech.mp3"
if (Test-Path "$env:TEMP\fase6-speech.mp3") {
    Write-Host "    Audio guardado en $env:TEMP\fase6-speech.mp3" -ForegroundColor Green
} else {
    Write-Host "    ERROR: no se generó el audio" -ForegroundColor Red
}
Write-Host ""

# ---- Transcripción ----
if (Test-Path "$env:TEMP\fase6-speech.mp3") {
    Write-Host ">>> [2/5] Speech-to-Text: transcribiendo audio..." -ForegroundColor Yellow
    $form = @{ audio = Get-Item "$env:TEMP\fase6-speech.mp3" }
    $transcription = Invoke-RestMethod -Uri "$BASE_URL/api/voice/transcribe" -Method POST -Form $form
    Write-Host "    Transcripción: $transcription" -ForegroundColor Green
} else {
    Write-Host ">>> [2/5] Speech-to-Text: OMITIDO" -ForegroundColor DarkGray
}
Write-Host ""

# ---- Imagen URL ----
Write-Host ">>> [3/5] Image generate-url: generando imagen con DALL-E 3..." -ForegroundColor Yellow
$imgUrlResp = Invoke-RestMethod -Uri "$BASE_URL/api/image/generate-url?prompt=Un+gato+astronauta+flotando+en+el+espacio%2C+estilo+acuarela" -Method POST
Write-Host "    URL: $($imgUrlResp.url.Substring(0, [Math]::Min(80, $imgUrlResp.url.Length)))..." -ForegroundColor Green
Write-Host ""

# ---- Imagen PNG ----
Write-Host ">>> [4/5] Image generate-png: descargando PNG..." -ForegroundColor Yellow
Invoke-WebRequest -Uri "$BASE_URL/api/image/generate-png?prompt=Una+ciudad+futurista+al+atardecer%2C+estilo+cyberpunk" -Method POST -OutFile "$env:TEMP\fase6-image.png"
if (Test-Path "$env:TEMP\fase6-image.png") {
    $size = (Get-Item "$env:TEMP\fase6-image.png").Length
    Write-Host "    PNG guardado en $env:TEMP\fase6-image.png ($size bytes)" -ForegroundColor Green
} else {
    Write-Host "    ERROR: no se generó la imagen" -ForegroundColor Red
}
Write-Host ""

# ---- Vision ----
if (Test-Path "$env:TEMP\fase6-image.png") {
    Write-Host ">>> [5/5] Vision: analizando la imagen..." -ForegroundColor Yellow
    $form = @{
        image    = Get-Item "$env:TEMP\fase6-image.png"
        question = "Describe esta imagen en español y menciona los colores principales."
    }
    $visionResp = Invoke-RestMethod -Uri "$BASE_URL/api/vision/describe" -Method POST -Form $form
    Write-Host "    Descripción: $($visionResp.description)" -ForegroundColor Green
} else {
    Write-Host ">>> [5/5] Vision: OMITIDO" -ForegroundColor DarkGray
}
Write-Host ""

Write-Host "======================================" -ForegroundColor Cyan
Write-Host " Tests completados" -ForegroundColor Cyan
Write-Host "======================================" -ForegroundColor Cyan
