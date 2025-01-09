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
package com.aurumsmods.ctse;

import com.aurumsmods.ajul.ResourceLoader;
import com.aurumsmods.ajul.SwingUtil;
import com.aurumsmods.ctse.editor.KinopioSaveEditor;
import com.aurumsmods.ctse.format.StageNode;
import com.aurumsmods.ctse.format.KinopioSaveException;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.util.prefs.Preferences;
import org.json.JSONObject;
import org.json.JSONTokener;

/**
 * @author Aurum
 */
public class CTSe {
    private CTSe() { throw new IllegalStateException(); }
    
    public static final ResourceLoader ASSET_LOADER = new ResourceLoader(CTSe.class);
    public static final BufferedImage PROGRAM_ICON = ASSET_LOADER.readImage("/assets/icon.png");
    
    public static final String AUTHOR = "Aurum";
    public static final String TITLE = "CTSe";
    public static final String LONG_TITLE = "CTSe -- Captain Toad Treasure Tracker Save Editor";
    public static final String VERSION = "v1.1.2";
    public static final String COPYRIGHT = "Copyright © 2025 Aurum";
    public static final String FULL_TITLE = String.join(" -- ", LONG_TITLE, VERSION, COPYRIGHT);
    
    public static void main(String[] args) throws IOException, KinopioSaveException {
        SwingUtil.trySetSystemUI();
        
        Localization.init();
        StageNode.init();
        initSettings();
        
        new KinopioSaveEditor().setVisible(true);
    }
    
    // -------------------------------------------------------------------------------------------------------------------------
    // Setting preferences
    
    public static void initSettings() {
        Preferences prefs = Preferences.userRoot();
        Localization.setLocalization(prefs.get("ctse.localization", "en_US"));
    }
    
    public static void saveSettings() {
        Preferences prefs = Preferences.userRoot();
        prefs.put("ctse.localization", Localization.getLocalization().getId());
    }
    
    // -------------------------------------------------------------------------------------------------------------------------
    // Utility functions
    
    public static void fillPadding(ByteBuffer buf, int count) {
        byte[] arr = buf.array();
        int pos = buf.position();
        int end = pos + count;
        
        for (int i = pos ; i < end ; i++)
            arr[i] = 0;
        
        buf.position(end);
    }
    
    public static JSONObject readJSONObject(String path) {
        return new JSONObject(new JSONTokener(new InputStreamReader(ASSET_LOADER.openStream(path), StandardCharsets.UTF_8)));
    }
}
