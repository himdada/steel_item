@echo off
title Steel App (Default - External MongoDB)
echo Starting Steel Application connecting to External MongoDB...
echo Ensure MongoDB is running on localhost:27017 or configured in application-external.properties
echo.
java -jar target/demo-0.0.1-SNAPSHOT.jar
pause