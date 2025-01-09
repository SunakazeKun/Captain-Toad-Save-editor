/*
 * Copyright (C) 2022 - 2025 Aurum
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either verAndItems 3 of the License, or
 * (at your option) any later verAndItems.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package com.aurumsmods.ctse.format;

import com.aurumsmods.ctse.CTSe;
import com.aurumsmods.ctse.Localization;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.charset.StandardCharsets;
import java.util.LinkedList;
import java.util.List;
import java.util.function.Predicate;
import javax.swing.tree.DefaultMutableTreeNode;

/**
 * @author Aurum
 */
public class StageNode extends DefaultMutableTreeNode {
    // -------------------------------------------------------------------------------------------------------------------------
    // StageNode root singleton and node parser
    
    private static final StageNode ROOT_NODE = new StageNode();
    private static StageNode[] STAGE_NODES;
    
    public static void init() {
        if (STAGE_NODES != null)
            throw new IllegalStateException("StageNodes already initialized!");
        
        byte[] raw = CTSe.ASSET_LOADER.readBinary("/assets/bin/StageNodeInfo.bin");
        
        // Fatal case, cannot proceed code execution of the entire program without proper node data
        if (raw == null) {
            System.err.println("Fatal! StageNodeInfo.bin could not be loaded!");
            System.exit(-1);
        }
        
        // Create and prepare buffer
        ByteBuffer buf = ByteBuffer.wrap(raw);
        buf.order(ByteOrder.LITTLE_ENDIAN);
        
        // Read header
        int numNodes = buf.getInt();
        int numStrings = buf.getInt();
        int lenStrings = buf.getInt();
        int lenTotal = buf.getInt();
        
        assert lenTotal == buf.capacity() && lenTotal == 0x10 + lenStrings + numNodes * 0xC;
        
        // Read strings from pool
        String[] strings = new String[numStrings];
        
        for (int i = 0 ; i < numStrings ; i++) {
            byte[] bufString = new byte[buf.getShort() & 0xFFFF];
            buf.get(bufString);
            strings[i] = new String(bufString, StandardCharsets.UTF_8);
        }
        
        // Parse and create all nodes
        STAGE_NODES = new StageNode[numNodes];
        
        LinkedList<StageNode> stack = new LinkedList();
        stack.add(ROOT_NODE);
        
        for (int i = 0 ; i < numNodes ; i++) {
            // Read stage information
            String name = strings[buf.getShort() & 0xFFFF];
            int dataId = buf.getShort();
            int pageId = buf.getShort();
            int stageType = buf.get();
            int nodeInfo = buf.get() & 0xFF;
            int verAndItems = buf.get() & 0xFF;
            int flags = buf.get() & 0xFF;
            int challengeTime = buf.getShort();
            
            int depth = nodeInfo & 0xF;
            int iconId = nodeInfo >>> 4;
            int version = verAndItems & 0xF;
            int collectItemNum = verAndItems >>> 4;
            
            assert depth != 0; // 0 is reserved for root node
            
            // Create stage node
            while(depth < stack.size())
                stack.removeLast();
            
            StageNode node = new StageNode(
                stack.getLast(), name, dataId, pageId, stageType, iconId, version, collectItemNum, flags, challengeTime
            );
            STAGE_NODES[i] = node;
            
            if (depth == stack.size())
                stack.addLast(node);
        }
    }
    
    public static StageNode getRootNode() {
        return ROOT_NODE;
    }
    
    private static boolean checkVersionSupportsStage(StageNode node, int ver) {
        switch(node.versionThreshold) {
            case 0: return true;
            case 1: return ver == KinopioSaveData.VERSION_WII_U;
            case 2: return ver >= KinopioSaveData.VERSION_SWITCH;
            case 3: return ver >= KinopioSaveData.VERSION_SWITCH_AOC;
            case 4: return ver == KinopioSaveData.VERSION_SWITCH_AOC;
            case 5: return ver == KinopioSaveData.VERSION_SWITCH_VR;
        }
        
        return false;
    }
    
    public static void growTree(int ver) {
        for (StageNode node : STAGE_NODES) {
            if (node.constParent == null)
                break;
            
            if (checkVersionSupportsStage(node, ver) && !node.isHideNode())
                node.constParent.add(node);
            else
                node.removeFromParent();
        }
    }
    
    private static void collectSeasonPagesWithPredicate(List<StageNode> output, int version, int season, Predicate<StageNode> pred) {
        output.clear();
        
        // Find season node beginning
        int seasonStartIdx = -1;
        
        for (int i = 0 ; i < STAGE_NODES.length ; i++) {
            StageNode node = STAGE_NODES[i];
            
            if (node.getSeasonId() == season && checkVersionSupportsStage(node, version)) {
                seasonStartIdx = i + 1;
                break;
            }
        }
        
        // No season start found -> no valid season
        if (seasonStartIdx < 0)
            return;
        
        // Collect pages
        for (int i = seasonStartIdx ; i < STAGE_NODES.length ; i++) {
            StageNode node = STAGE_NODES[i];
            
            if (!checkVersionSupportsStage(node, version))
                continue;
            if (node.isSeason())
                break;
            
            if (pred.test(node))
                output.add(node);
        }
    }
    
    public static void collectSeasonActualPages(List<StageNode> output, int version, int season) {
        collectSeasonPagesWithPredicate(output, version, season, (n) -> 0 <= n.pageId && n.pageId < 100 );
    }
    
    public static void collectSeasonValidBonusPages(List<StageNode> output, int version, int season) {
        collectSeasonPagesWithPredicate(output, version, season, (n) -> n.pageId >= 0 && n.isNormalStage() && !n.isBonusStage() );
    }
    
