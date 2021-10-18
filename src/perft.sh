#!/bin/bash

# Description: This script is used by perftree to call the perft function for debugging purposes

DEPTH=$1
FEN=$2
MOVE=$3

javac Main.java
java Main $DEPTH $FEN $MOVE