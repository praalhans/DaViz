#!/usr/bin/env bash
# Can run on zsh as well

DIR_FREGE="../DaVizSimulationFrege/src/com/aexiz/daviz/frege"
DIR_FREGE_SIMULATION=$DIR_FREGE"/simulation"
DIR_FREGE_SIMULATION_ALGORITHM=$DIR_FREGE_SIMULATION"/algorithm"

DIR_OUTPUT="../DaVizSimulationFrege/src/"

JAR_FREGE="../DaVizSimulationFrege/lib/frege3.25.84.jar"
BASE_DIR=$PWD

find $DIR_FREGE -name "*.java" -type f -delete

# TODO Replace this quick fix for
#  (A) a fully automated script (e.g receive DIR_FREGE directory and compile without defining orders)
#  (B) Frege compiler plugin for IntelliJ (if not outdated)
# Note that the current solution only generates intermediate java files in the src
# This is done to fix the "undefined references" displayed in the IDE (not needed to run the app though)
# The class files will be generated later when the IDE builds
FREGE_CORE_SOURCES_TO_COMPILE=(
  "${DIR_FREGE_SIMULATION}/Set.fr"
  "${DIR_FREGE_SIMULATION}/Graph.fr"
  "${DIR_FREGE_SIMULATION}/Process.fr"
  "${DIR_FREGE_SIMULATION}/Visited.fr"
  "${DIR_FREGE_SIMULATION}/Event.fr"
  "${DIR_FREGE_SIMULATION}/Simulation.fr"
)

FREGE_DIR_TO_COMPILE=(
  "${DIR_FREGE_SIMULATION_ALGORITHM}/wave"
)

for FILE_NAME in "${FREGE_CORE_SOURCES_TO_COMPILE[@]}"
do
	java -Xss1m \
      -jar $JAR_FREGE  \
      -d $DIR_OUTPUT \
      "$FILE_NAME"
done

for DIRECTORY in "${FREGE_DIR_TO_COMPILE[@]}"
do
  cd "$DIRECTORY" || exit 1

  java -Xss1m \
      -jar "${BASE_DIR}/${JAR_FREGE}"  \
      -d "${BASE_DIR}/${DIR_OUTPUT}" \
      *.fr

  cd "$BASE_DIR" || exit 1
done


find $DIR_FREGE -name "*.class" -type f -delete