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

import static com.aurum.ctse.CommonAssets.loadIcon;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.charset.StandardCharsets;
import java.util.LinkedList;
import javax.swing.ImageIcon;
import javax.swing.tree.DefaultMutableTreeNode;

public class StageNode extends DefaultMutableTreeNode {
    public static final int TYPE_PROLOGUE = 0;          // e.g. Season1OpeningStage
    public static final int TYPE_ILLUSTRATION = 1;      // ChapterX_Y
    public static final int TYPE_ILLUSTRATION_DEMO = 2; // e.g. Season1IntermissionStage
    public static final int TYPE_MINI_GAME = 3;         // GameOverStage
    public static final int TYPE_TRICK_ART = 4;         // TrickArt*Stage
    public static final int TYPE_NORMAL = 5;            // any stage
    public static final int TYPE_BONUS = 6;             // e.g. Bonus1Stage, GhostPlayerMaze2Stage
    public static final int TYPE_SPECIAL_3D_WORLD = 7;  // e.g. EnterCatMarioStage, CityWorldStage
    public static final int TYPE_SPECIAL_CHERRY = 8;    // ToadBrigade*Stage
    public static final int TYPE_SPECIAL_MANEK = 9;     // GhostPlayer*Stage
    public static final int TYPE_SPECIAL_PROLOGUE = 10; // e.g. Season1OpeningStage
    public static final int TYPE_SPECIAL_BONUS = 11;    // BonusXStage
    public static final int TYPE_SPECIAL_OTHER = 12;    // GhostPlayerMaze3Stage
    public static final int TYPE_LABYRINTH = 13;        // MoleMazeStage
    public static final int TYPE_NOT_A_STAGE = -1;      // used by category nodes that do not hold stage info
    
    private static final int VERSION_MASK = 0x1F;      // used to retrieve revision ID
    private static final int VER_FLAG_WII_U_ONLY = 5;  // stage appears only in Wii U version
    private static final int VER_FLAG_SWITCH_ONLY = 6; // stage appears only in Switch version
    private static final int VER_FLAG_LOCKED = 7;      // nodes will only appear for the specified revision
    public static final int WII_U_VERSION = 0x20;      // Wii U version mask
    public static final int SWITCH_VERSION = 0x40;     // Switch version mask
    
    private static final int FLAG_DOT_KINOPIO = 0;        // has Pixel Toad
    private static final int FLAG_COLLECT_ITEM = 1;       // has Super Gems
    private static final int FLAG_BADGE_CONDITION = 2;    // has bonus objective
    private static final int FLAG_HAS_CHALLENGE_TIME = 3; // has time trial
    private static final int FLAG_DLC_COLLECT_ITEM = 4;   // uses blue Super Gems
    private static final int FLAG_PREVIEW_IMAGE = 5;      // has a preview image
    
