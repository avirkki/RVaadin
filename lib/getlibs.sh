#!/bin/bash

echo
echo "The bundled Eclipse .classpath points to this directory to find"
echo "the required Rserve jar packages REngine.jar and RserveEngine.jar"
echo
echo "Rserve is needed for the communication between Java and R. The library is"
echo "developed by Simon Urbanek and the sources are licensed under GPL."
echo
echo "Downloading the latest libraries from http://www.rforge.net/Rserve/files/"
echo "Press Ctrl+C to cancel, or any other key to proceed"
read

wget -N http://www.rforge.net/Rserve/files/RserveEngine.jar
wget -N http://www.rforge.net/Rserve/files/REngine.jar



