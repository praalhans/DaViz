DaViz - Distributed Algorithms Visualization
============================================

This is the Java implementation consisting of the components:
- Controller, Glue, GUI

For the Haskell implementation, see the other project files.

Please also see the accompanying technical report for an overview
of this project. This file will only contain information regarding
the build of a deployable software artifact.

This project is built using Eclipse. The project depends on the
Frege project (DaVizFrege), and every time a change in the Frege
sources occurs, this project needs to be refreshed to expose the
compiled sources to the Swing project (DaViz).

It may be necessary to readd the DaVizFrege project to the classpath.
Open the Properties (context menu and select Properties...) and navigate
to Java Build Path in the tab Libraries. Remove the reference to
DaVizFrege and readd it by selecting "Add Class Folder..." and select
the "bin" folder of the project that is also opened in the workspace.

A run configuration for the main class com.aexiz.daviz.ControlFrame
should be created by usual means (e.g. selecting the class and selecting
the Run action on the toolbar).

If one wishes to export a Runnable JAR file with the visualizer and
Frege implementation all-in-one, select "Export..." from the context
menu of the "DaViz" project and select Java > Runnable JAR file. Select
Next and choose the launch configuration created before and an export
destination. Extract the required libraries into the generated JAR.

The resulting JAR file is too big, as it also contains a Frege compiler.
Open the exported JAR with a ZIP-file utility, like 7-Zip, and delete
the following folders:

/bin/							contains Frege intermediate sources
/frege/compiler					contains Frege compiler
/frege/ide						contains Frege IDE tools
/frege/test						contains Frege tests
/frege/tools					contaisn Frege documentation tools

This will reduce the resulting JAR file from approx. 32 MB to approx.
10 MB. Furhter reductions in size may be possible, but left unexplored
at the time of writing.
