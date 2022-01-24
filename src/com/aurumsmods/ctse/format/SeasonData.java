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
public final class SeasonData {
    static final int SIZE = 60;
    static final int PADDING_SIZE = 36;
    
    // -------------------------------------------------------------------------------------------------------------------------
    
    public final KinopioSaveData saveData;
    public final int seasonId;
    public int bonusCourseCounter, bonusCoursePageId, bonusCourseTypeCounter, lastPlayCourseId, dlcBonusCourseTypeCounter;
    public boolean openingEnded;
    
    SeasonData(KinopioSaveData savedata, int idx) {
        saveData = savedata;
        seasonId = idx;
        init();
    }
    
    public void init() {
        bonusCourseCounter = seasonId == 5 ? 4 : 6;
        bonusCoursePageId = 0;
        bonusCourseTypeCounter = 0;
        lastPlayCourseId = 0;
        openingEnded = true;
        dlcBonusCourseTypeCounter = 0;
    }
    
    public void read(ByteBuffer buf) {
        // First comes total block size
        int nextPosition = buf.getInt() + buf.position();
        
        // Then comes the actual data block
        bonusCourseCounter = buf.getInt();
        bonusCoursePageId = buf.getInt();
        bonusCourseTypeCounter = buf.getInt();
        lastPlayCourseId = buf.getInt();
        openingEnded = buf.get() != 0;
        buf.position(buf.position() + 3);
        dlcBonusCourseTypeCounter = buf.getInt();
        
        // Most of the bytes is... padding data, so these can be skipped
        buf.position(nextPosition);
    }
    
    public void write(ByteBuffer buf) {
        // First comes total block size
        buf.putInt(SIZE);
        
        // Then comes the actual data block
        buf.putInt(bonusCourseCounter);
        buf.putInt(bonusCoursePageId);
        buf.putInt(bonusCourseTypeCounter);
        buf.putInt(lastPlayCourseId);
        buf.put((byte)(openingEnded ? 1 : 0));
        buf.put((byte)0);
        buf.put((byte)0);
        buf.put((byte)0);
        buf.putInt(dlcBonusCourseTypeCounter);
        
        // Remaining bytes is padding
        CTSe.fillPadding(buf, PADDING_SIZE);
    }
}
