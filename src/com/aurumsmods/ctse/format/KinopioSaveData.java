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
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;
import java.util.zip.CRC32;

/**
 * @author Aurum
 */
public final class KinopioSaveData {
    // -------------------------------------------------------------------------------------------------------------------------
    // File format constants
    
    static final int TOTAL_FILE_SIZE = 0x800C;
    static final int GAME_IDENTIFIER = 7;
    
    // Seasond and courses per game version
    static final int[] SEASONS_PER_VERSION = { 5, 5, 5, 5, 6 };
    static final int[] COURSES_PER_VERSION = { 183, 183, 183, 209, 213 };
    
    // Game version identifiers
    public static final int VERSION_WII_U = 0;
    public static final int VERSION_3DS = 1;
    public static final int VERSION_SWITCH = 2;
    public static final int VERSION_SWITCH_AOC = 3;
    public static final int VERSION_SWITCH_VR = 4;
    
    // -------------------------------------------------------------------------------------------------------------------------
    // KinopioSaveData implementation
    
    private final ByteBuffer buffer;
    private final CRC32 crc32;
    private int gameVersion;
    private final GameData gameData;
    private final List<SeasonData> seasonData;
    private final List<CourseInfo> courseInfos;
    
    public KinopioSaveData() {
        buffer = ByteBuffer.wrap(new byte[TOTAL_FILE_SIZE]);
        buffer.order(ByteOrder.LITTLE_ENDIAN);
        crc32 = new CRC32();
        gameVersion = VERSION_SWITCH_VR;
        gameData = new GameData(this);
        seasonData = new ArrayList(6);
        courseInfos = new ArrayList(213);
        
        // Initialize data
        gameData.init();
        
        for (int i = 0 ; i < 6 ; i++) {
            SeasonData season = new SeasonData(this, i + 1);
            season.init();
            seasonData.add(season);
        }
        
        for (int i = 0 ; i < 213 ; i++) {
            CourseInfo course = new CourseInfo(this, i);
            course.init();
            courseInfos.add(course);
        }
    }
    
    public void init() {
        gameData.init();
        
        for (SeasonData season : seasonData)
            season.init();
        
        for (CourseInfo course : courseInfos)
            course.init();
    }
    
    // -------------------------------------------------------------------------------------------------------------------------
    // Save data reading and writing
    
    public void read(File file) throws IOException, KinopioSaveException {
        // Load data into buffer
        try(FileInputStream in = new FileInputStream(file)) {
            if (in.available() != TOTAL_FILE_SIZE)
                throw new KinopioSaveException("Invalid file size. Expected 32780 bytes.");
            in.read(buffer.array());
        }
        
        buffer.order(ByteOrder.LITTLE_ENDIAN);
        buffer.position(0);
        
        // Reinitialize all data
        init();
        
        // Check game identifier to determine endianness and game version
        switch (buffer.getInt(0x0C)) {
            // Wii U version
            case GAME_IDENTIFIER << 24:
                buffer.order(ByteOrder.BIG_ENDIAN);
                gameVersion = VERSION_WII_U;
                break;
            // 3DS or Switch version
            case GAME_IDENTIFIER:
                int revision = buffer.getInt(0x04);
                if (revision < 0 || revision > 2)
                    throw new KinopioSaveException(String.format("Unknown revision found: %d", revision));
                gameVersion = buffer.getInt(0x1C) == GameData.OLD_SIZE ? VERSION_3DS : VERSION_SWITCH + revision;
                break;
            default:
                throw new KinopioSaveException("File does not seem to contain Captain Toad Treasure Tracker save data.");
        }
        
        // A lot of the other information is not of any use for us, so we just skip to the start of the game data block
        buffer.position(0x1C);
        gameData.read(buffer);
        
        // Read season data
        for (int i = 0 ; i < SEASONS_PER_VERSION[gameVersion] ; i++)
            seasonData.get(i).read(buffer);
        
        // Read course info
        int numCourses = buffer.getInt();
        
        if (numCourses - courseInfos.size() > 0)
            throw new KinopioSaveException("File seems to contain more course infos than the game could handle.");
        
        for (int i = 0 ; i < numCourses ; i++)
            courseInfos.get(i).read(buffer);
        
        // Initialize DLC lock if necessary
        handleInitAOCLock();
    }
    
