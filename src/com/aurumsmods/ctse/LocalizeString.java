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
package com.aurumsmods.ctse;

import java.util.Objects;

/**
 * @author Aurum
 */
public class LocalizeString {
    private final String string;
    
    public LocalizeString(String key) {
        string = Objects.requireNonNull(key);
    }
    
    public String getKey() {
        return string;
    }
    
    @Override
    public String toString() {
        return Localization.getLocalization().getText(string);
    }
    
    @Override
    public int hashCode() {
        return string.hashCode();
    }
    
    @Override
    public boolean equals(Object o) {
        if (o == null)
            return false;
        if (!(o instanceof LocalizeString))
            return false;
        return string.equals(((LocalizeString)o).string);
    }
}
