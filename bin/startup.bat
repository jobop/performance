@echo off
java  -DappHome="..\conf"  -jar ..\lib\performance-${project.version}.jar -t 1 -c 1 -l D:\\recorde.log
pause