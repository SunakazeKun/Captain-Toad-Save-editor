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

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.time.Instant;
import java.util.zip.CRC32;

public class SaveData {
    public static final int LATEST_VERSION = 2;
    public static final int SAVE_FILE_SIZE = 0x800C;
    public static final int SEASON_ENTRY_SIZE = 0x40;
    public static final int STAGE_ENTRY_SIZE = 0x28;
    
    private static final int[] BOOKS_PER_VERSION = { 4, 5, 6 };
    private static final int[] LEVELS_PER_VERSION = { 0xB7, 0xD1, 0xD5 };
    
    private static final int STAGE_FLAG_IS_OPEN = 0;
    private static final int STAGE_FLAG_IS_BEAT = 1;
    private static final int STAGE_FLAG_2 = 2;
    private static final int STAGE_FLAG_IS_NEW = 3;
    private static final int STAGE_FLAG_4 = 4;
    private static final int STAGE_FLAG_HAS_BADGE_CONDITION = 5;
    private static final int STAGE_FLAG_HAS_DOT_KINOPIO = 8;
    private static final int STAGE_FLAG_IS_COMPLETE = 16;
    
    public class SeasonInfo {
        public int unk0, unk4, unk8, unkC, lastSelectedCourse, unk14, unk18;
        public byte[] unk1C;
        
        public SeasonInfo() {
            unk0 = 0x3C;
            unk4 = 0;
            unk8 = 0;
            unkC = 0;
            lastSelectedCourse = 0;
            unk14 = 0;
            unk18 = 0;
            unk1C = new byte[0x24];
        }
    }
    
    public class StageInfo {
        private int flags, coinNum, clearTime, unkC;
        private long lastSavedTime;
        private int collectItemFlags, unk1C;

        public StageInfo() {
            flags = 0;
            coinNum = 0;
            clearTime = -1;
            unkC = 0;
            lastSavedTime = 0L;
            collectItemFlags = 0;
            unk1C = 0;
        }

        public int getFlags() {
            return flags;
        }
        
        public boolean getIsOpen() {
            return BitUtil.test(flags, STAGE_FLAG_IS_OPEN);
        }
        
        public void setIsOpen(boolean state) {
            flags = BitUtil.update(flags, STAGE_FLAG_IS_OPEN, state);
        }
        
        public boolean getIsBeaten() {
            return BitUtil.test(flags, STAGE_FLAG_IS_BEAT);
        }
        
        public void setIsBeaten(boolean state) {
            flags = BitUtil.update(flags, STAGE_FLAG_IS_BEAT, state);
        }
        
        public boolean getFlag2() {
            return BitUtil.test(flags, STAGE_FLAG_2);
        }
        
        public void setFlag2(boolean state) {
            flags = BitUtil.update(flags, STAGE_FLAG_2, state);
        }
        
        public boolean getIsNew() {
            return BitUtil.test(flags, STAGE_FLAG_IS_NEW);
        }
        
        public void setIsNew(boolean state) {
            flags = BitUtil.update(flags, STAGE_FLAG_IS_NEW, state);
        }
        
        public boolean getFlag4() {
            return BitUtil.test(flags, STAGE_FLAG_4);
        }
        
        public void setFlag4(boolean state) {
            flags = BitUtil.update(flags, STAGE_FLAG_4, state);
        }
        
        public boolean getHasBadgeCondition() {
            return BitUtil.test(flags, STAGE_FLAG_HAS_BADGE_CONDITION);
        }
        
        public void setHasBadgeCondition(boolean state) {
            flags = BitUtil.update(flags, STAGE_FLAG_HAS_BADGE_CONDITION, state);
        }
        
        public boolean getHasDotKinopio() {
            return BitUtil.test(flags, STAGE_FLAG_HAS_DOT_KINOPIO);
        }
        
        public void setHasDotKinopio(boolean state) {
            flags = BitUtil.update(flags, STAGE_FLAG_HAS_DOT_KINOPIO, state);
        }
        
