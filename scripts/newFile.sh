#!/bin/bash

CP="$(which cp)"

tplDir="templates"
classTemplate="$tplDir/Class.java"
interfaceTemplate="$tplDir/Interface.java"
exceptionTemplate="$tplDir/Exception.java"
testTemplate="$tplDir/Test.java"
parameterizedTemplate="$tplDir/Parameterized.java"

src_path="src/de/htwsaar/chessbot"
test_path="test/de/htwsaar/chessbot"

printUsage() {
    echo "Aufruf: $0 <Typ> <Paketname> <Dateiname>"
    echo "Verfügbare Typen:"
    echo "    class       -- Erstellt eine neue Klasse"
    echo "    exception   -- Erstellt eine neue Ausnahmeklasse"
    echo "    interface   -- Erstellt eine neue Schnittstelle"
    echo "    package     -- Erstellt ein neues Paket"
    echo "    test        -- Erstellt einen neuen Test"
    echo "    testp       -- Erstellt einen neuen parametrierten Test"
}

operation="$1"
pkgName="$2"
className="$3"

makePkgPath() {
    local pkgName="$1"
    echo $pkgName | sed -e "s/\./\//g"
}

#tplFile=""
#targetFile=""
#targetPath=""

#
# $1 => package root
# $2 => package name
#
makePkgDir() {
    local pkgRoot="$1"
    local pkgName="$2"
    local pkgPath="$pkgRoot/`makePkgPath $pkgName`"
    if [ ! -d "$pkgPath" ]
    then
        echo "Erzeuge Paketverzeichnis $pkgPath"
        test -d "$pkgPath" || mkdir -p "$pkgPath"
    fi
}

#
#
# $1 => package name
#
makePkgInfo() { 
    local pkgPath=`makePkgPath "$1"`
    PI_TEMPLATE="$tplDir/package-info.java"
    PI_TARGET="$src_path/$pkgPath/package-info.java"
    
    if [ ! -f "$PI_TARGET" ]
    then
        echo "Erzeuge package-info.java"
        test -f "$PI_TARGET" || "$CP" "$PI_TEMPLATE" "$PI_TARGET"
        
        echo "Schreibe Paketnamen $pkgName"
        sed -i "s/%PKGNAME/$pkgName/g" "$PI_TARGET"
    fi
    
}

#
#
# $1 => package root
# $2 => package name
# $3 => template file
# $4 => class name
#
makeFile() {
    local src_root="$1"
    local pkgName="$2"
    local tplFile="$3"
    local className="$4"
    local pkgDir=`makePkgPath "$pkgName"`
    local targetFile="$src_root/$pkgDir/${className}.java"

    if [ -f "$targetFile" ]
    then
        read -n1 -p"Datei $targetFile existiert bereits. Überschreiben? (j/N) " answer
        echo
        case "$answer" in
            [jJyY])
            ;;
            *)
                echo "Abbruch."
                exit 1
            ;;  
        esac
    fi
    if [ ! -f "$targetFile" ]
    then
        echo "Kopiere Vorlage $tplFile nach $targetFile"
        "$CP" "$tplFile" "$targetFile"

        echo "Ersetze Paketnamen"
        sed -i "s/%PKGNAME/$pkgName/g" "$targetFile"

        echo "Ersetze Klassennamen"
        sed -i "s/%CLASSNAME/$className/g" "$targetFile"
    fi
}

#
#
# $1 => package name
# $2 => template file
# $3 => class name
#
makeSourceFile() {
    local pkgName="$1"
    local tplFile="$2"
    local className="$3"

    makePkgDir "$src_path" "$pkgName"
    makePkgInfo "$pkgName"
    makeFile "$src_path" "$pkgName" "$tplFile" "$className"
}

#
#
# $1 => package name
# $2 => template file
# $3 => class name
#
makeTestFile() {
    local pkgName="$1"
    local tplFile="$2"
    local className="$3"
    
    makePkgDir "$test_path" "$pkgName"
    makeFile "$test_path" "$pkgName" "$tplFile" "$className"   
}

case "$operation" in
    "class")
        tplFile="$classTemplate"
        makeSourceFile "$pkgName" "$tplFile" "$className"
    ;;
    "exception")
        tplFile="$exceptionTemplate"
        makeSourceFile "$pkgName" "$tplFile" "$className"
    ;;
    "interface")
        tplFile="$interfaceTemplate"
        makeSourceFile "$pkgName" "$tplFile" "$className"
    ;;
    "package")
        makePkgDir "$pkgName"
        makePkgInfo "$pkgName"
        exit 0
    ;;
    "test")
        tplFile="$testTemplate"
        makeTestFile "$pkgName" "$tplFile" "${className}Test"
    ;;
    "testp")
        tplFile="$parameterizedTemplate"
        makeTestFile "$pkgName" "$tplFile" "${className}Test"
    ;;
    *)
        printUsage
        exit 1
    ;;
esac

if [ -f "$targetFile" ]
then
    read -n1 -p"Datei $targetFile existiert bereits. Überschreiben? (j/N) " answer
    echo
    case "$answer" in
        [jJyY])
        ;;
        *)
            echo "Abbruch."
            exit 1
        ;;
    esac
fi

exit 0

