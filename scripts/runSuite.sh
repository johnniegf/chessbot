#!/bin/bash

suiteFile="$1"
if [ ! -f "$suiteFile" ]; then
    echo "No such file: $suiteFile"
    exit 1
fi

maxDepth=$2
if [ -z "$maxDepth" ]; then
    maxDepth=99
fi

echo 'Passed;FEN-String;Depth;Expected;Actual;"Time (ms)"'
while read line
do
    fen=$(echo $line | cut -d";" -f1 )
    for i in `seq 1 $maxDepth`
    do
        depth=$i
        field=$(( $i + 1 ))
        expected=$(echo $line | cut -d";" -f$field | cut -d" " -f2)
        if [ -z "$expected" ]; then
            break
        fi
        result=$(java -cp build de.htwsaar.chessbot.engine.util.Perft "$fen" $depth)
        actual=$(echo $result | cut -d";" -f1)
        seconds=$(echo $result | cut -d";" -f2)
        pass="Y"
        if [ "$expected" = "unknown" ]; then
            pass="U"
        fi
        if [ "$expected" != "$actual" ]; then
            pass="N"
        fi
        echo "$pass;$fen;$depth;$expected;$actual;$seconds"
    done
done < "$suiteFile"
