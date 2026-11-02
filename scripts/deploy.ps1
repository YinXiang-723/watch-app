# Install a cloud-built APK onto the watch and launch it.
#
# Usage:
#   .\deploy.ps1 -Apk .\app-debug.apk
#   .\deploy.ps1 -Apk .\app-debug.apk -Serial 192.168.0.116:5555
#
# This script does NOT compile anything. The APK comes from the
# "watch-debug-apk" artifact of the GitHub Actions run.
# NOTE: keep this file ASCII-only, otherwise Windows PowerShell 5.1
# may mis-parse UTF-8 Chinese text without a BOM.

param(
    [Parameter(Mandatory = $true)][string]$Apk,
    [string]$Serial = "",
    [string]$Adb = ""
)

$ErrorActionPreference = 'Stop'

$pkg = 'com.example.watchdemo'
$activity = "$pkg/.MainActivity"

if (-not (Test-Path -LiteralPath $Apk)) { throw "APK not found: $Apk" }

function Resolve-Adb([string]$explicit) {
    if ($explicit) { return $explicit }
    $candidates = @(
        (Join-Path $PSScriptRoot '..\..\tools\platform-tools\adb.exe'),
        (Join-Path $PSScriptRoot '..\tools\platform-tools\adb.exe')
    )
    foreach ($c in $candidates) {
        if (Test-Path -LiteralPath $c) { return (Resolve-Path -LiteralPath $c).Path }
    }
    $cmd = Get-Command 'adb' -ErrorAction SilentlyContinue
    if ($cmd) { return $cmd.Source }
    throw "adb not found. Pass -Adb <path>, or add tools\platform-tools to PATH."
}

$adb = Resolve-Adb $Adb
Write-Host "adb : $adb"
Write-Host "apk : $Apk"

$sel = @()
if ($Serial) { $sel = @('-s', $Serial) }

Write-Host ""
Write-Host "== devices =="
& $adb @sel devices

Write-Host ""
Write-Host "== install =="
& $adb @sel install -r $Apk
if ($LASTEXITCODE -ne 0) {
    throw "Install failed (exit=$LASTEXITCODE). Typical causes: signature mismatch (run 'adb uninstall $pkg' first), minSdk too high, truncated APK."
}

Write-Host ""
Write-Host "== launch =="
& $adb @sel shell am start -n $activity

& $adb @sel logcat -c | Out-Null
Write-Host ""
Write-Host "Installed and launched. Tail logs with:"
$prefix = if ($Serial) { "-s $Serial " } else { "" }
Write-Host "  & '$adb' ${prefix}logcat -v time | Select-String '$pkg|AndroidRuntime'"
