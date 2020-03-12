DaViz - Distributed Algorithms Visualization
============================================

DaViz is a tool for simulating and visualizing distributed algorithms.

[![Build Status](https://travis-ci.com/praalhans/DaViz.svg?branch=master)](https://travis-ci.com/praalhans/DaViz)

## Installation and starting the tool

Ensure you have a Java 8 runtime installed on your machine, or a Java runtime that is compatible with Java 8.
For Windows, Mac OS X, and Linux, you can download [Oracle's Java SE Runtime Environment 8](https://www.oracle.com/java/technologies/javase-jre8-downloads.html).
On Linux, you can alternatively install the open source OpenJDK 1.8.0 following the usual installation instructions from your distribution: e.g. [ArchLinux](https://www.archlinux.org/packages/extra/x86_64/jre8-openjdk/), [Debian](https://wiki.debian.org/Java/), [Fedora](https://docs.fedoraproject.org/en-US/quick-docs/installing-java/), [Ubuntu](https://packages.ubuntu.com/xenial/openjdk-8-jre).

Download the latest released JAR-file on the [release page](https://github.com/praalhans/DaViz/releases). You can start the application with the following command:

    > java -jar DaViz.jar

## User manual

For a detailed manual, covering the use of the tool, please see the [User Manual](https://github.com/praalhans/DaViz/wiki).

## Building the project yourself

If you want to contribute to the project, first set up a development environment. The requirements for building this project are:

- Java 8 development kit, for example OpenJDK1.8.0
- A recent Ant version, for example Ant 1.10

The project makes use of [Frege, a Haskell for the JVM](https://github.com/Frege/frege). The necessary library is included in the `lib` folder, we use version 3.24.405.

Open a shell, and run the following command:

    > ant

This will compile the Frege source files, the Java source files, and create a self-contained `.jar` file in `build/jar`. 

To start the application, run the following command:

    > java -jar build/jar/DaViz.jar
