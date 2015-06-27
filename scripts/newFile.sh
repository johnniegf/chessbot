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
author_name="$(git config user.name)"

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

# Erzeuge ein Paketverzeichnis
#
# $1 => Wurzelverzeichnis der Pakethierarchie
# $2 => Paketname
#
makePkgDir() {
    local pkgRoot="$1"
    local pkgName="$2"
    local pkgPath="$pkgRoot/`makePkgPath $pkgName`"
    if [ ! -d "$pkgPath" ]
    then
        read -n1 -p"Neues Paketverzeichnis $pkgPath erstellen? (j/N) " answer
        echo
        case "$answer" in
            [jJyY])
            ;;
            *)
                echo "Abbruch."
                exit 1
            ;;  
        esac
        
        echo "Erzeuge Paketverzeichnis $pkgPath"
        test -d "$pkgPath" || mkdir -p "$pkgPath"
    fi
}

# Erzeuge die package-info.java eines Pakets
#
# $1 => Paketname
#
makePkgInfo() { 
    local pkgPath=`makePkgPath "$1"`
    PI_TEMPLATE="$tplDir/package-info.java"
    PI_TARGET="$src_path/$pkgPath/package-info.java"
    
    if [ ! -f "$PI_TARGET" ]
    then
        echo "Erzeuge package-info.java"
        test -f "$PI_TEMPLATE" || echo "Warnung: Vorlage package-info.java nicht gefunden!"
        "$CP" "$PI_TEMPLATE" "$PI_TARGET"
        
        echo "Schreibe Paketnamen $pkgName"
        sed -i "s/%PKGNAME/$pkgName/g" "$PI_TARGET"
    fi
    
}

# Erzeuge eine Quelldatei aus einer Vorlage
#
# $1 => Wurzelverzeichnis der Pakethierarchie
# $2 => Paketname
# $3 => Vorlage
# $4 => Klassenname
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

        echo "Ersetze Autor"
        sed -i "s/%AUTHOR/$author_name/" "$targetFile"
    fi
}

# Erzeuge eine Quelldatei des Produktionscodes
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

# Erzeuge eine Quelldatei für JUnit-Tests
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
    "exception")
        tplFile="$exceptionTemplate"
        targetPath="$src_path/$pkgPath"
        targetFile="$targetPath/${className}.java"
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
exit 0

