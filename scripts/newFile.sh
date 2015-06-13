#!/bin/bash

CP="$(which cp)"

tplDir="templates"
classTemplate="$tplDir/Class.java"
interfaceTemplate="$tplDir/Interface.java"
testTemplate="$tplDir/Test.java"
parameterizedTemplate="$tplDir/Parameterized.java"

src_path="src/de/htwsaar/chessbot"
test_path="test/de/htwsaar/chessbot"

printUsage() {
    echo "Aufruf: $0 <Typ> <Paketname> <Dateiname>"
    echo "Verfügbare Typen:"
    echo "    class       -- Erstellt eine neue Klasse"
    echo "    interface   -- Erstellt eine neue Schnittstelle"
    echo "    test        -- Erstellt einen neuen Test"
    echo "    testp       -- Erstellt einen neuen parametrierten Test"
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
        targetPath="$src_path/$pkgPath"
        targetFile="$targetPath/${className}.java"
    ;;
    "test")
        tplFile="$testTemplate"
        targetPath="$test_path/$pkgPath"
        targetFile="$targetPath/${className}Test.java"
    ;;
    "testp")
        tplFile="$parameterizedTemplate"
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

