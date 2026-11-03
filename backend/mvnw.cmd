@echo off
rem Maven Wrapper simple (Windows, cmd o PowerShell): la primera vez descarga Maven en
rem %USERPROFILE%\.m2\wrapper y despues lo reutiliza. Requiere Java 17 o mas nuevo.
rem Uso, parados en la carpeta backend:   mvnw spring-boot:run
setlocal
set "MAVEN_VERSION=3.9.11"
set "WRAPPER_DIR=%USERPROFILE%\.m2\wrapper"
set "MAVEN_CMD=%WRAPPER_DIR%\apache-maven-%MAVEN_VERSION%\bin\mvn.cmd"

if exist "%MAVEN_CMD%" goto ejecutar

echo Descargando Maven %MAVEN_VERSION% (solo la primera vez)...
powershell -NoProfile -ExecutionPolicy Bypass -Command "$ErrorActionPreference = 'Stop'; [Net.ServicePointManager]::SecurityProtocol = [Net.SecurityProtocolType]::Tls12; $zip = Join-Path $env:TEMP 'apache-maven-%MAVEN_VERSION%-bin.zip'; Invoke-WebRequest -UseBasicParsing -Uri 'https://repo.maven.apache.org/maven2/org/apache/maven/apache-maven/%MAVEN_VERSION%/apache-maven-%MAVEN_VERSION%-bin.zip' -OutFile $zip; New-Item -ItemType Directory -Force -Path '%WRAPPER_DIR%' | Out-Null; Expand-Archive -Force -Path $zip -DestinationPath '%WRAPPER_DIR%'; Remove-Item $zip"
if errorlevel 1 (
    echo No se pudo descargar Maven. Revisa la conexion a internet.
    exit /b 1
)

:ejecutar
call "%MAVEN_CMD%" %*
exit /b %ERRORLEVEL%
