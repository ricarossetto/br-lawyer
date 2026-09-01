# =============================================================================
# BR-LAWYER: Modern Web Client One-Command Developer Launcher
# Usage: .\scripts\dev-web.ps1
# =============================================================================

$ErrorActionPreference = "Stop"

Write-Host "============================================================" -ForegroundColor Cyan
Write-Host "  BR-LAWYER Modern Web Client Developer Launcher" -ForegroundColor Cyan
Write-Host "============================================================" -ForegroundColor Cyan

# 1. Locate Docker CLI
$dockerExe = "C:\Users\Ricardo PC\AppData\Local\Programs\DockerDesktop\resources\bin\docker.exe"
if (-not (Test-Path $dockerExe)) {
    $dockerCmd = Get-Command docker -ErrorAction SilentlyContinue
    if ($dockerCmd) {
        $dockerExe = $dockerCmd.Source
    } else {
        Write-Error "Docker executable not found."
    }
}

# 2. Ensure Docker Daemon is Running
Write-Host "[1/4] Checking Docker daemon status..." -ForegroundColor Yellow
$daemonRunning = $false
try {
    $null = & $dockerExe ps 2>&1
    if ($LASTEXITCODE -eq 0) { $daemonRunning = $true }
} catch {}

if (-not $daemonRunning) {
    Write-Host "Docker daemon is not running. Starting Docker Desktop..." -ForegroundColor Yellow
    $dockerDesktop = "C:\Users\Ricardo PC\AppData\Local\Programs\DockerDesktop\Docker Desktop.exe"
    if (Test-Path $dockerDesktop) {
        Start-Process -FilePath $dockerDesktop -WorkingDirectory "C:\Users\Ricardo PC\AppData\Local\Programs\DockerDesktop"
    }
    
    $maxTries = 40
    for ($i = 1; $i -le $maxTries; $i++) {
        Start-Sleep -Seconds 2
        try {
            $null = & $dockerExe ps 2>&1
            if ($LASTEXITCODE -eq 0) {
                $daemonRunning = $true
                Write-Host "Docker daemon connected!" -ForegroundColor Green
                break
            }
        } catch {}
        Write-Host "Waiting for Docker daemon bridge... ($i/$maxTries)" -ForegroundColor Gray
    }
}

if (-not $daemonRunning) {
    Write-Error "Could not connect to Docker daemon."
}

# 3. Start Required Containers (db, br-lawyer-server)
Write-Host "[2/4] Ensuring database and WildFly containers are running..." -ForegroundColor Yellow
$containers = & $dockerExe ps -a --format "{{.Names}}"

if ($containers -match "db") {
    & $dockerExe start db | Out-Null
    Write-Host "Container 'db' (MariaDB) is running." -ForegroundColor Green
} else {
    Write-Host "Container 'db' not found. Please initialize docker compose." -ForegroundColor Red
}

if ($containers -match "br-lawyer-server") {
    & $dockerExe start br-lawyer-server | Out-Null
    Write-Host "Container 'br-lawyer-server' (WildFly) is running." -ForegroundColor Green
} else {
    Write-Host "Container 'br-lawyer-server' not found. Please initialize docker compose." -ForegroundColor Red
}

# 4. Wait for WildFly REST API Readiness on Port 8000
Write-Host "[3/4] Probing backend REST API readiness (http://localhost:8000)..." -ForegroundColor Yellow
$backendReady = $false
for ($i = 1; $i -le 30; $i++) {
    try {
        $tcpClient = New-Object System.Net.Sockets.TcpClient
        $asyncResult = $tcpClient.BeginConnect("127.0.0.1", 8000, $null, $null)
        $success = $asyncResult.AsyncWaitHandle.WaitOne(1000, $false)
        if ($success -and $tcpClient.Connected) {
            $tcpClient.EndConnect($asyncResult)
            $tcpClient.Close()
            $backendReady = $true
            Write-Host "Backend REST API port 8000 is ready and listening!" -ForegroundColor Green
            break
        }
        $tcpClient.Close()
    } catch {}
    Write-Host "Waiting for WildFly port 8000... ($i/30)" -ForegroundColor Gray
    Start-Sleep -Seconds 2
}

if (-not $backendReady) {
    Write-Warning "Backend port 8000 did not respond in time. Proceeding with frontend launch..."
}

# 5. Launch Vite Development Server
Write-Host "[4/4] Starting Vite development server (http://localhost:3000)..." -ForegroundColor Cyan
$frontendDir = Join-Path $PSScriptRoot "..\j-lawyer-web\frontend"
Set-Location $frontendDir

& npm run dev
