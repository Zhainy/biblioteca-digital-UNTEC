[CmdletBinding()]
param(
    [string]$ProjectPath = "D:\Estudios\Fullstack Java\ABP5\BibliotecaDigitalUNTEC",
    [string]$TomcatPath = "C:\servers\Tomcat 11.0",
    [string]$AppName = "BibliotecaDigitalUNTEC",
    [string]$ServiceName = "Tomcat11",
    [int]$Port = 9090,
    [string]$HealthPath = "login",
    [int]$ServiceTimeoutSeconds = 30,
    [int]$HttpTimeoutSeconds = 45,
    [switch]$SkipBuild,
    [switch]$DryRun
)

$ErrorActionPreference = "Stop"
Set-StrictMode -Version Latest

function Write-Step {
    param(
        [string]$Message,
        [string]$Level = "INFO"
    )
    $ts = Get-Date -Format "yyyy-MM-dd HH:mm:ss"
    $color = switch ($Level) {
        "WARN" { "Yellow" }
        "ERROR" { "Red" }
        default { "Cyan" }
    }
    Write-Host "[$ts] [$Level] [redeploy] $Message" -ForegroundColor $color
}

function Invoke-Action {
    param(
        [string]$Description,
        [scriptblock]$Action
    )

    Write-Step $Description
    if ($DryRun) {
        Write-Step "DryRun activo: se omite ejecucion" "WARN"
        return
    }

    & $Action
}

function Wait-ServiceState {
    param(
        [string]$Name,
        [string]$Desired,
        [int]$TimeoutSeconds
    )

    $deadline = (Get-Date).AddSeconds($TimeoutSeconds)
    do {
        $svc = Get-Service -Name $Name -ErrorAction Stop
        if ($svc.Status.ToString() -eq $Desired) {
            return
        }
        Start-Sleep -Milliseconds 500
    } while ((Get-Date) -lt $deadline)

    $current = (Get-Service -Name $Name -ErrorAction Stop).Status
    throw "Timeout esperando servicio '$Name' en estado '$Desired'. Estado actual: '$current'."
}

function Wait-HttpReady {
    param(
        [string]$Url,
        [int]$TimeoutSeconds
    )

    $deadline = (Get-Date).AddSeconds($TimeoutSeconds)
    do {
        try {
            $res = Invoke-WebRequest -Uri $Url -UseBasicParsing -TimeoutSec 5
            if ($res.StatusCode -ge 200 -and $res.StatusCode -lt 400) {
                return $res.StatusCode
            }
        }
        catch {
            # Reintenta hasta timeout
        }
        Start-Sleep -Seconds 1
    } while ((Get-Date) -lt $deadline)

    throw "La app no respondio correctamente en '$Url' dentro de $TimeoutSeconds segundos."
}

$warSource = Join-Path $ProjectPath ("target\{0}.war" -f $AppName)
$warDest = Join-Path $TomcatPath ("webapps\{0}.war" -f $AppName)
$expandedDir = Join-Path $TomcatPath ("webapps\{0}" -f $AppName)
$workDir = Join-Path $TomcatPath ("work\Catalina\localhost\{0}" -f $AppName)
$tempDir = Join-Path $TomcatPath "temp"
$healthUrl = "http://localhost:$Port/$AppName/$HealthPath"

Write-Step "Iniciando redeploy de $AppName"
Write-Step "Proyecto: $ProjectPath"
Write-Step "Tomcat: $TomcatPath"
Write-Step "Servicio: $ServiceName"
Write-Step "Health check: $healthUrl"

if (-not (Test-Path $ProjectPath)) {
    throw "No existe el proyecto en: $ProjectPath"
}
if (-not (Test-Path $TomcatPath)) {
    throw "No existe Tomcat en: $TomcatPath"
}
if (-not (Test-Path (Join-Path $TomcatPath "webapps"))) {
    throw "No existe carpeta webapps en: $TomcatPath"
}

$null = Get-Service -Name $ServiceName -ErrorAction Stop

if (-not $SkipBuild) {
    Invoke-Action "Compilando WAR con Maven (clean package)" {
        Push-Location $ProjectPath
        try {
            & mvn clean package
            if ($LASTEXITCODE -ne 0) {
                throw "Maven fallo con codigo $LASTEXITCODE"
            }
        }
        finally {
            Pop-Location
        }
    }
}
else {
    Write-Step "SkipBuild activo: se omite compilacion" "WARN"
}

if (-not (Test-Path $warSource)) {
    throw "No se encontro el WAR generado en: $warSource"
}

Invoke-Action "Deteniendo servicio $ServiceName (si esta en ejecucion)" {
    $svc = Get-Service -Name $ServiceName -ErrorAction Stop
    if ($svc.Status -ne "Stopped") {
        Stop-Service -Name $ServiceName -Force -ErrorAction Stop
        Wait-ServiceState -Name $ServiceName -Desired "Stopped" -TimeoutSeconds $ServiceTimeoutSeconds
    }
}

Invoke-Action "Eliminando despliegue expandido anterior" {
    Remove-Item $expandedDir -Recurse -Force -ErrorAction SilentlyContinue
}

Invoke-Action "Eliminando WAR previo en Tomcat" {
    Remove-Item $warDest -Force -ErrorAction SilentlyContinue
}

Invoke-Action "Limpiando cache de work de Tomcat" {
    Remove-Item $workDir -Recurse -Force -ErrorAction SilentlyContinue
}

Invoke-Action "Limpiando carpeta temp de Tomcat" {
    Remove-Item (Join-Path $tempDir "*") -Recurse -Force -ErrorAction SilentlyContinue
}

Invoke-Action "Copiando WAR nuevo a webapps" {
    Copy-Item $warSource $warDest -Force
    $warInfo = Get-Item $warDest
    Write-Step "WAR copiado: $($warInfo.FullName) (LastWriteTime: $($warInfo.LastWriteTime))"
}

Invoke-Action "Iniciando servicio $ServiceName" {
    Start-Service -Name $ServiceName -ErrorAction Stop
    Wait-ServiceState -Name $ServiceName -Desired "Running" -TimeoutSeconds $ServiceTimeoutSeconds
}

Invoke-Action "Verificando disponibilidad HTTP" {
    $status = Wait-HttpReady -Url $healthUrl -TimeoutSeconds $HttpTimeoutSeconds
    Write-Step "Health check OK: HTTP $status en $healthUrl"
}

Write-Step "Redeploy finalizado correctamente"
Write-Step "URL esperada: $healthUrl"
