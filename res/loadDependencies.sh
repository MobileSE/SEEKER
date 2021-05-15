#!/bin/bash

mvn install:install-file -Dfile=./res/soot-infoflow-android-classes.jar -DgroupId=de.tud.sse -DartifactId=soot-infoflow-android -Dversion=2.10.0 -Dpackaging=jar
mvn install:install-file -Dfile=./res/soot-infoflow-classes.jar -DgroupId=de.tud.sse -DartifactId=soot-infoflow -Dversion=2.10.0 -Dpackaging=jar

