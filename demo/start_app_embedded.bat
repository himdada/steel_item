@echo off
title Steel App (Embedded Mode - No Install)
echo Starting Steel Application with Embedded MongoDB...
echo No external database installation required.
echo.
java -Dspring.profiles.active=embedded -jar target/demo-0.0.1-SNAPSHOT.jar
pause