/*
 * Copyright (C) 2022 - 2025 Aurum
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
public final class GameData {
    static final int SIZE = 520;             // Total section size (Switch Version 1.0.0 or newer)
    static final int OLD_SIZE = 544;         // Total section size (Wii U or 3DS Version)
    static final int PADDING_SIZE = 475;     // Padding data size (Switch Version 1.0.0 or newer)
    static final int OLD_PADDING_SIZE = 499; // Padding data size (Wii U or 3DS Version)
    
    public static final int FLAG_OPEN_SEASON_2                     = 0b00000000000000000000000000001;
    public static final int FLAG_OPEN_SEASON_3                     = 0b00000000000000000000000000010;
    public static final int FLAG_OPEN_SEASON_SP                    = 0b00000000000000000000000000100;
    public static final int FLAG_OPEN_SEASON_SP_BONUS              = 0b00000000000000000000000001000;
    public static final int FLAG_SHOW_INFO_MANUAL                  = 0b00000000000000000000000010000;
    public static final int FLAG_SHOW_PLAY_OTHER_COURSE            = 0b00000000000000000000000100000;
    public static final int FLAG_SHOW_TOUCH_COURSE_PAGE            = 0b00000000000000000000001000000;
    public static final int FLAG_SHOW_ENDING                       = 0b00000000000000000000010000000;
    public static final int FLAG_SHOW_TUTORIAL_1                   = 0b00000000000000000000100000000;
    public static final int FLAG_SHOW_TUTORIAL_2                   = 0b00000000000000000001000000000;
    public static final int FLAG_SHOW_LIGHT_ON_OFF                 = 0b00000000000000000010000000000;
    public static final int FLAG_SHOW_ZOOM                         = 0b00000000000000000100000000000;
    public static final int FLAG_SHOW_RETURN_TITLE                 = 0b00000000000000001000000000000;
    public static final int FLAG_OPEN_MANECK_FOREVER               = 0b00000000000000010000000000000;
    public static final int FLAG_SHOW_3D_WORLD_SAVE_DATA           = 0b00000000000000100000000000000;
    public static final int FLAG_SHOW_HIDE_AND_SEEK_TUTORIAL       = 0b00000000000001000000000000000;
    public static final int FLAG_OPEN_HIDE_AND_SEEK                = 0b00000000000010000000000000000;
    public static final int FLAG_OPEN_PIXEL_TOAD                   = 0b00000000000100000000000000000;
    public static final int FLAG_SHOW_TOUCH_CONTROLS               = 0b00000000001000000000000000000;
    public static final int FLAG_SHOW_GYRO_CONTROLS                = 0b00000000010000000000000000000;
    public static final int FLAG_PAGE_TURN_AFTER_SEASON_1_PROLOGUE = 0b00000000100000000000000000000;
    public static final int FLAG_OPEN_SEASON_SP_2                  = 0b00000001000000000000000000000;
    public static final int FLAG_SHOW_2P_WELCOME                   = 0b00000010000000000000000000000;
    public static final int FLAG_SHOW_AOC_WELCOME_1                = 0b00000100000000000000000000000;
    public static final int FLAG_SHOW_AOC_WELCOME_2                = 0b00001000000000000000000000000;
    public static final int FLAG_SHOW_GHOST_TUTORIAL               = 0b00010000000000000000000000000;
    public static final int FLAG_SHOW_TOUCH_REMINDER               = 0b00100000000000000000000000000;
    public static final int FLAG_SHOW_SPECIAL_2_CREDITS            = 0b01000000000000000000000000000;
    public static final int FLAG_SHOW_VR_WELCOME                   = 0b10000000000000000000000000000;
    
    public static final int CONTROL_CAMERA_REVERSE_VERTICAL       = 0b00001;
    public static final int CONTROL_CAMERA_REVERSE_HORIZONTAL     = 0b00010;
    public static final int CONTROL_CAMERA_GYRO                   = 0b00100;
    public static final int CONTROL_TRUCK_CAMERA_GYRO             = 0b01000;
    public static final int CONTROL_TRUCK_CAMERA_REVERSE_VERTICAL = 0b10000;
    
    // -------------------------------------------------------------------------------------------------------------------------
    
    public final KinopioSaveData saveData;
    boolean isUseOldSize;
    
    public int flags, controlSetting, playerLife, currentSeasonId, maneckFinalFloorCount, moleMazeFinalFloorCount;
    public boolean miiverseSetting, exist3DWorldSaveData, unlockOdysseyLevels, needInitAOCLock;
    public short unk6;
    public long lastPlayTime, lastUptime;
    
    GameData(KinopioSaveData savedata) {
        saveData = savedata;
        init();
    }
    
    public void init() {
        isUseOldSize = saveData.getGameVersion() < KinopioSaveData.VERSION_SWITCH;
        
        flags = 0;
        controlSetting = 0;
        miiverseSetting = true;
        unk6 = 0;
        lastPlayTime = 0;
        playerLife = 2;
        currentSeasonId = 1;
        exist3DWorldSaveData = false;
        unlockOdysseyLevels = false;
        maneckFinalFloorCount = 0;
        lastUptime = 0;
        moleMazeFinalFloorCount = 0;
        needInitAOCLock = true;
    }
    
    public void read(ByteBuffer buf) {
        // First comes total block size
        int nextPosition = buf.getInt() + buf.position();
        
        // Then comes the actual data block
        flags = buf.getInt();
        controlSetting = buf.get() & 0xFF;
        miiverseSetting = buf.get() != 0;
        unk6 = buf.getShort();
        lastPlayTime = buf.getLong();
        playerLife = buf.getInt();
        currentSeasonId = buf.getInt();
        exist3DWorldSaveData = buf.get() != 0;
        unlockOdysseyLevels = buf.get() != 0;
        buf.getShort(); // padding
        maneckFinalFloorCount = buf.getInt();
        lastUptime = buf.getLong();
        moleMazeFinalFloorCount = buf.getInt();
        needInitAOCLock = buf.get() != 0;
        
        // Most of the bytes is... padding data, so these can be skipped
        buf.position(nextPosition);
    }
    
    public void write(ByteBuffer buf) {
        // First comes total block size
        buf.putInt(isUseOldSize ? OLD_SIZE : SIZE);
        
        // Then comes the actual data block
        buf.putInt(flags);
        buf.put((byte)controlSetting);
        buf.put((byte)(miiverseSetting ? 1 : 0));
        buf.putShort(unk6);
        buf.putLong(lastPlayTime);
        buf.putInt(playerLife);
        buf.putInt(currentSeasonId);
        buf.put((byte)(exist3DWorldSaveData ? 1 : 0));
        buf.put((byte)(unlockOdysseyLevels ? 1 : 0));
        buf.putShort((short)0); // padding
        buf.putInt(maneckFinalFloorCount);
        buf.putLong(lastUptime);
        buf.putInt(moleMazeFinalFloorCount);
        buf.put((byte)(needInitAOCLock ? 1 : 0));
        
        // Remaining bytes is padding
        CTSe.fillPadding(buf, isUseOldSize ? OLD_PADDING_SIZE : PADDING_SIZE);
    }
}
