@echo off
echo %0
echo %1
cd /d C:\Users\user\workspace\CheckNew\7-Zip
7z x %1\*.7z  -aoa -o%1
del "%1\*.7z"
del "%1\ok"