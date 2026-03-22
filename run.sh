#!/bin/bash
# Recompile the Java files just in case
javac -d out $(find src/main/java -name "*.java")

# Run the Stock Manager App
java -cp "out:lib/mysql-connector-j-8.3.0.jar" com.stockmanager.app.StockManagerApp
