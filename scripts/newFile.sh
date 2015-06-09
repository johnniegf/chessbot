#!/bin/bash

CP="$(which cp)"

tplDir="templates"
classTemplate="$tplDir/Class.java"
interfaceTemplate="$tplDir/Interface.java"
testTemplate="$tplDir/Test.java"

src_path="src/de/htwsaar/chessbot"
test_path="test/de/htwsaar/chessbot"

printUsage() {
    echo "Aufruf: $0 <class|test|interface> <Paketname> <Dateiname>"
}

operation="$1"
pkgName="$2"
className="$3"

pkgPath=$(echo $pkgName | sed -e "s/\./\//g")
tplFile=""
targetFile=""
targetPath=""

case "$operation" in
    "class")
        tplFile="$classTemplate"
        targetPath="$src_path/$pkgPath"
        targetFile="$targetPath/${className}.java"
    ;;
    "interface")
        tplFile="$interfaceTemplate"
        targetFile="$src_path/$pkgPath/${className}.java"
    ;;
    "test")
        tplFile="$testTemplate"
        targetPath="$test_path/$pkgPath"
        targetFile="$targetPath/${className}Test.java"
    ;;
    *)
        printUsage
        exit 1
    ;;
esac

if [ ! -d "$targetPath" ]
then
    echo "Erstelle Verzeichnis $targetPath"
    mkdir -p "$targetPath"
fi

echo "Kopiere Vorlage \'$tplFile\' nach \'$targetFile\'"
"$CP" "$tplFile" "$targetFile"

echo "Ersetze Paketnamen"
sed -i "s/%PKGNAME/$pkgName/g" "$targetFile"

echo "Ersetze Klassennamen"
sed -i "s/%CLASSNAME/$className/g" "$targetFile"

exit 0

