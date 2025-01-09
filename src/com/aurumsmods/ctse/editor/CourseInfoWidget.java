/*
 * Copyright (C) 2022 - 2025 Aurum
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

package com.aurumsmods.ctse.editor;

import com.aurumsmods.ctse.format.StageNode;
import com.aurumsmods.ajul.BitUtil;
import com.aurumsmods.ctse.CTSe;
import com.aurumsmods.ctse.Localization;
import com.aurumsmods.ctse.format.CourseInfo;
import com.aurumsmods.ctse.format.GameData;
import java.util.Date;
import javax.swing.ImageIcon;

/**
 * An editor widget for editing CourseInfo information.
 * @author Aurum
 */
final class CourseInfoWidget extends javax.swing.JPanel {
    private static final ImageIcon[] ICONS_COLLECT_ITEMS = {
        CTSe.ASSET_LOADER.readIcon("/assets/img/collect_item_unselected.png"),
        CTSe.ASSET_LOADER.readIcon("/assets/img/collect_item_selected.png"),
        CTSe.ASSET_LOADER.readIcon("/assets/img/collect_item_selected_aoc.png")
    };
    
    // -------------------------------------------------------------------------------------------------------------------------
    
    private final KinopioSaveEditor editor;  // parent editor context
    CourseInfo courseInfo;                   // currently edited CourseInfo
    boolean blockInput;                      // blocks user input when necessary

    /**
     * Constructs a new CourseInfoWidget using the specified editor context.
     * @param context the save editor context.
     */
    public CourseInfoWidget(KinopioSaveEditor context) {
        initComponents();
        
        editor = context;
        courseInfo = null;
        blockInput = true;
    }
    
    /**
     * Reloads all information from the GameData. This should be invoked by the save editor after loading a save file.
     */
    void reloadData() {
        blockInput = true;
            
        if (courseInfo != null && editor.selectedNode != null) {
            // Page flags
            chkOpen.setSelected(BitUtil.testWithMask(courseInfo.flags, CourseInfo.FLAG_OPEN));
            chkClear.setSelected(BitUtil.testWithMask(courseInfo.flags, CourseInfo.FLAG_CLEAR));
            chkNew.setSelected(BitUtil.testWithMask(courseInfo.flags, CourseInfo.FLAG_NEW));
            chkLock.setSelected(BitUtil.testWithMask(courseInfo.flags, CourseInfo.FLAG_LOCK));
            chkAssistClear.setSelected(BitUtil.testWithMask(courseInfo.flags, CourseInfo.FLAG_ASSIST_CLEAR));
            chkAcquireComplete.setSelected(BitUtil.testWithMask(courseInfo.flags, CourseInfo.FLAG_ACQUIRE_COMPLETE));
            
            // Collectibles & tasks
            chkCollectItem1.setSelected(BitUtil.test(courseInfo.collectItemFlags, 0));
            chkCollectItem2.setSelected(BitUtil.test(courseInfo.collectItemFlags, 1));
            chkCollectItem3.setSelected(BitUtil.test(courseInfo.collectItemFlags, 2));
            localizeBadgeConditions();
            chkBadgeCondition0.setSelected(BitUtil.testWithMask(courseInfo.flags, CourseInfo.FLAG_BADGE_CONDITION_0));
            chkClearHideAndSeek.setSelected(BitUtil.testWithMask(courseInfo.flags, CourseInfo.FLAG_CLEAR_HIDE_AND_SEEK));
            
            // Miscellaneous
            GameData gameData = courseInfo.saveData.getGameData();
            spnManeckFinalFloorCount.setValue(gameData.maneckFinalFloorCount);
            spnMoleMazeFinalFloorCount.setValue(gameData.moleMazeFinalFloorCount);
            spnBestCoin.setValue(courseInfo.bestCoin);
            spnBestTime.setValue(courseInfo.bestTime);
            spnMissCount.setValue(courseInfo.missCount);
            spnLastPlayTime.setValue(new Date(courseInfo.lastPlayTime * 1000));
            
            reloadUI();
            blockInput = false;
        }
    }
    
