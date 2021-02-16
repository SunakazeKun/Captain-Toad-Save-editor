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

import java.awt.image.BufferedImage;
import java.util.prefs.Preferences;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;

public final class CTSe {
    private CTSe() {}
    
    public static final String TITLE = "CTSe -- Captain Toad Save editor -- © 2021 Aurum";
    public static final String VERSION = "v1.0 Beta 2";
    public static final String FULL_TITLE = TITLE + '\n' + VERSION;
    public static final String GIT_USER = "SunakazeKun";
    public static final String GIT_REPOSITORY = "CTSe";
    public static final String GIT_URL = "http://github.com/" + GIT_USER + '/' + GIT_REPOSITORY;
    public static final BufferedImage PROGRAM_ICON = CommonAssets.loadImage("icon.png");
    
    public static void main(String[] args) {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        }
        catch (ClassNotFoundException | IllegalAccessException | InstantiationException | UnsupportedLookAndFeelException ex) {
            System.err.print("Could not set System Look and Feel: " + ex);
        }
        
        Localization.init();
        StageNode.init();
        initSettings();
        
        new SaveEditor().setVisible(true);
    }
    
    //--------------------------------------------------------------------------------------------------------------------------
    
    /**
     * Loads all preferences from the registry and initializes the last used localization.
     */
    public static void initSettings() {
        Preferences prefs = Preferences.userRoot();
        Localization.setLocalization(prefs.get("ctse.localization", "en_US"));
    }
    
    /**
     * Stores all preferences in the registry.
     */
    public static void saveSettings() {
        Preferences prefs = Preferences.userRoot();
        prefs.put("ctse.localization", Localization.getLocalization().getId());
    }
}
