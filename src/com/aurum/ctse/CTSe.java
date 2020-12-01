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

import java.util.prefs.Preferences;
import javax.swing.JFrame;
import javax.swing.UIManager;

public final class CTSe {
    private CTSe() {}
    
    private static final String TITLE = "CTSe -- Captain Toad Save Editor -- © 2020 Aurum";
    private static final String VERSION = "v1.0 Beta 1";
    private static final String FULL_TITLE = TITLE + '\n' + VERSION;
    private static final String GIT_USER = "SunakazeKun";
    private static final String GIT_REPOSITORY = "CTSe";
    private static final String GIT_URL = "http://github.com/" + GIT_USER + '/' + GIT_REPOSITORY;
    
    public static String getTitle() {
        return TITLE;
    }
    
    public static String getVersion() {
        return VERSION;
    }
    
    public static String getFullTitle() {
        return FULL_TITLE;
    }

    public static String getGitUser() {
        return GIT_USER;
    }

    public static String getGitRepository() {
        return GIT_REPOSITORY;
    }

    public static String getGitUrl() {
        return GIT_URL;
    }
    
    public static void main(String[] args) {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        }
        catch (Exception ex) {
            System.err.print(ex);
        }
        
        initSettings();
        CommonAssets.initLocalization();
        CommonAssets.initLevelNodes();
        
        JFrame editor = new SaveEditor();
        editor.setVisible(true);
    }
    
    //--------------------------------------------------------------------------------------------------------------------------
    
    private static String LOCALIZATION;
    
    public static void initSettings() {
        Preferences prefs = Preferences.userRoot();
        LOCALIZATION = prefs.get("ctse.localization", "en_US");
    }
    
    public static void saveSettings() {
        Preferences prefs = Preferences.userRoot();
        prefs.put("ctse.localization", LOCALIZATION);
    }
    
    public static String getLocalization() {
        return LOCALIZATION;
    }
    
    public static void setLocalization(String localization) {
        LOCALIZATION = localization;
    }
}