    private void reloadUI() {
        StageNode node = editor.selectedNode;
        boolean hasHeadCollectibles = false;
        boolean hasHeadMisc = false;
        
        // Update preview image
        if (node.hasPreviewImage()) {
            String stageName = node.getStageName();

            // VR stages reuse the preview images of their original level counterparts
            if (node.isVRUseOriginalPreview())
                stageName = stageName.substring(2); // Cuts off "VR"

            lblPreviewImage.setIcon(CTSe.ASSET_LOADER.readIcon(String.format("/assets/img/stages/%s.png", stageName)));
            lblPreviewImage.setVisible(true);
            sep1.setVisible(true);
        }
        else {
            lblPreviewImage.setIcon(null);
            lblPreviewImage.setVisible(false);
            sep1.setVisible(false);
        }
        
        // Update CollectItem flags
        if (node.getCollectItemNum() > 0) {
            ImageIcon selicon = node.hasDlcCollectItem() ? ICONS_COLLECT_ITEMS[2] : ICONS_COLLECT_ITEMS[1];
            chkCollectItem1.setSelectedIcon(selicon);
            chkCollectItem2.setSelectedIcon(selicon);
            chkCollectItem3.setSelectedIcon(selicon);
            pnlCollectItem.setVisible(true);
            hasHeadCollectibles = true;
        }
        else {
            chkCollectItem1.setSelectedIcon(null);
            chkCollectItem2.setSelectedIcon(null);
            chkCollectItem3.setSelectedIcon(null);
            pnlCollectItem.setVisible(false);
        }
        
        // Update BadgeConditions & ClearHideAndSeek
        boolean hasBadgeCondition0 = node.hasBadgeCondition();
        boolean hasDotKinopio = node.hasDotKinopio();
        hasHeadCollectibles |= hasBadgeCondition0;
        hasHeadCollectibles |= hasDotKinopio;
        
        chkBadgeCondition0.setVisible(hasBadgeCondition0);
        chkClearHideAndSeek.setVisible(hasDotKinopio);
        
        // Update BestTime
        boolean hasChallengeTime = node.hasChallengeTime();
        hasHeadMisc |= hasChallengeTime;
        
        lblBestTimeThreshold.setText(String.format(" / %03d", node.getChallengeTime()));
        lblBestTime.setVisible(hasChallengeTime);
        pnlBestTime.setVisible(hasChallengeTime);
        
        // Update MazeFloorCount
        boolean hasManeckFinalFloorCount = node.isManeckFinalFloorCountStage();
        boolean hasMoleMazeFinalFloorCount = node.isMoleMazeFinalFloorCountStage();
        hasHeadMisc |= hasManeckFinalFloorCount;
        hasHeadMisc |= hasMoleMazeFinalFloorCount;
        
        lblManeckFinalFloorCount.setVisible(hasManeckFinalFloorCount);
        spnManeckFinalFloorCount.setVisible(hasManeckFinalFloorCount);
        lblMoleMazeFinalFloorCount.setVisible(hasMoleMazeFinalFloorCount);
        spnMoleMazeFinalFloorCount.setVisible(hasMoleMazeFinalFloorCount);
        
        // Update other properties
        boolean isStageUseCourseParam = node.isNormalStage();
        hasHeadMisc |= isStageUseCourseParam;
        
        lblBestCoin.setVisible(isStageUseCourseParam);
        spnBestCoin.setVisible(isStageUseCourseParam);
        lblMissCount.setVisible(isStageUseCourseParam);
        spnMissCount.setVisible(isStageUseCourseParam);
        lblLastPlayTime.setVisible(isStageUseCourseParam);
        spnLastPlayTime.setVisible(isStageUseCourseParam);

        // Toggle headline visibility
        lblHeadCollectibles.setVisible(hasHeadCollectibles);
        sep2.setVisible(hasHeadCollectibles);
        lblHeadMisc.setVisible(hasHeadMisc);
        sep3.setVisible(hasHeadMisc);
    }
    