    // -------------------------------------------------------------------------------------------------------------------------
    // Stage type constants and flag masks declaration
    
    public static final int TYPE_SEASON            = 0;  // SeasonX
    public static final int TYPE_PROLOGUE          = 1;  // e.g. Season1OpeningStage
    public static final int TYPE_ILLUSTRATION      = 2;  // ChapterX_Y
    public static final int TYPE_ILLUSTRATION_DEMO = 3;  // e.g. Season1IntermissionStage
    public static final int TYPE_MINI_GAME         = 4;  // GameOverStage
    public static final int TYPE_TRICK_ART         = 5;  // TrickArt*Stage
    public static final int TYPE_NORMAL            = 6;  // any stage
    public static final int TYPE_BONUS             = 7;  // e.g. Bonus1Stage, GhostPlayerMaze2Stage
    public static final int TYPE_SPECIAL_3D_WORLD  = 8;  // e.g. EnterCatMarioStage, CityWorldStage
    public static final int TYPE_SPECIAL_CHERRY    = 9;  // ToadBrigade*Stage
    public static final int TYPE_SPECIAL_MANEK     = 10; // GhostPlayer*Stage
    public static final int TYPE_SPECIAL_PROLOGUE  = 11; // e.g. Season1OpeningStage
    public static final int TYPE_SPECIAL_BONUS     = 12; // BonusXStage
    public static final int TYPE_SPECIAL_OTHER     = 13; // GhostPlayerMaze3Stage
    public static final int TYPE_LABYRINTH         = 14; // MoleMazeStage
    public static final int TYPE_NOT_A_STAGE       = -1; // used by category nodes that do not hold stage info
    
    private static final int FLAG_HAS_DOT_KINOPIO            = 0b000001;
    private static final int FLAG_HAS_DLC_COLLECT_ITEM       = 0b000010;
    private static final int FLAG_HAS_BADGE_CONDITION        = 0b000100;
    private static final int FLAG_HAS_CHALLENGE_TIME         = 0b001000;
    private static final int FLAG_IS_VR_USE_ORIGINAL_PREVIEW = 0b010000;
    private static final int FLAG_HAS_PREVIEW_IMAGE          = 0b100000;
    
    // -------------------------------------------------------------------------------------------------------------------------
    // Actual StageNode implementation
    
    private final StageNode constParent;
    private final String stageName;
    private final int dataId, pageId, stageType, iconId, versionThreshold, collectItemNum, flags, challengeTime;
    
    private StageNode() {
        this(null, "Root", -1, -1, TYPE_NOT_A_STAGE, 0, 0, 0, 0, 0);
    }
    
    private StageNode(StageNode parent, String name, int id, int page, int type, int icon, int version, int items, int flgs, int time) {
        constParent = parent;
        
        stageName = name;
        dataId = id;
        pageId = page;
        stageType = type;
        iconId = icon;
        versionThreshold = version;
        collectItemNum = items;
        flags = flgs;
        challengeTime = time;
    }
    
    public String toString() {
        return Localization.getLocalization().getText(String.format("stage.name.%s", stageName));
    }
    
    public String getStageName() {
        return stageName;
    }
    
    public int getCourseId() {
        return stageType != TYPE_SEASON ? dataId : -1;
    }
    
    public int getSeasonId() {
        return stageType == TYPE_SEASON ? dataId : -1;
    }
    
    public int getPageId() {
        return pageId;
    }
    
    public int getStageType() {
        return stageType;
    }
    
    public int getIconId() {
        return iconId;
    }
    
    public int getCollectItemNum() {
        return collectItemNum;
    }
    
    public int getChallengeTime() {
        return challengeTime;
    }
    
    public boolean isSeason() {
        return stageType == TYPE_SEASON;
    }
    
    public boolean isIllustration() {
        return stageType == TYPE_ILLUSTRATION || stageType == TYPE_ILLUSTRATION_DEMO;
    }
    
    public boolean isNormalStage() {
        return stageType >= TYPE_NORMAL;
    }
    
    public boolean isPrologueStage() {
        return stageType == TYPE_PROLOGUE;
    }
    
    public boolean isBonusStage() {
        return stageType == TYPE_BONUS;
    }
    
    public boolean isMiniGameStage() {
        return stageType == TYPE_MINI_GAME;
    }
    
    public boolean isManeckFinalFloorCountStage() {
        return stageType == TYPE_SPECIAL_OTHER;
    }
    
    public boolean isMoleMazeFinalFloorCountStage() {
        return stageType == TYPE_LABYRINTH;
    }
    
    public boolean hasDotKinopio() {
        return (flags & FLAG_HAS_DOT_KINOPIO) != 0;
    }
    
    public boolean hasDlcCollectItem() {
        return (flags & FLAG_HAS_DLC_COLLECT_ITEM) != 0;
    }
    
    public boolean hasBadgeCondition() {
        return (flags & FLAG_HAS_BADGE_CONDITION) != 0;
    }
    
    public boolean hasChallengeTime() {
        return (flags & FLAG_HAS_CHALLENGE_TIME) != 0;
    }
    
    public boolean isVRUseOriginalPreview() {
        return (flags & FLAG_IS_VR_USE_ORIGINAL_PREVIEW) != 0;
    }
    
    public boolean hasPreviewImage() {
        return (flags & FLAG_HAS_PREVIEW_IMAGE) != 0;
    }
    
    public boolean isHideNode() {
        return stageType == TYPE_PROLOGUE || stageType == TYPE_MINI_GAME || stageType == TYPE_TRICK_ART;
    }
}