        public boolean getIsCompleted() {
            return BitUtil.test(flags, STAGE_FLAG_IS_COMPLETE);
        }
        
        public void setIsCompleted(boolean state) {
            flags = BitUtil.update(flags, STAGE_FLAG_IS_COMPLETE, state);
        }
        
        public int getCoinNum() {
            return coinNum;
        }
        
        public void setCoinNum(int val) {
            coinNum = val;
        }
        
        public int getClearTime() {
            return clearTime;
        }
        
        public void setClearTime(int val) {
            clearTime = val;
        }
        
        public int getUnkC() {
            return unkC;
        }
        
        public void setUnkC(int val) {
            unkC = val;
        }
        
        public long getLastSavedTime() {
            return lastSavedTime;
        }
        
        public void setLastSavedTime(long val) {
            lastSavedTime = val;
        }
        
        public int getCollectItemFlags() {
            return collectItemFlags;
        }
        
        public void setCollectItemFlags(int val) {
            collectItemFlags = val;
        }
    }
    
    private ByteBuffer buffer;
    private final CRC32 crc32;
    private boolean isValid;
    private int offSaveBlockInfo, offSeasons, offStages;
    
    public int version, unk0, lenSaveBlock, unk8, unkC, lenSaveBlockInfo, gameFlags, unk18;
    public long lastGameSaveTime;
    public int numOneUps, lastSeason, unk2C, unk30, unk34, unk38, unk3C;
    public SeasonInfo[] seasonInfos;
    public StageInfo[] stageInfos;
    
    public SaveData() {
        buffer = ByteBuffer.allocate(SAVE_FILE_SIZE);
        buffer.order(ByteOrder.LITTLE_ENDIAN);
        crc32 = new CRC32();
        isValid = true;
        
        offSaveBlockInfo = 0x20;
        offSeasons = 0x228;
        offStages = 0x3A8;
        
        version = LATEST_VERSION;
        unk0 = 7;
        lenSaveBlock = 0x24E8;
        unk8 = 0;
        unkC = 0;
        lenSaveBlockInfo = 0x208;
        gameFlags = 0;
        unk18 = 0;
        lastGameSaveTime = Instant.now().getEpochSecond();
        numOneUps = 2;
        lastSeason = 1;
        unk2C = 0;
        unk30 = 0x32;
        unk34 = 0;
        unk38 = 0;
        unk3C = 0;
        seasonInfos = new SeasonInfo[BOOKS_PER_VERSION[version]];
        stageInfos = new StageInfo[LEVELS_PER_VERSION[version]];
        
        for (int i = 0 ; i < seasonInfos.length ; i++)
            seasonInfos[i] = new SeasonInfo();
        
        for (int i = 0 ; i < stageInfos.length ; i++)
            stageInfos[i] = new StageInfo();
    }
    
    //--------------------------------------------------------------------------------------------------------------------------
    
    public SaveData(byte[] raw) {
        crc32 = new CRC32();
        load(raw);
    }
    
