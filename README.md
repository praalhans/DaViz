# DaViz - Distributed Algorithms Visualization

DaViz is a software tool written in the Haskell and Java programming languages for simulating and visualizing executions of distributed algorithms.

## Getting Started

The steps below explain how to setup and run DaViz. For a detailed description of the software, refer to the [documentation](doc) directory.

### Prerequisites

- [Java](https://www.oracle.com/java/technologies/javase-downloads.html) v11.0.1+
- [Frege](https://github.com/Frege/frege) v3.25.42+ (available in [lib](lib/) directory)
- [IntelliJ IDEA](https://www.jetbrains.com/idea/) (Community version is enough)

### Setup

1. Clone the repository using `git clone git@github.com:praalhans/DaViz.git`
1. Open the project in IntelliJ IDEA 

### Build and Execution

The dependencies between modules are already configured and shared in the repository.

Base build and application (run) configurations are already available in the project and can be simply executed to build or start the application. The following configurations are available:

- Build Frege source: Generate intermediate Java files for frege implementation in the SimulationFrege module. Available for Zsh and PowerShell (**Not tested**). 
- Run DaViz: Invoke the IDE build/run for the main executable class of the project.
- Run DaViz and build Frege source: Run the two configurations above. 

These configurations were verified to work for Mac OS and Linux distributions. They were **not tested** yet on Windows.

Currently, Frege source is build by running the [frege-build](scripts/build-frege.sh) script. The core files that contains dependencies are defined in `FREGE_CORE_SOURCES_TO_COMPILE` in the correct compilation order. For the remaining files, we specify their directories in `FREGE_DIR_TO_COMPILE`. These files are compiled using the Frege JAR located in the `lib` directory and generate java and class files in the `src` directory, to remove the unknown references errors displayed in the IDE. Once all files are compiled, the class files are removed. If new Frege files are created, they need to be added in the script.

Although this workflow overhead is minimal since the project is small, we know that this solution is not optimal, thus better alternatives are welcome. Considered alternatives to this process could be:

- Using the IntelliJ Frege compiler plugin or Maven dependency &#8594; Not recommended as both are outdated and likely unstable.
- Enhancing the script to be more automated and require less manual changes
- Verify the possibility of using proper build tools, such as Gulp or Maven

### Generating Executable JAR

Configurations to generating executable JARs are also provided. To run these configurations, go to `Build > Build artifacts` and select the desired configuration. Currently, there are 2 configurations: a Java native solution and a Java-Haskell/Frege solution. The resulting build will be located at `out/artifacts`.

For additional configurations, refers to the [Package your application in a JAR](https://www.jetbrains.com/help/idea/packaging-a-module-into-a-jar-file.html) docs.

### Haskell

For learning Haskell, refer to:

- [Haskell Tutorial by Derek Banas](https://www.youtube.com/watch?v=02_H3LjqMr8) - YouTube Video - Great introduction to Haskell.
- [Learn You a Haskell by miran lipovača](http://learnyouahaskell.com/) - Free online or paid printable Book - Great coverage fo Haskell, from basics to advanced scenarios.
- [Haskell Documentation](https://www.haskell.org/documentation/)
- [The Haskell Cheatsheet by Justin Bailey](https://cheatsheet.codeslower.com/)
- [Introduction to Functional Programming](https://courses.edx.org/courses/course-v1:DelftX+FP101x+3T2015/course/) - FreeEdx course to learn functional programming focused on Haskell.

## Modules

This project is organized using the [Modules](https://www.jetbrains.com/help/idea/creating-and-managing-modules.html) structure from IntelliJ IDEA. Currently, there is 1 UI module (Swing) and 3 simulation modules (Core, Java and Frege). While there is just one UI module, it is possible to run DaViz as a native Java solution using the Java module or as a Java-Haskell solution using the Frege module. The Core module provides shared classes between the Java and Frege modules.

UI modules:

- Swing

Simulation modules:

- Core
- Frege
- Java (**Not ready for use**)

## Contributing

Please read [CONTRIBUTING.md](CONTRIBUTING.md) for details on our code of conduct, and the process for submitting pull requests to us.

## Versioning

We use [SemVer](http://semver.org/) for versioning. For the versions available, see the [tags on this repository](https://github.com/praalhans/DaViz/tags). 


## Changelog

See the [CHANGELOG](CHANGELOG.md) to learn about the latest improvements.

## Authors

- Hans-Dieter Hiep - [praalhans](https://github.com/praalhans) - <hdh@cwi.nl> - Centrum Wiskunde & Informatica
- Wesley Shann - [sshann](https://github.com/sshann) - <w.genizshann@student.vu.nl> - Vrije Universiteit, Amsterdam

See also the list of [contributors](https://github.com/praalhans/DaViz/graphs/contributors) who participated in this project.

## License

This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details

## Acknowledgments

- prof. dr. Wan J. Fokkink