    private static final ImageIcon[] ICONS_NODES = {
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
    
    //--------------------------------------------------------------------------------------------------------------------------
    
    private static final StageNode ROOT_NODE = new StageNode();
    private static StageNode[] STAGE_NODES;
    
    /**
     * Initializes the stage nodes using the data in <i>/assets/bin/StageNodeInfo.bin</i>. The format of this file will not be
     * specified in this documentation.
     * <p>
     * It should be called only once! Any subsequent call will throw an <i>IllegalStateException</i>.
     */
    public static void init() {
        if (STAGE_NODES != null)
            throw new IllegalStateException("StageNodes already initialized! Cannot initialize again.");
        
        byte[] raw = CommonAssets.loadBytes("bin/StageNodeInfo.bin");
        
        // fatal case
        if (raw == null) {
            System.err.println("Fatal! StageNodeInfo.bin could not be loaded!");
            System.exit(-1);
        }
        
        // create and prepare ByteBuffer
        ByteBuffer buf = ByteBuffer.wrap(raw);
        buf.order(ByteOrder.BIG_ENDIAN);
        
        // read header
        int numNodes = buf.getInt(); // number of nodes
        int numStrings = buf.getInt(); // number of strings in pool
        int lenStrings = buf.getInt(); // total size of string pool
        int lenTotal = buf.getInt(); // total file size
        
        assert lenTotal == buf.capacity() && lenTotal == 0x10 + lenStrings + numNodes * 0xA;
        
        // read strings from pool
        String[] strings = new String[numStrings];
        
        for (int i = 0 ; i < numStrings ; i++) {
            byte[] bufString = new byte[buf.getShort() & 0xFFFF];
            buf.get(bufString);
            strings[i] = new String(bufString, StandardCharsets.UTF_8);
        }
        
        // parse and create all nodes
        STAGE_NODES = new StageNode[numNodes];
        
        LinkedList<StageNode> nodeStack = new LinkedList();
        nodeStack.add(ROOT_NODE);
        
        for (int i = 0 ; i < numNodes ; i++) {
            // read stage information
            String name = strings[buf.getShort() & 0xFFFF];
            int id = buf.getShort();
            int type = buf.get();
            int nodeInfo = buf.get() & 0xFF;
            int version = buf.get() & 0xFF;
            int flags = buf.get() & 0xFF;
            int time = buf.getShort();
            
            // create stage node
            int nodeDepth = nodeInfo & 0xF;
            int nodeIcon = nodeInfo >>> 4;
            
            assert nodeDepth != 0; // 0 is reserved for root node
            
            while(nodeDepth < nodeStack.size())
                nodeStack.removeLast();
            
            StageNode node = new StageNode(nodeStack.getLast(), name, id, type, version, flags, time, nodeIcon);
            STAGE_NODES[i] = node;
            
            if (nodeDepth == nodeStack.size())
                nodeStack.addLast(node);
        }
    }
    
    /**
     * Returns the root stage node to which all the other stage nodes are connected.
     * @return the root stage node
     */
    public static StageNode getRootNode() {
        return ROOT_NODE;
    }
    
    /**
     * Connects the stage nodes for the specified game version. Hidden stage nodes and those that are not supported by the
     * specified version will be detached from their parent nodes. The ver parameter should be masked on either with
     * WII_U_VERSION or with SWITCH_VERSION. Otherwise, this function will ignore the game console restriction and check the
     * version number only.
     * @param ver the game version, bits 0 to 4 are the version number, bit 5 declares if the stage is Wii U only, bit 6
     * declares if the stage is Switch only, bit 7 is ignored
     */
    public static void growTree(int ver) {
        // split version info
        boolean forWiiU = BitUtil.test(ver, VER_FLAG_WII_U_ONLY);
        boolean forSwitch = BitUtil.test(ver, VER_FLAG_SWITCH_ONLY);
        ver &= VERSION_MASK;
        
        for (StageNode n : STAGE_NODES) {
            if (n.constParent == null)
                return;
            
            // check if the current version supports the stage
            int maskver = n.gameVersion & VERSION_MASK;
            boolean stagesupportsver = forWiiU == BitUtil.test(n.gameVersion, VER_FLAG_WII_U_ONLY);
            stagesupportsver |= (forSwitch == BitUtil.test(n.gameVersion, VER_FLAG_SWITCH_ONLY));
            
            if (stagesupportsver) {
                if (BitUtil.test(n.gameVersion, VER_FLAG_LOCKED))
                    stagesupportsver = ver == maskver;
                else
                    stagesupportsver = ver >= maskver;
            }
            
            // add node if version supports stage and if stage is not hidden
            if (stagesupportsver && !n.isHideStage())
                n.constParent.add(n);
            else
                n.removeFromParent();
        }
    }
    
    //--------------------------------------------------------------------------------------------------------------------------
    
    private final StageNode constParent;
    private final String stageName;
    private final int courseId, stageType, gameVersion, flags, challengeTime, nodeIcon;
    
    /**
     * This is used to instantiate the root node.
     */
    private StageNode() {
        constParent = null;
        
        stageName = "Root";
        courseId = -1;
        stageType = TYPE_NOT_A_STAGE;
        gameVersion = VERSION_MASK;
        flags = 0;
        challengeTime = 0;
        nodeIcon = 0;
    }
    
    /**
     * Instantiates a new stage node with the specified information.
     * @param parent parent stage node
     * @param name stage name
     * @param id course ID used to retrieve save data
     * @param st stage type
     * @param ver game version
     * @param flags misc info flags
     * @param time challenge time
     * @param icon node icon ID
     */
    private StageNode(StageNode parent, String name, int id, int st, int ver, int flags, int time, int icon) {
        constParent = parent;
        
        stageName = name;
        courseId = id;
        stageType = st;
        gameVersion = ver;
        this.flags = flags;
        challengeTime = time;
        nodeIcon = icon;
    }
    
    /**
     * Returns the localized name of this stage node.
     * @return the localized name
     */
    @Override
    public String toString() {
        return Localization.getLocalization().getText(String.format("stage.name.%s", stageName));
    }
    
    /**
     * Returns the stage's file name. Some category nodes may use names that are not found in the actual game.
     * @return the stage's file name
     */
    public String getStageName() {
        return stageName;
    }
    
    /**
     * Returns the global stage ID. This is used to retrieve the StaqgeInfo entry from save data.
     * @return the global stage ID
     */
    public int getCourseId() {
        return courseId;
    }
    
    /**
     * Returns the stage type. The returned value is one of the TYPE_* constants.
     * @return the stage type
     */
    public int getStageType() {
        return stageType;
    }
    
    /**
     * Returns the major game version in which this stage was introduced.
     * @return the game version
     */
    public int getGameVersion() {
        return gameVersion;
    }
    
    /**
     * Returns true if this stage features Pixel Toad.
     * @return true if stage features Pixel Toad
     */
    public boolean hasDotKinopio() {
        return BitUtil.test(flags, FLAG_DOT_KINOPIO);
    }
    
    /**
     * Returns true if this stage has Super Gems.
     * @return true if stage has Super Gems
     */
    public boolean hasCollectItem() {
        return BitUtil.test(flags, FLAG_COLLECT_ITEM);
    }
    
    /**
     * Returns true if this stage has a bonus condition.
     * @return true if stage has bonus condition
     */
    public boolean hasBadgeCondition() {
        return BitUtil.test(flags, FLAG_BADGE_CONDITION);
    }
    
    /**
     * Returns true if this stage supports challenge times. Usually, checking if the challenge time is not 0 would be suitable,
     * however, some levels have unused challenge times.
     * @return true if stage supports challenge times
     */
    public boolean hasChallengeTime() {
        return BitUtil.test(flags, FLAG_HAS_CHALLENGE_TIME);
    }
    
    /**
     * Returns true if this stage uses the DLC-specific Super Gem icon.
     * @return true if stage uses DLC Super Gem icon
     */
    public boolean hasDlcCollectItem() {
        return BitUtil.test(flags, FLAG_DLC_COLLECT_ITEM);
    }
    
    /**
     * Returns true if the stage has a preview screenshot image. This is faster than checking if the screenshot file exists.
     * @return true if stage has preview image
     */
    public boolean hasPreviewImage() {
        return BitUtil.test(flags, FLAG_PREVIEW_IMAGE);
    }
    
    /**
     * Returns the challenge time for this stage.
     * @return the challenge time
     */
    public int getChallengeTime() {
        return challengeTime;
    }
    
    /**
     * Returns the node's image icon.
     * @return the image icon
     */
    public ImageIcon getNodeIcon() {
        return ICONS_NODES[nodeIcon];
    }
    
    /**
     * Returns true if this node describes a level stage.
     * @return true if node is a level stage
     */
    public boolean isNormalStage() {
        return stageType >= TYPE_NORMAL;
    }
    
    /**
     * Returns true if this node describes a chapter. This method checks whether the stageType equals TYPE_ILLUSTRATION or
     * TYPE_ILLUSTRATION_DEMO.
     * @return true if node is a chapter page
     */
    public boolean isChapterStage() {
        return stageType == TYPE_ILLUSTRATION || stageType == TYPE_ILLUSTRATION_DEMO;
    }
    
    /**
     * Returns true if this node is hidden from view under normal circumstances. This method checks whether the stageType equals
     * TYPE_PROLOGUE, TYPE_MINI_GAME or TYPE_TRICK_ART.
     * @return true if node should be hidden
     */
    public boolean isHideStage() {
        return stageType == TYPE_PROLOGUE || stageType == TYPE_MINI_GAME || stageType == TYPE_TRICK_ART;
    }
}
