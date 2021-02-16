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
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import org.json.JSONObject;
import org.json.JSONTokener;

public final class CommonAssets {
    /**
     * Private constructor to prevent instantiation.
     */
    private CommonAssets() {}
    
    /**
     * Opens an input stream from the resource specified by the given path.
     * @param path the file path
     * @return an input stream from the resource
     */
    public static InputStream openStream(String path) {
        return CommonAssets.class.getResourceAsStream("/assets/" + path);
    }
    
    /**
     * Reads and returns all bytes from the specified file.
     * @param path the file path
     * @return all bytes from file
     */
    public static byte[] loadBytes(String path) {
        byte[] ret;
        
        try (InputStream in = openStream(path)) {
            ret = new byte[in.available()];
            in.read(ret);
        }
        catch(IOException ex) {
            ret = new byte[0];
            System.err.print(ex);
        }
        
        return ret;
    }
    
    /**
     * Loads an image from the specifed file.
     * @param path the file path
     * @return the image
     */
    public static BufferedImage loadImage(String path) {
        try (InputStream in = openStream(path)) {
            return in == null ? null : ImageIO.read(in);
        }
        catch(IOException ex) {
            System.err.print(ex);
        }
        
        return null;
    }
    
    /**
     * Loads an image icon from the specified file.
     * @param path the file path
     * @return the image icon
     */
    public static ImageIcon loadIcon(String path) {
        BufferedImage img = loadImage(path);
        return img == null ? null : new ImageIcon(loadImage(path));
    }
    
    /**
     * Loads JSON data from the specified file. The returned value is either a JSONArray or a JSONObject.
     * @param path the file path
     * @return the JSON data
     */
    public static Object loadJson(String path) {
        return new JSONObject(new JSONTokener(new InputStreamReader(openStream(path), StandardCharsets.UTF_8)));
    }
}