    public final void load(byte[] raw) {
        if (raw.length != SAVE_FILE_SIZE)
            throw new IllegalArgumentException("Input size is not 32780!");
        buffer = ByteBuffer.wrap(raw);
        buffer.order(ByteOrder.LITTLE_ENDIAN);
        
        // save header
        buffer.position(0x0004);
        version = buffer.getInt();
        int fileSize = buffer.getInt();
        
        isValid = 0 <= version && version <= LATEST_VERSION && fileSize == SAVE_FILE_SIZE;
        if (!isValid)
            return;
        
        // save block
        unk0 = buffer.getInt();
        lenSaveBlock = buffer.getInt();
        unk8 = buffer.getInt();
        unkC = buffer.getInt();
        lenSaveBlockInfo = buffer.getInt();
        offSaveBlockInfo = buffer.position();
        gameFlags = buffer.getInt();
        unk18 = buffer.getInt();
        lastGameSaveTime = buffer.getLong();
        numOneUps = buffer.getInt();
        lastSeason = buffer.getInt();
        unk2C = buffer.getInt();
        unk30 = buffer.getInt();
        unk34 = buffer.getInt();
        unk38 = buffer.getInt();
        unk3C = buffer.getInt();
        
        offSeasons = offSaveBlockInfo + lenSaveBlockInfo;
        int numSeasonsMax = BOOKS_PER_VERSION[version];
        offStages = offSeasons + numSeasonsMax * SEASON_ENTRY_SIZE;
        int numLevels = buffer.getInt(offStages);
        
        isValid &= numLevels == LEVELS_PER_VERSION[version];
        if (!isValid)
            return;
        
        seasonInfos = new SeasonInfo[numSeasonsMax];
        stageInfos = new StageInfo[numLevels];
        
        // seasons
        buffer.position(offSeasons);
        for (int i = 0 ; i < numSeasonsMax ; i++) {
            SeasonInfo si = new SeasonInfo();
            si.unk0 = buffer.getInt();
            si.unk4 = buffer.getInt();
            si.unk8 = buffer.getInt();
            si.unkC = buffer.getInt();
            si.lastSelectedCourse = buffer.getInt();
            si.unk14 = buffer.getInt();
            si.unk18 = buffer.getInt();
            buffer.get(si.unk1C);
            seasonInfos[i] = si;
        }
        
        // stages
        buffer.position(offStages + 4);
        for (int i = 0 ; i < numLevels ; i++) {
            StageInfo si = new StageInfo();
            si.flags = buffer.getInt();
            si.coinNum = buffer.getInt();
            si.clearTime = buffer.getInt();
            si.unkC = buffer.getInt();
            si.lastSavedTime = buffer.getLong();
            si.collectItemFlags = buffer.getInt();
            si.unk1C = buffer.getInt();
            buffer.getLong(); // unknown, always 0
            stageInfos[i] = si;
        }
    }
    
    public final byte[] save() {
        buffer.position(0x0004);
        buffer.putInt(version);
        buffer.putInt(SAVE_FILE_SIZE);
        buffer.putInt(unk0);
        buffer.putInt(lenSaveBlock);
        buffer.putInt(unk8);
        buffer.putInt(unkC);
        buffer.putInt(lenSaveBlockInfo);
        buffer.putInt(gameFlags);
        buffer.putInt(unk18);
        buffer.putLong(lastGameSaveTime);
        buffer.putInt(numOneUps);
        buffer.putInt(lastSeason);
        buffer.putInt(unk2C);
        buffer.putInt(unk30);
        buffer.putInt(unk34);
        buffer.putInt(unk38);
        buffer.putInt(unk3C);
        
        buffer.position(offSeasons);
        for (SeasonInfo si : seasonInfos) {
            buffer.putInt(si.unk0);
            buffer.putInt(si.unk4);
            buffer.putInt(si.unk8);
            buffer.putInt(si.unkC);
            buffer.putInt(si.lastSelectedCourse);
            buffer.putInt(si.unk14);
            buffer.putInt(si.unk18);
            buffer.put(si.unk1C);
        }
        
        buffer.position(offStages);
        buffer.putInt(stageInfos.length);
        for (StageInfo si : stageInfos) {
            buffer.putInt(si.flags);
            buffer.putInt(si.coinNum);
            buffer.putInt(si.clearTime);
            buffer.putInt(si.unkC);
            buffer.putLong(si.lastSavedTime);
            buffer.putInt(si.collectItemFlags);
            buffer.putInt(si.unk1C);
            buffer.putLong(0L); // unknown, always 0
        }
        
        crc32.update(buffer.array(), 4, SAVE_FILE_SIZE - 4);
        buffer.putInt(0x0000, (int)crc32.getValue());
        return buffer.array();
    }
    
    public boolean isValid() {
        return isValid;
    }
    
    public boolean isOutdated() {
        return version != LATEST_VERSION;
    }
    
    public boolean isSwitchVersion() {
        return true;
    }
    
    public void update() {
        if (!isOutdated())
            return;
        
        // todo
    }
}
