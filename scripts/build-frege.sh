#!/usr/bin/env zsh
# Can run on bash as well

# This script expects to be executed from the root of the project
DIR_FREGE="SimulationFrege/src/com/aexiz/daviz/frege"

DIR_OUTPUT="SimulationFrege/src/"

JAR_FREGE="lib/frege3.25.84.jar"

# Clean previous build
find $DIR_FREGE -name '*.java' -type f -delete

# Specify an compilation order based on dependencies
FREGE_CORE_SOURCES_TO_COMPILE=(
  "${DIR_FREGE}/simulation/Set.fr"
  "${DIR_FREGE}/simulation/Graph.fr"
  "${DIR_FREGE}/simulation/Process.fr"
  "${DIR_FREGE}/simulation/Event.fr"
)

# Compile the files in the specified order
for FILE_NAME in "${FREGE_CORE_SOURCES_TO_COMPILE[@]}"
do
	java -Xss1m -jar "$JAR_FREGE" -d "$DIR_OUTPUT" "$FILE_NAME"
done

# TODO Verify how to simply ignore all matches that are in $FREGE_CORE_SOURCES_TO_COMPILE array
# Compile all other files
find $DIR_FREGE -name '*.fr' \
                ! -name 'Set.fr' \
                ! -name 'Graph.fr' \
                ! -name 'Process.fr' \
                ! -name 'Event.fr' \
                -exec java -Xss1m -jar "$JAR_FREGE" -d "$DIR_OUTPUT" {} ';'

# Clean up generated class files
find $DIR_FREGE -name '*.class' -type f -delete
