package de.htwsaar.chessbot.util;

import static de.htwsaar.chessbot.util.Exceptions.*;

import java.util.*;
/**
* Beschreibung.
*
* @author Johannes Haupt
*/
public class Table<KEY1, KEY2, VAL> {
    
    private Map<KEY1, Map<KEY2, VAL> > mTable;
    private Collection<KEY1> mRows;
    private Collection<KEY2> mCols;
    private final VAL mInitialValue;

    public Table() {
        this(null); 
    }

    public Table(final VAL initialValue) {
        this(initialValue, (Collection<KEY1>) new ArrayList<Object>());
    }

    public Table(final VAL initialValue,
                  final Collection<KEY1> rows)
    {
        this(
            initialValue,
            rows,
            (Collection<KEY2>) new ArrayList<Object>()
        );
    }

    public Table(final VAL initialValue,
                  final Collection<KEY1> rows,
                  final Collection<KEY2> cols) 
    {
        checkNull(rows, "rows");
        checkNull(cols, "cols");

        mInitialValue = initialValue;
        mRows = (Collection<KEY1>) new ArrayList<Object>(); 
        mCols = (Collection<KEY2>) new ArrayList<Object>(); 
        mTable = (Map<KEY1, Map<KEY2, VAL> >) new HashMap();
        for (KEY2 colIndex : cols) 
            addColumn(colIndex);
        for (KEY1 rowIndex : rows) 
            addRow(rowIndex);
        /*for (KEY1 rowIndex : rows) {
            checkNull(rowIndex, "rowIndex");
            Map<KEY2, VAL> current = 
                (Map<KEY2, VAL>) new HashMap<Object,Object>();
            for (KEY2 colIndex : cols) {
                current.put(colIndex, initialValue);
            }
            mTable.put(rowIndex, current);
        }*/
    }

    /**
    * Gib den Hashwert dieses Objekts aus.
    *
    * @return Hashwert dieses Objekts.
    */
    public int hashCode() {
        int hash = 0;
        hash += mTable.hashCode();
        return hash;
    }

    public boolean put(final KEY1 rowIndex, 
                       final KEY2 colIndex, 
                       final VAL newValue) {
        checkNull(rowIndex, "rowIndex");
        checkNull(colIndex, "colIndex");
        Map<KEY2, VAL> row = mTable.get(rowIndex);
        if (row == null)
            return false;
        
        if (!row.containsKey(colIndex))
            return false;

        row.put(colIndex, newValue);
        return true;
    }

    public boolean hasCell(final KEY1 rowIndex, final KEY2 colIndex) {
        if (!mTable.containsKey(rowIndex))
            return false;

        return mTable.get(rowIndex).containsKey(colIndex);
    }

    public VAL get(final KEY1 rowIndex, final KEY2 colIndex) {
        if (!hasCell(rowIndex,colIndex))
            throw new IllegalArgumentException("No such table cell [" + rowIndex + "|" + colIndex + "]");
        return mTable.get(rowIndex).get(colIndex);
    }

    public boolean putAll(final Table<KEY1,KEY2,VAL> otherTable) {
        checkNull(otherTable,"otherTable");

        for (KEY1 rowIndex : otherTable.getRows()) {
            addRow(rowIndex);
            for (KEY2 colIndex : otherTable.getColumns()) {
                addColumn(colIndex);
                put(rowIndex, colIndex, otherTable.get(rowIndex,colIndex));
            }
        }
        return true;
    }

    public Collection<KEY1> getRows() {
        return mRows;
    }

    public Collection<KEY2> getColumns() {
        return mCols;
    }

    public boolean addRow(KEY1 rowIndex) {
        checkNull(rowIndex, "rowIndex");
        if (getRows().contains(rowIndex))
            return false;

        Map<KEY2,VAL> row = (Map<KEY2,VAL>) new HashMap<Object,Object>();
        for (KEY2 colIndex : getColumns()) {
            row.put(colIndex, mInitialValue);
        }
        mTable.put(rowIndex, row);
        mRows.add(rowIndex);
        return true;
    }

    public boolean addColumn(final KEY2 colIndex) {
        checkNull(colIndex, "colIndex");
        if (getColumns().contains(colIndex))
            return false;
        
        for (KEY1 rowIndex : getRows()) {
            mTable.get(rowIndex).put(colIndex, mInitialValue);
        }
        mCols.add(colIndex);
        return true;
    }

    public int colCount() {
        return mCols.size();
    }

    public int rowCount() {
        return mRows.size();
    }

    /**
    * Prüfe das Objekt auf Gleichheit mit einem anderen Objekt.
    *
    * @param other das zu prüfende Objekt.
    * @return <code>true</code> genau dann, wenn die Objekte gleich sind,
    *         sonst <code>false</code>
    */
    public boolean equals(final Object other) {
        // Triviale Fälle
        if (other == null) return false;
        if (other == this) return true;

        if (other instanceof Table) {
            final Table<KEY1,KEY2,VAL> ot = 
                (Table<KEY1,KEY2,VAL>) other;
            // Prüfe die Attribute auf Gleichheit
            
            return true;
        } else {
            return false;
        }
    }

    /**
    * Stringkonversion.
    *
    * @return Stringdarstellung dieses Objekts.
    */
    public String toString() {
        if (mRows.isEmpty() && mCols.isEmpty())
            return "empty";
        final String delim = "|";
        final String newline = System.getProperty("line.separator");
        StringBuilder sb = new StringBuilder();
        sb.append("***").append(delim);
        for (KEY2 col : mCols) {
            sb.append(col).append(delim);
        }
        sb.append(newline);
        for (KEY1 row : mRows) {
            sb.append(row).append(delim);
            for(KEY2 col : mCols) {
                sb.append(get(row,col)).append(delim);
            }
            sb.append(newline);
        }
        return sb.toString();
    }
}
