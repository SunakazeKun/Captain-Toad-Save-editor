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

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.charset.StandardCharsets;
import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

public final class CommonAssets {
    private CommonAssets() {}
    
    public static final BufferedImage PROGRAM_ICON = loadImage("icon.png");
    
    public static InputStream openStream(String path) throws IOException {
        return CommonAssets.class.getResourceAsStream("/assets/" + path);
    }
    
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
    
    public static BufferedImage loadImage(String path) {
        try (InputStream in = openStream(path)) {
            return in == null ? null : ImageIO.read(in);
        }
        catch(IOException ex) {
            System.err.print(ex);
        }
        
        return null;
    }
    
    public static ImageIcon loadIcon(String path) {
        BufferedImage img = loadImage(path);
        return img == null ? null : new ImageIcon(loadImage(path));
    }
    
    //--------------------------------------------------------------------------------------------------------------------------
    
    private static JSONObject LOCALIZATION;
    
    public static void initLocalization() {
        try {
            String path = String.format("text/%s.json", CTSe.getLocalization());
            LOCALIZATION = (JSONObject)new JSONParser().parse(new InputStreamReader(openStream(path), StandardCharsets.UTF_8));
        }
        catch (IOException | ParseException ex) {
            System.err.println(ex);
        }
    }
    
    public static String getText(String key) {
        if (LOCALIZATION != null && LOCALIZATION.containsKey(key))
            return LOCALIZATION.get(key).toString();
        return key;
    }
    
    public static JSONArray getTextList(String key) {
        if (LOCALIZATION != null && LOCALIZATION.containsKey(key))
            return (JSONArray)LOCALIZATION.get(key);
        return null;
    }
    
    public static String getIndexedText(String key, int index) {
        JSONArray list = getTextList(key);
        if (list != null && index < list.size())
            return list.get(index).toString();
        return String.format("%s.%d", key, index);
    }
    
    //--------------------------------------------------------------------------------------------------------------------------
    
    private static final StageNode ROOT_NODE = new StageNode(null, "Root", -1, StageNode.TYPE_NOT_A_STAGE, (byte)127, (byte)0, 0, -1, 0);
    private static StageNode[] LEVEL_NODES;
    
    public static void initLevelNodes() {
        if (LEVEL_NODES != null)
            throw new IllegalStateException("LevelNodes already initialized!");
        
        ByteBuffer buf = ByteBuffer.wrap(loadBytes("bin/LevelNodeInfo.bin"));
        buf.order(ByteOrder.BIG_ENDIAN);
        
        int numNodes = buf.getInt();
        int numStrings = buf.getInt();
        int lenStrings = buf.getInt();
        int lenTotal = buf.getInt();
        
        assert lenTotal == buf.capacity() && lenTotal == 0x10 + lenStrings + numNodes * 0xA;
        
        LEVEL_NODES = new StageNode[numNodes];
        String[] strings = new String[numStrings];
        
        for (int i = 0 ; i < numStrings ; i++) {
            byte[]bufString = new byte[buf.getShort() & 0xFFFF];
            buf.get(bufString);
            strings[i] = new String(bufString, StandardCharsets.UTF_8);
        }
        
        StageNode curSeason = null;
        StageNode curChapter = null;
        
        for (int i = 0 ; i < numNodes ; i++) {
            String name = strings[buf.getShort() & 0xFFFF];
            int id = buf.getShort();
            byte type = buf.get();
            byte nodeInfo = buf.get();
            byte version = buf.get();
            byte flags = buf.get();
            int time = buf.getShort();
            int nodeType = nodeInfo & 0xF;
            int nodeIcon = (nodeInfo >>> 4) & 0xF;
            
            StageNode node;
            
            switch (nodeType) {
                case StageNode.NODE_SEASON:
                    node = new StageNode(ROOT_NODE, name, id, type, version, flags, time, nodeType, nodeIcon);
                    curSeason = node;
                    curChapter = node;
                    break;
                case StageNode.NODE_CHAPTER:
                    node = new StageNode(curSeason, name, id, type, version, flags, time, nodeType, nodeIcon);
                    curChapter = node;
                    break;
                default:
                    node = new StageNode(curChapter, name, id, type, version, flags, time, nodeType, nodeIcon);
                    break;
            }
            
            LEVEL_NODES[i] = node;
        }
    }
    
    public static StageNode getLevelNodeRoot() {
        return ROOT_NODE;
    }
    
    public static void reloadTree(int ver) {
        for (StageNode n : LEVEL_NODES)
            n.plantTree(ver);
    }
    
    //--------------------------------------------------------------------------------------------------------------------------
    
    public static final ImageIcon[] ICONS_NODES = {
        loadIcon("img/nodes/Default.png"),
        loadIcon("img/nodes/Season.png"),
        loadIcon("img/nodes/Chapter.png"),
        loadIcon("img/nodes/Star.png"),
        loadIcon("img/nodes/Shine.png"),
        loadIcon("img/nodes/Crown.png"),
        loadIcon("img/nodes/ToadBrigade.png"),
        loadIcon("img/nodes/GhostPlayer.png"),
        loadIcon("img/nodes/GhostPlayerMaze.png"),
        loadIcon("img/nodes/Bonus.png")
    };
    
    public static final ImageIcon[] ICONS_COLLECT_ITEMS = {
        loadIcon("img/collectItem/unselected.png"),
        loadIcon("img/collectItem/selected.png"),
        loadIcon("img/collectItem/selectedAoC.png")
    };
}
