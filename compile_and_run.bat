@echo off
echo Compiling Java files...
if not exist out mkdir out
javac -d out src/com/teenupi/*.java src/com/teenupi/model/*.java src/com/teenupi/service/*.java

if %errorlevel% neq 0 (
    echo Compilation failed!
    pause
    exit /b %errorlevel%
)

echo Starting TeenPay App...
java -cp out com.teenupi.TeenPayApp
