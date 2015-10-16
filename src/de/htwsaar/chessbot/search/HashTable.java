/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.htwsaar.chessbot.search;

/**
 *
 * @author Johannes Haupt
 */
public interface HashTable {
    
    void setCapacity(final int capacity);
    
    int capacity();
    
    void clear();
    
    int size();
}
