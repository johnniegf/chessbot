#!/bin/bash

suiteFile="$@"
if [ ! -f "$suiteFile" ]; then
    echo "No such file: $suiteFile"
    exit 1
fi

while read line
do
    fen=$(echo $line | cut -d";" -f1 )
    for i in `seq 2 7`
    do
        depth=$(( $i - 1 ))
        expected=$(echo $line | cut -d";" -f$i | cut -d" " -f2)
        if [ "$expected" = "unknown" ]; then
            continue
        fi
        actual=$(java -cp build de.htwsaar.chessbot.engine.util.Perft "$fen" $depth)
        pass="Y"
        if [ "$expected" != "$actual" ]; then
            pass="N"
        fi
        echo "$pass;$fen;$depth;$expected;$actual"
    done
done < "$suiteFile"
