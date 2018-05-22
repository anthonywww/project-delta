@echo off

REM Change to current working directory
cd /D %~dp0

REM launch executable
java -jar dist/compiled.jar

echo.
echo ^> Press [ENTER] to quit.
pause>nul
