/*
 * Copyright (C) 2022 Aurum
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package com.aurumsmods.ctse.format;

import com.aurumsmods.ctse.CTSe;
import java.nio.ByteBuffer;

/**
 * @author Aurum
 */
public final class CourseInfo {
    static final int SIZE = 40;
    static final int PADDING_SIZE = 12;
    
    public static final int FLAG_OPEN                = 0b00000000000000001;
    public static final int FLAG_CLEAR               = 0b00000000000000010;
    public static final int FLAG_LOCK                = 0b00000000000000100;
    public static final int FLAG_NEW                 = 0b00000000000001000;
    public static final int FLAG_ASSIST_CLEAR        = 0b00000000000010000;
    public static final int FLAG_BADGE_CONDITION_0   = 0b00000000000100000;
    public static final int FLAG_BADGE_CONDITION_1   = 0b00000000001000000;
    public static final int FLAG_BADGE_CONDITION_2   = 0b00000000010000000;
    public static final int FLAG_CLEAR_HIDE_AND_SEEK = 0b00000000100000000;
    public static final int FLAG_ACQUIRE_COMPLETE    = 0b10000000000000000;
    
    // -------------------------------------------------------------------------------------------------------------------------
    
    public final KinopioSaveData saveData;
    public final int courseId;
    public int flags, bestCoin, bestTime, missCount, collectItemFlags;
    public long lastPlayTime;
    
    CourseInfo(KinopioSaveData savedata, int idx) {
        saveData = savedata;
        courseId = idx;
        init();
    }
    
    public void init() {
        flags = 0;
        bestCoin = 0;
        bestTime = -1;
        missCount = 0;
        lastPlayTime = 0;
        collectItemFlags = 0;
    }
    
    public void read(ByteBuffer buf) {
        // Get next position after reading
        int nextPosition = buf.position() + SIZE;
        
        // First comes the actual data block
        flags = buf.getInt();
        bestCoin = buf.getInt();
        bestTime = buf.getInt();
        missCount = buf.getInt();
        lastPlayTime = buf.getLong();
        collectItemFlags = buf.getInt();
        
        // Most of the bytes is... padding data, so these can be skipped
        buf.position(nextPosition);
    }
    
    public void write(ByteBuffer buf) {
        // First comes the actual data block
        buf.putInt(flags);
        buf.putInt(bestCoin);
        buf.putInt(bestTime);
        buf.putInt(missCount);
        buf.putLong(lastPlayTime);
        buf.putInt(collectItemFlags);
        
        // Remaining bytes is padding
        CTSe.fillPadding(buf, PADDING_SIZE);
    }
}
