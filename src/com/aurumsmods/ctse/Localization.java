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

package com.aurumsmods.ctse;

import java.util.Collections;
import java.util.Map;
import org.json.JSONArray;
import org.json.JSONObject;

/**
 * @author Aurum
 */
public final class Localization {
    //--------------------------------------------------------------------------------------------------------------------------
    // Global localization handler
    
    private static Localization CURRENT;
    private static Map<String, Object> LOCALIZATIONS; // unmodifiable
    
    public static void init() {
        if (LOCALIZATIONS != null)
            throw new IllegalStateException("Localizations already initialized! Cannot initialize again.");
        
        JSONObject raw = CTSe.readJSONObject("/assets/text/LocalizationInfo.json");
        
        // fatal case
        if (raw == null) {
            System.err.println("Fatal! LocalizationInfo.json could not be loaded!");
            System.exit(-1);
        }
        
        LOCALIZATIONS = Collections.unmodifiableMap(raw.toMap());
    }
    
    public static Localization getLocalization() {
        return CURRENT;
    }
    
    public static Localization setLocalization(String id) {
        return CURRENT = new Localization(id);
    }
    
    public static Map<String, Object> getLocalizations() {
        return LOCALIZATIONS;
    }
    
    public static String getLocalizationId() {
        return CURRENT.getId();
    }
    
    //--------------------------------------------------------------------------------------------------------------------------
    // Localization implementation
    
    private final String id;
    private final JSONObject content;
    
    private Localization(String id) {
        if (!LOCALIZATIONS.containsKey(id))
            id = "en_US";
        
        this.id = id;
        content = CTSe.readJSONObject(String.format("/assets/text/%s.json", id));
    }
    
    public String getId() {
        return id;
    }
    
    public String getText(String key) {
        if (content != null && content.has(key))
            return content.getString(key);
        return key;
    }
    
    public JSONArray getTextList(String key) {
        if (content != null && content.has(key))
            return content.getJSONArray(key);
        return null;
    }
    
    public String getIndexedText(String key, int index) {
        JSONArray list = getTextList(key);
        if (list != null && 0 <= index && index < list.length())
            return list.getString(index);
        return String.format("%s.%d", key, index);
    }
}