    public void write(File file) throws IOException {
        int revision;
        
        switch(gameVersion) {
            case VERSION_WII_U:
            case VERSION_3DS:
            case VERSION_SWITCH:
                revision = 0;
                break;
            case VERSION_SWITCH_AOC:
                revision = 1;
                break;
            case VERSION_SWITCH_VR:
                revision = 2;
                break;
            default:
                revision = 0;
                break;
        }
        
        // Write sead stream header
        buffer.position(0x04);
        buffer.putInt(revision);
        buffer.putInt(TOTAL_FILE_SIZE);
        buffer.putInt(GAME_IDENTIFIER);
        
        // Write SaveData header
        buffer.putInt(calculateSaveDataSize());
        buffer.putInt(0);
        buffer.putInt(0);
        
        // Write GameData
        gameData.write(buffer);
        
        // Write SeasonData
        for (int i = 0 ; i < SEASONS_PER_VERSION[gameVersion] ; i++)
            seasonData.get(i).write(buffer);
        
        // Write CourseInfos
        int numStages = COURSES_PER_VERSION[gameVersion];
        buffer.putInt(numStages);
        
        for (int i = 0 ; i < numStages ; i++)
            courseInfos.get(i).write(buffer);
        
        // All remaining data is padding
        CTSe.fillPadding(buffer, buffer.remaining());
        
        // Calculate and write CRC32 checksum
        crc32.reset();
        crc32.update(buffer.array(), 4, TOTAL_FILE_SIZE - 4);
        buffer.putInt(0x00, (int)crc32.getValue());
        
        // Write file data
        Files.write(file.toPath(), buffer.array());
    }
    
    // -------------------------------------------------------------------------------------------------------------------------
    // Getters
    
    public GameData getGameData() {
        return gameData;
    }
    
    public SeasonData getSeasonData(int i) {
        return seasonData.get(i);
    }
    
    public CourseInfo getCourseInfo(int i) {
        return courseInfos.get(i);
    }
    
    public int getGameVersion() {
        return gameVersion;
    }
    
    // -------------------------------------------------------------------------------------------------------------------------
    // Helper functions for saving and updating
    
    private int calculateSaveDataSize() {
        int numSeasons = SEASONS_PER_VERSION[gameVersion];
        int numStages = COURSES_PER_VERSION[gameVersion];
        int gameDataSize = gameData.isUseOldSize ? GameData.OLD_SIZE : GameData.SIZE;
        
        // This is the sum of:
        // - SaveData header (16 bytes)
        // - GameData (block size and content)
        // - every SeasonData (block size and content)
        // - number of stages
        // - every CourseInfo contents
        return 16 + (4 + gameDataSize) + numSeasons * (4 + SeasonData.SIZE) + 4 + numStages * CourseInfo.SIZE;
    }
    
    public void updateVersionToSwitchVR() {
        int oldVersion = gameVersion;
        gameVersion = VERSION_SWITCH_VR;
        
        switch(oldVersion) {
            case VERSION_WII_U:
                buffer.order(ByteOrder.LITTLE_ENDIAN);
                gameData.miiverseSetting = true;
                gameData.unlockOdysseyLevels = gameData.exist3DWorldSaveData;
                gameData.exist3DWorldSaveData = false;
                gameData.flags &= ~GameData.FLAG_SHOW_3D_WORLD_SAVE_DATA;
                handleInitOdysseyChapter();
            case VERSION_3DS:
                // Use newer GameData block size
                gameData.isUseOldSize = false;
            case VERSION_SWITCH:
                // Season 5 data exists prior to the DLC, but its bonus level counter is set to 6, although the max is 4
                seasonData.get(4).init();
                
                // Force DLC lock
                gameData.needInitAOCLock = true;
                handleInitAOCLock();
            case VERSION_SWITCH_AOC:
            case VERSION_SWITCH_VR:
                // This is the latest version
                break;
        }
    }
    
    private void handleInitOdysseyChapter() {
        for (int i = 93 ; i < 97 ; i++) {
            CourseInfo course = courseInfos.get(i);
            course.init();

            if (gameData.unlockOdysseyLevels || (gameData.flags & GameData.FLAG_OPEN_SEASON_SP) != 0)
                course.flags = CourseInfo.FLAG_OPEN | CourseInfo.FLAG_NEW;
            else
                course.flags = CourseInfo.FLAG_LOCK;
        }
    }
    
    private void handleInitAOCLock() {
        if (gameData.needInitAOCLock) {
            for (int i = 119 ; i < 149 ; i++)
                courseInfos.get(i).flags = CourseInfo.FLAG_LOCK;
            gameData.needInitAOCLock = false;
        }
    }
}
