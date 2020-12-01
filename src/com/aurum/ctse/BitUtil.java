// Copyright © 2020 Aurum
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
    private BitUtil() {}
    
    public static boolean test(int val, int bit) {
        return ((val >> bit) & 1) == 1;
    }
    
    public static int toggle(int val, int bit, boolean state) {
        return state ? set(val, bit) : clear(val, bit);
    }
    
    public static int set(int val, int bit) {
        return val | (1 << bit);
    }
    
    public static int clear(int val, int bit) {
        return val & ~(1 << bit);
    }
}
