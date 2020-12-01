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

import javax.swing.tree.DefaultMutableTreeNode;

public class StageNode extends DefaultMutableTreeNode {
    public static final int NODE_SEASON = 0;     // Books and main categories
    public static final int NODE_CHAPTER = 1;    // Chapters and sub categories
    public static final int NODE_STAGE = 2;      // Actual levels with proper parameters
    public static final int NODE_HIDE_STAGE = 3; // These stages are completely hidden by default
    
    private static final int LOCKED_VERSION_FLAG = 7; // nodes will only appear for the specified game version
    
    public static final int TYPE_NORMAL = 0;
    public static final int TYPE_PROLOGUE = 1;
    public static final int TYPE_ILLUSTRATION = 2;
    public static final int TYPE_ILLUSTRATION_DEMO = 3;
    public static final int TYPE_BONUS = 4;
    public static final int TYPE_MINI_GAME = 5; // GameOverStage
    public static final int TYPE_TRICK_ART = 6; // TrickArt*Stage
    public static final int TYPE_SPECIAL_3D_WORLD = 7;
    public static final int TYPE_SPECIAL_CHERRY = 8; // ToadBrigade*Stage
    public static final int TYPE_SPECIAL_MANEK = 9; // GhostPlayer*Stage
    public static final int TYPE_SPECIAL_PROLOGUE = 10;
    public static final int TYPE_SPECIAL_BONUS = 11;
    public static final int TYPE_SPECIAL_OTHER = 12; // GhostPlayerMaze3Stage
    public static final int TYPE_LABYRINTH = 13; // MoleMazeStage
    public static final int TYPE_NOT_A_STAGE = -1;
    
    private static final int FLAG_DOT_KINOPIO = 0;        // has Pixel Toad
    private static final int FLAG_COLLECT_ITEM = 1;       // has Super Gems
    private static final int FLAG_BADGE_CONDITION = 2;    // has bonus objective
    private static final int FLAG_HAS_CHALLENGE_TIME = 3; // has time trial
    private static final int FLAG_DLC_COLLECT_ITEM = 4;   // uses blue Super Gems
    private static final int FLAG_PREVIEW_IMAGE = 5;      // has a preview image
    
    //--------------------------------------------------------------------------------------------------------------------------
    
    private final StageNode constParent;
    private final String stageName;
    private final int courseId, stageType;
    private final byte gameVersion, flags;
    private final int challengeTime, nodeType, nodeIcon;
    
    public StageNode(StageNode parent, String name, int id, int ct, byte ver, byte flags, int time, int nt, int ni) {
        constParent = parent;
        
        stageName = name;
        courseId = id;
        stageType = ct;
        gameVersion = ver;
        this.flags = flags;
        challengeTime = time;
        nodeType = nt;
        nodeIcon = ni;
    }
    
    @Override
    public String toString() {
        return CommonAssets.getText(String.format("stage.name.%s", stageName));
    }
    
    public String getStageName() {
        return stageName;
    }
    
    public int getCourseId() {
        return courseId;
    }
    
    public int getStageType() {
        return stageType;
    }
    
    public int getGameVersion() {
        return gameVersion;
    }
    
    public boolean hasDotKinopio() {
        return BitUtil.test(flags, FLAG_DOT_KINOPIO);
    }
    
    public boolean hasCollectItem() {
        return BitUtil.test(flags, FLAG_COLLECT_ITEM);
    }
    
    public boolean hasBadgeCondition() {
        return BitUtil.test(flags, FLAG_BADGE_CONDITION);
    }
    
    public boolean hasChallengeTime() {
        return BitUtil.test(flags, FLAG_HAS_CHALLENGE_TIME);
    }
    
    public boolean hasDlcCollectItem() {
        return BitUtil.test(flags, FLAG_DLC_COLLECT_ITEM);
    }
    
    public boolean hasPreviewImage() {
        return BitUtil.test(flags, FLAG_PREVIEW_IMAGE);
    }
    
    public int getChallengeTime() {
        return challengeTime;
    }
    
    public int getNodeType() {
        return nodeType;
    }
    
    public int getNodeIcon() {
        return nodeIcon;
    }
    
    public void chopTree() {
        removeFromParent();
    }
    
    public void plantTree(int ver) {
        if (constParent == null)
            return;
        
        int maskver = gameVersion & 0x7F;
        
        if (nodeType != NODE_HIDE_STAGE && (BitUtil.test(gameVersion, LOCKED_VERSION_FLAG) ? ver == maskver : ver >= maskver))
            constParent.add(this);
    }
}
