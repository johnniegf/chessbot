#!/bin/bash
#
# Erzeugt eine Liste von Testklassen, 
# die vom TestRunner nacheinander ausgeführt werden.
#
# Exitcodes:
#   - 0: OK, kein Fehler
#   - 1: Klassenverzeichnis existiert nicht oder wurde nicht angegeben 

TESTLIST="TestList.txt"

$(which find) . -name '*Test.class' \
    | sed 's/\//./g'     \
    | sed 's/\.\././g'     \
    | sed 's/^\.//'     \
    | sed 's/\.class$//' \
    > "$TESTLIST"

exit 0