    private void localizeBadgeConditions() {
        Localization localization = Localization.getLocalization();
        StageNode node = editor.selectedNode;
        
        if (node != null) {
            String stageName = node.getStageName();
            chkBadgeCondition0.setText(localization.getText(String.format("stage.badgeCondition0.%s", stageName)));
        }
    }
    
    /**
     * Updates the localized text strings for display components.
     */
    void localizeAll() {
        blockInput = true;
        Localization localization = Localization.getLocalization();
        
            // Page flags
        lblHeadFlags.setText(localization.getText("courseinfowidget.lblHeadFlags.text"));
        chkOpen.setText(localization.getText("courseinfowidget.chkOpen.text"));
        chkClear.setText(localization.getText("courseinfowidget.chkClear.text"));
        chkLock.setText(localization.getText("courseinfowidget.chkLock.text"));
        chkNew.setText(localization.getText("courseinfowidget.chkNew.text"));
        chkAssistClear.setText(localization.getText("courseinfowidget.chkAssistClear.text"));
        chkAcquireComplete.setText(localization.getText("courseinfowidget.chkAcquireComplete.text"));
        
        // Collectibles & tasks
        lblHeadCollectibles.setText(localization.getText("courseinfowidget.lblHeadCollectibles.text"));
        localizeBadgeConditions();
        chkClearHideAndSeek.setText(localization.getText("courseinfowidget.chkClearHideAndSeek.text"));
        
        // Miscellaneous
        lblHeadMisc.setText(localization.getText("courseinfowidget.lblHeadMisc.text"));
        lblManeckFinalFloorCount.setText(localization.getText("courseinfowidget.lblManeckFinalFloorCount.text"));
        lblMoleMazeFinalFloorCount.setText(localization.getText("courseinfowidget.lblMoleMazeFinalFloorCount.text"));
        lblBestCoin.setText(localization.getText("courseinfowidget.lblBestCoin.text"));
        lblBestTime.setText(localization.getText("courseinfowidget.lblBestTime.text"));
        lblMissCount.setText(localization.getText("courseinfowidget.lblMissCount.text"));
        lblLastPlayTime.setText(localization.getText("courseinfowidget.lblLastPlayTime.text"));
        
        blockInput = false;
    }

