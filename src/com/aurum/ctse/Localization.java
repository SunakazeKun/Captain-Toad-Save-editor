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

import java.util.Collections;
import java.util.Map;
import org.json.JSONArray;
import org.json.JSONObject;

public final class Localization {
    private static Localization CURRENT;
    private static Map<String, Object> LOCALIZATIONS; // unmodifiable
    
    /**
     * Initializes an unmodifiable map of available localizations and populates it with the information stored in<br>
     * <i>/assets/text/LocalizationInfo.json</i>.
     * <p>
     * It should be called only once! Any subsequent call will throw an <i>IllegalStateException</i>.
     */
    public static void init() {
        if (LOCALIZATIONS != null)
            throw new IllegalStateException("Localizations already initialized! Cannot initialize again.");
        
        JSONObject raw = (JSONObject)CommonAssets.loadJson("text/LocalizationInfo.json");
        
        // fatal case
        if (raw == null) {
            System.err.println("Fatal! LocalizationInfo.json could not be loaded!");
            System.exit(-1);
        }
        
        LOCALIZATIONS = Collections.unmodifiableMap(raw.toMap());
    }
    
    /**
     * Returns the currently used Localization instance.
     * @return the current localization
     */
    public static Localization getLocalization() {
        return CURRENT;
    }
    
    /**
     * Creates and loads a new localization specified by the localization identifier. The currently active localization instance
     * will be set to the new instance. The new localization is returned as well.
     * @param id the identifier of the localization to be loaded
     * @return the newly created localization
     */
    public static Localization setLocalization(String id) {
        return CURRENT = new Localization(id);
    }
    
    /**
     * Returns the read-only Map of available localizations. Key is the localization identifier and value is a descriptive name
     * that will be displayed on the SaveEditor form.
     * @return read-only Map of available localizations
     */
    public static Map<String, Object> getLocalizations() {
        return LOCALIZATIONS;
    }
    
    //--------------------------------------------------------------------------------------------------------------------------
    
    private final String id;
    private final JSONObject content;
    
    /**
     * Creates a new localization as specified by the identifier. The text content is loaded from<br>
     * <i>/assets/text/<b>id</b>.json</i>. If the specified ID is not supported, en_US will be loaded as a fail-safe.
     * @param id the localization identifier
     */
    private Localization(String id) {
        if (!LOCALIZATIONS.containsKey(id))
            id = "en_US";
        
        this.id = id;
        content = (JSONObject)CommonAssets.loadJson(String.format("text/%s.json", id));
    }
    
    /**
     * Returns the identifier of this localization.
     * @return the identifier
     */
    public String getId() {
        return id;
    }
    
    /**
     * Returns the localized text specified by the key. If the localization's content is null or if it does not contain the
     * specified key, the key is returned instead.
     * @param key the text's key
     * @return the text specified by the key if it exists, otherwise the key itself
     */
    public String getText(String key) {
        if (content != null && content.has(key))
            return content.getString(key);
        return key;
    }
    
    /**
     * Returns the list of localized text specified by the key. If the localization's content is null or if it does not contain
     * the specified key, null is returned instead.
     * @param key the list's key
     * @return the list specified by the key if it exists, otherwise null
     */
    public JSONArray getTextList(String key) {
        if (content != null && content.has(key))
            return content.getJSONArray(key);
        return null;
    }
    
    /**
     * Returns the text at the specified index in the text list specified by the key. If the list does not exist or if the index
     * is out of the list's range, a String combining the key and index is returned.
     * @param key the list's key
     * @param index index into the text list
     * @return the text if it exists, otherwise a String combining the key and index
     */
    public String getIndexedText(String key, int index) {
        JSONArray list = getTextList(key);
        if (list != null && 0 <= index && index < list.length())
            return list.getString(index);
        return String.format("%s.%d", key, index);
    }
}
