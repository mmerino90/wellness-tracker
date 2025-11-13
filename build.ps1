#!/usr/bin/env powershell
# Wellness Tracker - Build Script
# This script compiles the project with JavaFX support

param(
    [string]$JavaFXVersion = "21.0.1",
    [string]$Mode = "compile"
)

$ErrorActionPreference = "Stop"

# Configuration
$ProjectRoot = (Get-Location).Path
$SrcDir = Join-Path $ProjectRoot "src\main"
$TestDir = Join-Path $ProjectRoot "src\tests"
$BinDir = Join-Path $ProjectRoot "bin"
$LibDir = Join-Path $ProjectRoot "lib"
$SourcesFile = Join-Path $ProjectRoot "sources.txt"

# Create necessary directories
if (-not (Test-Path $BinDir)) {
    New-Item -ItemType Directory -Path $BinDir -Force | Out-Null
    Write-Host "✓ Created bin directory"
}

if (-not (Test-Path $LibDir)) {
    New-Item -ItemType Directory -Path $LibDir -Force | Out-Null
    Write-Host "✓ Created lib directory"
}

# Generate source file list
Write-Host "`n📝 Generating source file list..."
$javaFiles = @()
$javaFiles += (Get-ChildItem -Path $SrcDir -Filter "*.java" -Recurse | Select-Object -ExpandProperty FullName)
$javaFiles | Out-File -FilePath $SourcesFile -Encoding UTF8 -Force
Write-Host "✓ Found $($javaFiles.Count) source files"

# Try to find JavaFX SDK
Write-Host "`n🔍 Locating JavaFX SDK..."
$javaFxPaths = @(
    "C:\Program Files\javafx-sdk-$JavaFXVersion\lib",
    "C:\Program Files (x86)\javafx-sdk-$JavaFXVersion\lib",
    "$env:LOCALAPPDATA\javafx-sdk-$JavaFXVersion\lib",
    "$env:HOME\javafx-sdk-$JavaFXVersion\lib"
)

$javaFxLib = $null
foreach ($path in $javaFxPaths) {
    if (Test-Path $path) {
        $javaFxLib = $path
        Write-Host "✓ Found JavaFX SDK at: $path"
        break
    }
}

if (-not $javaFxLib) {
    Write-Host "⚠️  JavaFX SDK not found in standard locations"
    Write-Host "Attempting compilation without module-path (will only work for non-JavaFX code)"
    Write-Host "`nTo fix this, download JavaFX SDK from https://gluonhq.com/products/javafx/"
    Write-Host "Extract it and place in one of these locations:"
    $javaFxPaths | ForEach-Object { Write-Host "  - $_" }
}

# Compile
Write-Host "`n🔨 Compiling Java files..."
$compileArgs = @(
    "-d", $BinDir,
    "-encoding", "UTF-8",
    "-source", "21",
    "-target", "21",
    "@$SourcesFile"
)

if ($javaFxLib) {
    $compileArgs = @(
        "-d", $BinDir,
        "--module-path", $javaFxLib,
        "--add-modules", "javafx.controls,javafx.fxml,javafx.graphics",
        "-encoding", "UTF-8",
        "-source", "21",
        "-target", "21",
        "@$SourcesFile"
    )
}

& javac $compileArgs
if ($LASTEXITCODE -ne 0) {
    Write-Host "❌ Compilation failed"
    exit 1
}

Write-Host "✓ Compilation successful"
Write-Host "`n✅ Build complete! Output in: $BinDir"
