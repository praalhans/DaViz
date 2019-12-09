DaViz - Distributed Algorithms Visualization
============================================

This is the Haskell implementation.

For the Java implementation, see the other project files.

The Frege port is a mirror of the main sources, made to compile with
the Frege implementation of Haskell running on the JVM. The project
is an Eclipse project. A recent version of Java is required (Java 8 or
higher), and the following plug-in is used:

Frege Development	Version 3.24.93

This can be obtained from: https://github.com/Frege/eclipse-plugin

The project can be imported in the Eclipse workspace by File > Import
select General > Existing Project... and select the
/java/ folder. The "DaVizFrege" project is for the Frege port
and "DaViz" for Swing sources. Both should be imported. If Eclipse is
installed with EGit, and the selected sources are under source control,
you may also commit changes from Eclipse.

Due to the nature of the Frege plug-in, it may be necessary to delete
a faulty reference to the Frege library, that points to a Windows path
on the author's computer: context menu of DaVizFrege, select
Properties and choose Java Build Path and the tab Libraries. Remove the
offending fregec.jar reference. Afterwards, select the
"Enable Frege Builder" action in the context menu of the project.

This should also be done for the "DaViz" project, if necessary, to
enable running Frege compiled sources. However, only adding a Frege
run-time library to that project is also sufficient, but left
unexplored at the time of writing.
