# Changelog

## v2.0.0-rc.0

Date: 23 Feb 2020

This pre-release perform an entire refactoring in the simulation and glue module to prepare the application for a native Java implementation of the simulation.

The Frege simulation can still be used but the Java simulation module is however, not initiated yet.

### Features

- [#16](https://github.com/praalhans/DaViz/pull/16) - Prepare application to receive a java port

## v1.0.1

Date 8 Jan 2020

This release provides minor fixes and improvements, ensuring that the Tree and Echo algorithm can be simulated.

### Fix

- [#3](https://github.com/praalhans/DaViz/pull/3) - Attempt to fix class path libraries for any contributor 
- [#4](https://github.com/praalhans/DaViz/pull/4) - Fixes exceptions when trying to run simulations using the tree algorithm
- [#10](https://github.com/praalhans/DaViz/pull/10) - Enable Echo algorithm 

### Docummentation

- [8](https://github.com/praalhans/DaViz/pull/8) - Describe Frege implementation of Tarry and Tree algorithms

## v1.0.0

Date: 9 Dec 2019

This release sets the beginning of versioning control and provides a functional prototype of DaViz and allows users to visualize and simulate the following wave algorithms:

Awerbuch
Cidon
DFS ( + Visited)
Echo
Tarry
Tree (+ Ack)
