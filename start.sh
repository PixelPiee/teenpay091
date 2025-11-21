#!/bin/bash
javac -d out src/com/teenupi/*.java src/com/teenupi/model/*.java src/com/teenupi/service/*.java
java -cp out com.teenupi.TeenPayApp
