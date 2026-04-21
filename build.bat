@echo off
echo ========================================
echo  Tunnelgraeber Plugin - Build Script
echo ========================================
echo.

REM Pfad zur spigot.jar anpassen!
SET SPIGOT_JAR=spigot.jar
SET OUTPUT=TunnelgraberPlugin.jar

REM Prüfe ob spigot.jar vorhanden
IF NOT EXIST "%SPIGOT_JAR%" (
    echo FEHLER: %SPIGOT_JAR% nicht gefunden!
    echo Bitte kopiere deine spigot.jar in diesen Ordner
    echo oder passe den Pfad in dieser Datei an.
    pause
    exit /b 1
)

REM Prüfe ob javac vorhanden
javac -version >nul 2>&1
IF ERRORLEVEL 1 (
    echo FEHLER: javac nicht gefunden!
    echo Bitte JDK installieren: https://adoptium.net
    pause
    exit /b 1
)

echo Kompiliere Plugin...
mkdir classes 2>nul

javac -cp "%SPIGOT_JAR%" -d classes -encoding UTF-8 src\de\tunnelgraber\*.java

IF ERRORLEVEL 1 (
    echo.
    echo FEHLER beim Kompilieren!
    pause
    exit /b 1
)

echo Erstelle JAR...
copy plugin.yml classes\ >nul
jar cf %OUTPUT% -C classes .

echo.
echo ========================================
echo  FERTIG! %OUTPUT% wurde erstellt.
echo  Kopiere die JAR in deinen plugins Ordner
echo ========================================
pause