    /** 
     * This method is called from within the constructor to initialize the form. WARNING: Do NOT modify this code. The content
     * of this method is always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {
        java.awt.GridBagConstraints gridBagConstraints;

        lblPreviewImage = new javax.swing.JLabel();
        sep1 = new javax.swing.JSeparator();
        lblHeadFlags = new javax.swing.JLabel();
        chkOpen = new javax.swing.JCheckBox();
        chkClear = new javax.swing.JCheckBox();
        chkAcquireComplete = new javax.swing.JCheckBox();
        chkNew = new javax.swing.JCheckBox();
        chkLock = new javax.swing.JCheckBox();
        chkAssistClear = new javax.swing.JCheckBox();
        sep2 = new javax.swing.JSeparator();
        lblHeadCollectibles = new javax.swing.JLabel();
        pnlCollectItem = new javax.swing.JPanel();
        chkCollectItem1 = new javax.swing.JCheckBox();
        chkCollectItem2 = new javax.swing.JCheckBox();
        chkCollectItem3 = new javax.swing.JCheckBox();
        chkBadgeCondition0 = new javax.swing.JCheckBox();
        chkClearHideAndSeek = new javax.swing.JCheckBox();
        sep3 = new javax.swing.JSeparator();
        lblHeadMisc = new javax.swing.JLabel();
        lblManeckFinalFloorCount = new javax.swing.JLabel();
        lblMoleMazeFinalFloorCount = new javax.swing.JLabel();
        lblBestCoin = new javax.swing.JLabel();
        lblBestTime = new javax.swing.JLabel();
        lblMissCount = new javax.swing.JLabel();
        lblLastPlayTime = new javax.swing.JLabel();
        spnManeckFinalFloorCount = new javax.swing.JSpinner();
        spnMoleMazeFinalFloorCount = new javax.swing.JSpinner();
        spnBestCoin = new javax.swing.JSpinner();
        pnlBestTime = new javax.swing.JPanel();
        spnBestTime = new javax.swing.JSpinner();
        lblBestTimeThreshold = new javax.swing.JLabel();
        spnMissCount = new javax.swing.JSpinner();
        spnLastPlayTime = new javax.swing.JSpinner();
        filler1 = new javax.swing.Box.Filler(new java.awt.Dimension(0, 0), new java.awt.Dimension(0, 0), new java.awt.Dimension(0, 32767));

        setLayout(new java.awt.GridBagLayout());

        lblPreviewImage.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lblPreviewImage.setMaximumSize(new java.awt.Dimension(181, 152));
        lblPreviewImage.setMinimumSize(new java.awt.Dimension(181, 152));
        lblPreviewImage.setPreferredSize(new java.awt.Dimension(181, 152));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.VERTICAL;
        gridBagConstraints.insets = new java.awt.Insets(2, 2, 2, 2);
        add(lblPreviewImage, gridBagConstraints);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(2, 2, 2, 2);
        add(sep1, gridBagConstraints);

        lblHeadFlags.setFont(new java.awt.Font("Dialog", 1, 12)); // NOI18N
        lblHeadFlags.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lblHeadFlags.setText("Page flags");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(2, 2, 2, 2);
        add(lblHeadFlags, gridBagConstraints);

        chkOpen.setText("Open");
        chkOpen.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                chkOpenActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(2, 2, 2, 2);
        add(chkOpen, gridBagConstraints);

        chkClear.setText("Clear");
        chkClear.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                chkClearActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(2, 2, 2, 2);
        add(chkClear, gridBagConstraints);

        chkAcquireComplete.setText("AcquireComplete");
        chkAcquireComplete.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                chkAcquireCompleteActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(2, 2, 2, 2);
        add(chkAcquireComplete, gridBagConstraints);

        chkNew.setText("New");
        chkNew.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                chkNewActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(2, 2, 2, 2);
        add(chkNew, gridBagConstraints);

        chkLock.setText("Lock");
        chkLock.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                chkLockActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(2, 2, 2, 2);
        add(chkLock, gridBagConstraints);

        chkAssistClear.setText("AssistClear");
        chkAssistClear.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                chkAssistClearActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(2, 2, 2, 2);
        add(chkAssistClear, gridBagConstraints);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(2, 2, 2, 2);
        add(sep2, gridBagConstraints);

        lblHeadCollectibles.setFont(new java.awt.Font("Dialog", 1, 12)); // NOI18N
        lblHeadCollectibles.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lblHeadCollectibles.setText("Collectibles & tasks");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(2, 2, 2, 2);
        add(lblHeadCollectibles, gridBagConstraints);

        pnlCollectItem.setLayout(new java.awt.GridBagLayout());

        chkCollectItem1.setIcon(ICONS_COLLECT_ITEMS[0]);
        chkCollectItem1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                chkCollectItem1ActionPerformed(evt);
            }
        });
        pnlCollectItem.add(chkCollectItem1, new java.awt.GridBagConstraints());

        chkCollectItem2.setIcon(ICONS_COLLECT_ITEMS[0]);
        chkCollectItem2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                chkCollectItem2ActionPerformed(evt);
            }
        });
        pnlCollectItem.add(chkCollectItem2, new java.awt.GridBagConstraints());

        chkCollectItem3.setIcon(ICONS_COLLECT_ITEMS[0]);
        chkCollectItem3.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                chkCollectItem3ActionPerformed(evt);
            }
        });
        pnlCollectItem.add(chkCollectItem3, new java.awt.GridBagConstraints());

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(2, 2, 2, 2);
        add(pnlCollectItem, gridBagConstraints);

        chkBadgeCondition0.setText("BadgeCondition0");
        chkBadgeCondition0.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                chkBadgeCondition0ActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(2, 2, 2, 2);
        add(chkBadgeCondition0, gridBagConstraints);

        chkClearHideAndSeek.setText("ClearHideAndSeek");
        chkClearHideAndSeek.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                chkClearHideAndSeekActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(2, 2, 2, 2);
        add(chkClearHideAndSeek, gridBagConstraints);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(2, 2, 2, 2);
        add(sep3, gridBagConstraints);

        lblHeadMisc.setFont(new java.awt.Font("Dialog", 1, 12)); // NOI18N
        lblHeadMisc.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lblHeadMisc.setText("Miscellaneous");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(2, 2, 2, 2);
        add(lblHeadMisc, gridBagConstraints);

        lblManeckFinalFloorCount.setText("ManeckFinalFloorCount");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(2, 2, 2, 2);
        add(lblManeckFinalFloorCount, gridBagConstraints);

        lblMoleMazeFinalFloorCount.setText("MoleMazeFinalFloorCount");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(2, 2, 2, 2);
        add(lblMoleMazeFinalFloorCount, gridBagConstraints);

        lblBestCoin.setText("BestCoin");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.weightx = 0.5;
        gridBagConstraints.insets = new java.awt.Insets(2, 2, 2, 2);
        add(lblBestCoin, gridBagConstraints);

        lblBestTime.setText("BestTime");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.weightx = 0.5;
        gridBagConstraints.insets = new java.awt.Insets(2, 2, 2, 2);
        add(lblBestTime, gridBagConstraints);

        lblMissCount.setText("MissCount");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.weightx = 0.5;
        gridBagConstraints.insets = new java.awt.Insets(2, 2, 2, 2);
        add(lblMissCount, gridBagConstraints);

        lblLastPlayTime.setText("LastPlayTime");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.weightx = 0.5;
        gridBagConstraints.insets = new java.awt.Insets(2, 2, 2, 2);
        add(lblLastPlayTime, gridBagConstraints);

        spnManeckFinalFloorCount.setModel(new javax.swing.SpinnerNumberModel(0, 0, 50, 1));
        spnManeckFinalFloorCount.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                spnManeckFinalFloorCountStateChanged(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(2, 2, 2, 2);
        add(spnManeckFinalFloorCount, gridBagConstraints);

        spnMoleMazeFinalFloorCount.setModel(new javax.swing.SpinnerNumberModel(0, 0, 30, 1));
        spnMoleMazeFinalFloorCount.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                spnMoleMazeFinalFloorCountStateChanged(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(2, 2, 2, 2);
        add(spnMoleMazeFinalFloorCount, gridBagConstraints);

        spnBestCoin.setModel(new javax.swing.SpinnerNumberModel(0, 0, 9999, 1));
        spnBestCoin.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                spnBestCoinStateChanged(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.weightx = 0.5;
        gridBagConstraints.insets = new java.awt.Insets(2, 2, 2, 2);
        add(spnBestCoin, gridBagConstraints);

        pnlBestTime.setLayout(new java.awt.GridBagLayout());

        spnBestTime.setModel(new javax.swing.SpinnerNumberModel(0, -1, null, 1));
        spnBestTime.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                spnBestTimeStateChanged(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        pnlBestTime.add(spnBestTime, gridBagConstraints);

        lblBestTimeThreshold.setText(" / 000");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        pnlBestTime.add(lblBestTimeThreshold, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.weightx = 0.5;
        gridBagConstraints.insets = new java.awt.Insets(2, 2, 2, 2);
        add(pnlBestTime, gridBagConstraints);

        spnMissCount.setModel(new javax.swing.SpinnerNumberModel(0, 0, null, 1));
        spnMissCount.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                spnMissCountStateChanged(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.weightx = 0.5;
        gridBagConstraints.insets = new java.awt.Insets(2, 2, 2, 2);
        add(spnMissCount, gridBagConstraints);

        spnLastPlayTime.setModel(new javax.swing.SpinnerDateModel(new java.util.Date(1641235909171L), null, null, java.util.Calendar.DAY_OF_MONTH));
        spnLastPlayTime.setEditor(new javax.swing.JSpinner.DateEditor(spnLastPlayTime, "dd.MM.yyyy HH:mm:ss"));
        spnLastPlayTime.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                spnLastPlayTimeStateChanged(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.weightx = 0.5;
        gridBagConstraints.insets = new java.awt.Insets(2, 2, 2, 2);
        add(spnLastPlayTime, gridBagConstraints);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        add(filler1, gridBagConstraints);
    }// </editor-fold>//GEN-END:initComponents

    private void chkCollectItem1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_chkCollectItem1ActionPerformed
        if (blockInput)
            return;
        
        courseInfo.collectItemFlags = BitUtil.update(courseInfo.collectItemFlags, 0, chkCollectItem1.isSelected());
        editor.needSaveChanges();
    }//GEN-LAST:event_chkCollectItem1ActionPerformed

    private void chkCollectItem2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_chkCollectItem2ActionPerformed
        if (blockInput)
            return;
        
        courseInfo.collectItemFlags = BitUtil.update(courseInfo.collectItemFlags, 1, chkCollectItem2.isSelected());
        editor.needSaveChanges();
    }//GEN-LAST:event_chkCollectItem2ActionPerformed

    private void chkCollectItem3ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_chkCollectItem3ActionPerformed
        if (blockInput)
            return;
        
        courseInfo.collectItemFlags = BitUtil.update(courseInfo.collectItemFlags, 2, chkCollectItem3.isSelected());
        editor.needSaveChanges();
    }//GEN-LAST:event_chkCollectItem3ActionPerformed

    private void chkBadgeCondition0ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_chkBadgeCondition0ActionPerformed
        if (blockInput)
            return;
        
        courseInfo.flags = BitUtil.updateWithMask(courseInfo.flags, CourseInfo.FLAG_BADGE_CONDITION_0, chkBadgeCondition0.isSelected());
        editor.needSaveChanges();
    }//GEN-LAST:event_chkBadgeCondition0ActionPerformed

    private void chkClearHideAndSeekActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_chkClearHideAndSeekActionPerformed
        if (blockInput)
            return;
        
        courseInfo.flags = BitUtil.updateWithMask(courseInfo.flags, CourseInfo.FLAG_CLEAR_HIDE_AND_SEEK, chkClearHideAndSeek.isSelected());
        editor.needSaveChanges();
    }//GEN-LAST:event_chkClearHideAndSeekActionPerformed

    private void spnBestCoinStateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_spnBestCoinStateChanged
        if (blockInput)
            return;
        
        courseInfo.bestCoin = (int)spnBestCoin.getValue();
        editor.needSaveChanges();
    }//GEN-LAST:event_spnBestCoinStateChanged

    private void spnBestTimeStateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_spnBestTimeStateChanged
        if (blockInput)
            return;
        
        courseInfo.bestTime = (int)spnBestTime.getValue();
        editor.needSaveChanges();
    }//GEN-LAST:event_spnBestTimeStateChanged

    private void spnMissCountStateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_spnMissCountStateChanged
        if (blockInput)
            return;
        
        courseInfo.missCount = (int)spnMissCount.getValue();
        editor.needSaveChanges();
    }//GEN-LAST:event_spnMissCountStateChanged

    private void spnLastPlayTimeStateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_spnLastPlayTimeStateChanged
        if (blockInput)
            return;
        
        courseInfo.lastPlayTime = ((Date)spnLastPlayTime.getValue()).getTime() / 1000;
        editor.needSaveChanges();
    }//GEN-LAST:event_spnLastPlayTimeStateChanged

    private void spnManeckFinalFloorCountStateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_spnManeckFinalFloorCountStateChanged
        if (blockInput)
            return;
        
        courseInfo.saveData.getGameData().maneckFinalFloorCount = (int)spnManeckFinalFloorCount.getValue();
        editor.needSaveChanges();
    }//GEN-LAST:event_spnManeckFinalFloorCountStateChanged

    private void spnMoleMazeFinalFloorCountStateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_spnMoleMazeFinalFloorCountStateChanged
        if (blockInput)
            return;
        
        courseInfo.saveData.getGameData().moleMazeFinalFloorCount = (int)spnMoleMazeFinalFloorCount.getValue();
        editor.needSaveChanges();
    }//GEN-LAST:event_spnMoleMazeFinalFloorCountStateChanged

    private void chkOpenActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_chkOpenActionPerformed
        if (blockInput)
            return;
        
        courseInfo.flags = BitUtil.updateWithMask(courseInfo.flags, CourseInfo.FLAG_OPEN, chkOpen.isSelected());
        editor.needSaveChanges();
    }//GEN-LAST:event_chkOpenActionPerformed

    private void chkClearActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_chkClearActionPerformed
        if (blockInput)
            return;
        
        courseInfo.flags = BitUtil.updateWithMask(courseInfo.flags, CourseInfo.FLAG_CLEAR, chkClear.isSelected());
        editor.needSaveChanges();
    }//GEN-LAST:event_chkClearActionPerformed

    private void chkAcquireCompleteActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_chkAcquireCompleteActionPerformed
        if (blockInput)
            return;
        
        courseInfo.flags = BitUtil.updateWithMask(courseInfo.flags, CourseInfo.FLAG_ACQUIRE_COMPLETE, chkAcquireComplete.isSelected());
        editor.needSaveChanges();
    }//GEN-LAST:event_chkAcquireCompleteActionPerformed

    private void chkNewActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_chkNewActionPerformed
        if (blockInput)
            return;
        
        courseInfo.flags = BitUtil.updateWithMask(courseInfo.flags, CourseInfo.FLAG_NEW, chkNew.isSelected());
        editor.needSaveChanges();
    }//GEN-LAST:event_chkNewActionPerformed

    private void chkLockActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_chkLockActionPerformed
        if (blockInput)
            return;
        
        courseInfo.flags = BitUtil.updateWithMask(courseInfo.flags, CourseInfo.FLAG_LOCK, chkLock.isSelected());
        editor.needSaveChanges();
    }//GEN-LAST:event_chkLockActionPerformed

    private void chkAssistClearActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_chkAssistClearActionPerformed
        if (blockInput)
            return;
        
        courseInfo.flags = BitUtil.updateWithMask(courseInfo.flags, CourseInfo.FLAG_CLEAR, chkAssistClear.isSelected());
        editor.needSaveChanges();
    }//GEN-LAST:event_chkAssistClearActionPerformed

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JCheckBox chkAcquireComplete;
    private javax.swing.JCheckBox chkAssistClear;
    private javax.swing.JCheckBox chkBadgeCondition0;
    private javax.swing.JCheckBox chkClear;
    private javax.swing.JCheckBox chkClearHideAndSeek;
    private javax.swing.JCheckBox chkCollectItem1;
    private javax.swing.JCheckBox chkCollectItem2;
    private javax.swing.JCheckBox chkCollectItem3;
    private javax.swing.JCheckBox chkLock;
    private javax.swing.JCheckBox chkNew;
    private javax.swing.JCheckBox chkOpen;
    private javax.swing.Box.Filler filler1;
    private javax.swing.JLabel lblBestCoin;
    private javax.swing.JLabel lblBestTime;
    private javax.swing.JLabel lblBestTimeThreshold;
    private javax.swing.JLabel lblHeadCollectibles;
    private javax.swing.JLabel lblHeadFlags;
    private javax.swing.JLabel lblHeadMisc;
    private javax.swing.JLabel lblLastPlayTime;
    private javax.swing.JLabel lblManeckFinalFloorCount;
    private javax.swing.JLabel lblMissCount;
    private javax.swing.JLabel lblMoleMazeFinalFloorCount;
    private javax.swing.JLabel lblPreviewImage;
    private javax.swing.JPanel pnlBestTime;
    private javax.swing.JPanel pnlCollectItem;
    private javax.swing.JSeparator sep1;
    private javax.swing.JSeparator sep2;
    private javax.swing.JSeparator sep3;
    private javax.swing.JSpinner spnBestCoin;
    private javax.swing.JSpinner spnBestTime;
    private javax.swing.JSpinner spnLastPlayTime;
    private javax.swing.JSpinner spnManeckFinalFloorCount;
    private javax.swing.JSpinner spnMissCount;
    private javax.swing.JSpinner spnMoleMazeFinalFloorCount;
    // End of variables declaration//GEN-END:variables

}
