// Copyright © 2021 Aurum
//
// This file is part of "CTSe"
//
// "CTSe" is free software: you can redistribute it and/or modify it under
// the terms of the GNU General Public License as published by the Free
// Software Foundation, either version 3 of the License, or (at your option)
// any later version.
//
// "CTSe" is distributed in the hope that it will be useful, but WITHOUT ANY 
// WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS 
// FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
//
// You should have received a copy of the GNU General Public License along 
// with "CTSe". If not, see http://www.gnu.org/licenses/.

package com.aurum.ctse;

public final class BitUtil {
    /**
     * Private constructor to prevent instantiation.
     */
    private BitUtil() {}
    
    /**
     * Tests whether the specified bit in val is set or not. Returns {@code true} if set, {@code false} if not.
     * @param val the value to check the bit in
     * @param bit the bit to be checked
     * @return true if specified bit is set, false if not
     */
    public static boolean test(int val, int bit) {
        return ((val >>> bit) & 1) == 1;
    }
    
    /**
     * Returns val with the specified bit set.
     * @param val the value to set the bit in
     * @param bit the bit to be set
     * @return val after bit is set
     */
    public static int set(int val, int bit) {
        return val | (1 << bit);
    }
    
    /**
     * Returns val with the specified bit cleared.
     * @param val the value to clear the bit in
     * @param bit the bit to be cleared
     * @return val after bit is cleared
     */
    public static int clear(int val, int bit) {
        return val & ~(1 << bit);
    }
    
    /**
     * Returns val with the specified bit toggled.
     * @param val the value to toggle the bit in
     * @param bit the bit to be toggled
     * @return val after bit is toggled
     */
    public static int toggle(int val, int bit) {
        return val ^ (1 << bit);
    }
    
    /**
     * Sets the specified bit in val if state is {@code true}. The bit is cleared otherwise. The updated val will be returned.
     * @param val the value to update the bit in
     * @param bit the bit to be updated
     * @param state declares whether to set or clear the bit
     * @return val after bit is updated
     */
    public static int update(int val, int bit, boolean state) {
        return state ? set(val, bit) : clear(val, bit);
    }
}
