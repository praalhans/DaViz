DaViz - Distributed Algorithms Visualization
============================================

DaViz is a tool for simulating and visualizing distributed algorithms.

## Build project

Requirements for building the project are:

- Java 8 development kit, for example OpenJDK1.8.0
- A recent Ant version, for example Ant 1.10

In a shell, run the following command:

    > ant

This will compile the Frege source files, the Java source files, and create a self-contained `.jar` file in `build/jar`. 

To start the application, run the following command:

    > java -jar build/jar/DaViz.jar
