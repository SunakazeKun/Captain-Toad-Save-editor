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

import com.aurumsmods.ajul.BitUtil;
import com.aurumsmods.ctse.LocalizeString;
import com.aurumsmods.ctse.Localization;
import com.aurumsmods.ctse.format.GameData;
import java.util.Date;
import javax.swing.JCheckBox;
import javax.swing.JSpinner.DateEditor;

/**
 * An editor widget for editing GameData information.
 * @author Aurum
 */
class GameDataWidget extends javax.swing.JPanel {
    private final KinopioSaveEditor editor; // parent editor context
    GameData gameData;                      // currently edited GameData
    boolean blockInput;                     // blocks user input when necessary

    /**
     * Constructs a new GameDataWidget using the specified editor context.
     * @param context the save editor context.
     */
    GameDataWidget(KinopioSaveEditor editor) {
        initComponents();
        
        this.editor = editor;
        gameData = null;
        blockInput = true;
        
        cmoCurrentSeasonId.addItem(new LocalizeString("stage.name.Season1"));
        cmoCurrentSeasonId.addItem(new LocalizeString("stage.name.Season2"));
        cmoCurrentSeasonId.addItem(new LocalizeString("stage.name.Season3"));
        cmoCurrentSeasonId.addItem(new LocalizeString("stage.name.Season4"));
        cmoCurrentSeasonId.addItem(new LocalizeString("stage.name.Season5"));
        cmoCurrentSeasonId.addItem(new LocalizeString("stage.name.Season6"));
    }
    
    /**
     * Reloads all information from the GameData. This should be invoked by the save editor after loading a save file.
     */
    void reloadData() {
        blockInput = true;
        gameData = editor.saveData.getGameData();
        
        if (gameData != null) {
            spnLastPlayTime.setValue(new Date(gameData.lastPlayTime * 1000));
            spnLastUptime.setValue(gameData.lastUptime);
            spnPlayerLife.setValue(gameData.playerLife);
            cmoCurrentSeasonId.setSelectedIndex(gameData.currentSeasonId - 1);
            chkExist3DWorldSaveData.setSelected(gameData.exist3DWorldSaveData);
            chkUnlockOdysseyLevels.setSelected(gameData.unlockOdysseyLevels);
            chkMiiverseSetting.setSelected(gameData.miiverseSetting);
            
            chkCameraReverseVertical.setSelected(checkControlSetting(GameData.CONTROL_CAMERA_REVERSE_VERTICAL));
            chkCameraReverseHorizontal.setSelected(checkControlSetting(GameData.CONTROL_CAMERA_REVERSE_HORIZONTAL));
            chkCameraGyro.setSelected(checkControlSetting(GameData.CONTROL_CAMERA_GYRO));
            chkTruckCameraGyro.setSelected(checkControlSetting(GameData.CONTROL_TRUCK_CAMERA_GYRO));
            chkTruckCameraReverseVertical.setSelected(checkControlSetting(GameData.CONTROL_TRUCK_CAMERA_REVERSE_VERTICAL));
            
            chkOpenSeason2.setSelected(checkFlag(GameData.FLAG_OPEN_SEASON_2));
            chkOpenSeason3.setSelected(checkFlag(GameData.FLAG_OPEN_SEASON_3));
            chkOpenSeasonSp.setSelected(checkFlag(GameData.FLAG_OPEN_SEASON_SP));
            chkOpenSeasonSpBonus.setSelected(checkFlag(GameData.FLAG_OPEN_SEASON_SP_BONUS));
            chkShowInfoManual.setSelected(checkFlag(GameData.FLAG_SHOW_INFO_MANUAL));
            chkShowPlayOtherCourse.setSelected(checkFlag(GameData.FLAG_SHOW_PLAY_OTHER_COURSE));
            chkShowTouchCoursePage.setSelected(checkFlag(GameData.FLAG_SHOW_TOUCH_COURSE_PAGE));
            chkShowEnding.setSelected(checkFlag(GameData.FLAG_SHOW_ENDING));
            chkShowTutorial1.setSelected(checkFlag(GameData.FLAG_SHOW_TUTORIAL_1));
            chkShowTutorial2.setSelected(checkFlag(GameData.FLAG_SHOW_TUTORIAL_2));
            chkShowLightOnOff.setSelected(checkFlag(GameData.FLAG_SHOW_LIGHT_ON_OFF));
            chkShowZoom.setSelected(checkFlag(GameData.FLAG_SHOW_ZOOM));
            chkShowReturnTitle.setSelected(checkFlag(GameData.FLAG_SHOW_RETURN_TITLE));
            chkOpenManeckForever.setSelected(checkFlag(GameData.FLAG_OPEN_MANECK_FOREVER));
            chkShow3DWorldSaveData.setSelected(checkFlag(GameData.FLAG_SHOW_3D_WORLD_SAVE_DATA));
            chkShowHideAndSeekTutorial.setSelected(checkFlag(GameData.FLAG_SHOW_HIDE_AND_SEEK_TUTORIAL));
            chkOpenHideAndSeek.setSelected(checkFlag(GameData.FLAG_OPEN_HIDE_AND_SEEK));
            chkOpenPixelToad.setSelected(checkFlag(GameData.FLAG_OPEN_PIXEL_TOAD));
            chkShowTouchControls.setSelected(checkFlag(GameData.FLAG_SHOW_TOUCH_CONTROLS));
            chkShowGyroControls.setSelected(checkFlag(GameData.FLAG_SHOW_GYRO_CONTROLS));
            chkPageTurnAfterSeason1Prologue.setSelected(checkFlag(GameData.FLAG_PAGE_TURN_AFTER_SEASON_1_PROLOGUE));
            chkOpenSeasonSp2.setSelected(checkFlag(GameData.FLAG_OPEN_SEASON_SP_2));
            chkShow2PWelcome.setSelected(checkFlag(GameData.FLAG_SHOW_2P_WELCOME));
            chkShowAOCWelcome1.setSelected(checkFlag(GameData.FLAG_SHOW_AOC_WELCOME_1));
            chkShowAOCWelcome2.setSelected(checkFlag(GameData.FLAG_SHOW_AOC_WELCOME_2));
            chkShowGhostTutorial.setSelected(checkFlag(GameData.FLAG_SHOW_GHOST_TUTORIAL));
            chkShowTouchReminder.setSelected(checkFlag(GameData.FLAG_SHOW_TOUCH_REMINDER));
            chkShowSpecial2Credits.setSelected(checkFlag(GameData.FLAG_SHOW_SPECIAL_2_CREDITS));
            chkShowVRWelcome.setSelected(checkFlag(GameData.FLAG_SHOW_VR_WELCOME));
            
            blockInput = false;
        }
    }
    
    /**
     * Updates the localized text strings for display components.
     */
    void localizeAll() {
        blockInput = true;
        Localization localization = Localization.getLocalization();
        
        lblHeadGeneral.setText(localization.getText("gamedatawidget.lblHeadGeneral.text"));
        lblHeadGameFlag.setText(localization.getText("gamedatawidget.lblHeadGameFlag.text"));
        lblHeadControlSetting.setText(localization.getText("gamedatawidget.lblHeadControlSetting.text"));
        lblHeadMessageFlag.setText(localization.getText("gamedatawidget.lblHeadMessageFlag.text"));
        
        lblLastPlayTime.setText(localization.getText("gamedatawidget.lblLastPlayTime.text"));
        lblLastUptime.setText(localization.getText("gamedatawidget.lblLastUptime.text"));
        lblPlayerLife.setText(localization.getText("gamedatawidget.lblPlayerLife.text"));
        lblCurrentSeasonId.setText(localization.getText("gamedatawidget.lblCurrentSeasonId.text"));
        chkExist3DWorldSaveData.setText(localization.getText("gamedatawidget.chkExist3DWorldSaveData.text"));
        chkUnlockOdysseyLevels.setText(localization.getText("gamedatawidget.chkUnlockOdysseyLevels.text"));
        chkMiiverseSetting.setText(localization.getText("gamedatawidget.chkMiiverseSetting.text"));
        
        chkCameraReverseVertical.setText(localization.getText("gamedatawidget.chkCameraReverseVertical.text"));
        chkCameraReverseHorizontal.setText(localization.getText("gamedatawidget.chkCameraReverseHorizontal.text"));
        chkCameraGyro.setText(localization.getText("gamedatawidget.chkCameraGyro.text"));
        chkTruckCameraGyro.setText(localization.getText("gamedatawidget.chkTruckCameraGyro.text"));
        chkTruckCameraReverseVertical.setText(localization.getText("gamedatawidget.chkTruckCameraReverseVertical.text"));
        
        chkOpenSeason2.setText(localization.getText("gamedatawidget.chkOpenSeason2.text"));
        chkOpenSeason3.setText(localization.getText("gamedatawidget.chkOpenSeason3.text"));
        chkOpenSeasonSp.setText(localization.getText("gamedatawidget.chkOpenSeasonSp.text"));
        chkOpenSeasonSpBonus.setText(localization.getText("gamedatawidget.chkOpenSeasonSpBonus.text"));
        chkShowInfoManual.setText(localization.getText("gamedatawidget.chkShowInfoManual.text"));
        chkShowPlayOtherCourse.setText(localization.getText("gamedatawidget.chkShowPlayOtherCourse.text"));
        chkShowTouchCoursePage.setText(localization.getText("gamedatawidget.chkShowTouchCoursePage.text"));
        chkShowEnding.setText(localization.getText("gamedatawidget.chkShowEnding.text"));
        chkShowTutorial1.setText(localization.getText("gamedatawidget.chkShowTutorial1.text"));
        chkShowTutorial2.setText(localization.getText("gamedatawidget.chkShowTutorial2.text"));
        chkShowLightOnOff.setText(localization.getText("gamedatawidget.chkShowLightOnOff.text"));
        chkShowZoom.setText(localization.getText("gamedatawidget.chkShowZoom.text"));
        chkShowReturnTitle.setText(localization.getText("gamedatawidget.chkShowReturnTitle.text"));
        chkOpenManeckForever.setText(localization.getText("gamedatawidget.chkOpenManeckForever.text"));
        chkShow3DWorldSaveData.setText(localization.getText("gamedatawidget.chkShow3DWorldSaveData.text"));
        chkShowHideAndSeekTutorial.setText(localization.getText("gamedatawidget.chkShowHideAndSeekTutorial.text"));
        chkOpenHideAndSeek.setText(localization.getText("gamedatawidget.chkOpenHideAndSeek.text"));
        chkOpenPixelToad.setText(localization.getText("gamedatawidget.chkOpenPixelToad.text"));
        chkShowTouchControls.setText(localization.getText("gamedatawidget.chkShowTouchControls.text"));
        chkShowGyroControls.setText(localization.getText("gamedatawidget.chkShowGyroControls.text"));
        chkPageTurnAfterSeason1Prologue.setText(localization.getText("gamedatawidget.chkPageTurnAfterSeason1Prologue.text"));
        chkOpenSeasonSp2.setText(localization.getText("gamedatawidget.chkOpenSeasonSp2.text"));
        chkShow2PWelcome.setText(localization.getText("gamedatawidget.chkShow2PWelcome.text"));
        chkShowAOCWelcome1.setText(localization.getText("gamedatawidget.chkShowAOCWelcome1.text"));
        chkShowAOCWelcome2.setText(localization.getText("gamedatawidget.chkShowAOCWelcome2.text"));
        chkShowGhostTutorial.setText(localization.getText("gamedatawidget.chkShowGhostTutorial.text"));
        chkShowTouchReminder.setText(localization.getText("gamedatawidget.chkShowTouchReminder.text"));
        chkShowSpecial2Credits.setText(localization.getText("gamedatawidget.chkShowSpecial2Credits.text"));
        chkShowVRWelcome.setText(localization.getText("gamedatawidget.chkShowVRWelcome.text"));
        
        blockInput = false;
    }
    
    private boolean checkControlSetting(int mask) {
        return (gameData.controlSetting & mask) != 0;
    }
    
    private boolean checkFlag(int mask) {
        return (gameData.flags & mask) != 0;
    }
    
    private void updateControlSetting(JCheckBox src, int mask) {
        if (blockInput)
            return;
        gameData.controlSetting = BitUtil.updateWithMask(gameData.controlSetting, mask, src.isSelected());
        editor.needSaveChanges();
    }
    
    private void updateFlag(JCheckBox src, int mask) {
        if (blockInput)
            return;
        gameData.flags = BitUtil.updateWithMask(gameData.flags, mask, src.isSelected());
        editor.needSaveChanges();
    }
    
    /**
     * This method is called from within the constructor to initialize the form. WARNING: Do NOT modify this code. The content
     * of this method is always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {
        java.awt.GridBagConstraints gridBagConstraints;

        lblHeadGeneral = new javax.swing.JLabel();
        lblLastPlayTime = new javax.swing.JLabel();
        lblLastUptime = new javax.swing.JLabel();
        lblPlayerLife = new javax.swing.JLabel();
        lblCurrentSeasonId = new javax.swing.JLabel();
        spnLastPlayTime = new javax.swing.JSpinner();
        spnLastUptime = new javax.swing.JSpinner();
        spnPlayerLife = new javax.swing.JSpinner();
        cmoCurrentSeasonId = new javax.swing.JComboBox<>();
        chkExist3DWorldSaveData = new javax.swing.JCheckBox();
        chkUnlockOdysseyLevels = new javax.swing.JCheckBox();
        chkMiiverseSetting = new javax.swing.JCheckBox();
        sep1 = new javax.swing.JSeparator();
        lblHeadGameFlag = new javax.swing.JLabel();
        chkOpenSeason2 = new javax.swing.JCheckBox();
        chkOpenSeason3 = new javax.swing.JCheckBox();
        chkOpenSeasonSp = new javax.swing.JCheckBox();
        chkOpenSeasonSp2 = new javax.swing.JCheckBox();
        chkShowEnding = new javax.swing.JCheckBox();
        chkShowSpecial2Credits = new javax.swing.JCheckBox();
        chkPageTurnAfterSeason1Prologue = new javax.swing.JCheckBox();
        chkOpenHideAndSeek = new javax.swing.JCheckBox();
        chkOpenPixelToad = new javax.swing.JCheckBox();
        chkOpenManeckForever = new javax.swing.JCheckBox();
        chkOpenSeasonSpBonus = new javax.swing.JCheckBox();
        sep2 = new javax.swing.JSeparator();
        lblHeadControlSetting = new javax.swing.JLabel();
        chkCameraReverseVertical = new javax.swing.JCheckBox();
        chkCameraReverseHorizontal = new javax.swing.JCheckBox();
        chkTruckCameraReverseVertical = new javax.swing.JCheckBox();
        chkCameraGyro = new javax.swing.JCheckBox();
        chkTruckCameraGyro = new javax.swing.JCheckBox();
        sep3 = new javax.swing.JSeparator();
        lblHeadMessageFlag = new javax.swing.JLabel();
        chkShow3DWorldSaveData = new javax.swing.JCheckBox();
        chkShowHideAndSeekTutorial = new javax.swing.JCheckBox();
        chkShow2PWelcome = new javax.swing.JCheckBox();
        chkShowAOCWelcome1 = new javax.swing.JCheckBox();
        chkShowAOCWelcome2 = new javax.swing.JCheckBox();
        chkShowVRWelcome = new javax.swing.JCheckBox();
        chkShowInfoManual = new javax.swing.JCheckBox();
        chkShowPlayOtherCourse = new javax.swing.JCheckBox();
        chkShowTouchCoursePage = new javax.swing.JCheckBox();
        chkShowTutorial1 = new javax.swing.JCheckBox();
        chkShowTutorial2 = new javax.swing.JCheckBox();
        chkShowLightOnOff = new javax.swing.JCheckBox();
        chkShowZoom = new javax.swing.JCheckBox();
        chkShowReturnTitle = new javax.swing.JCheckBox();
        chkShowTouchControls = new javax.swing.JCheckBox();
        chkShowGhostTutorial = new javax.swing.JCheckBox();
        chkShowTouchReminder = new javax.swing.JCheckBox();
        chkShowGyroControls = new javax.swing.JCheckBox();
        filler = new javax.swing.Box.Filler(new java.awt.Dimension(0, 0), new java.awt.Dimension(0, 0), new java.awt.Dimension(0, 32767));

        setLayout(new java.awt.GridBagLayout());

        lblHeadGeneral.setFont(new java.awt.Font("Dialog", 1, 12)); // NOI18N
        lblHeadGeneral.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lblHeadGeneral.setText("General data");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(2, 2, 2, 2);
        add(lblHeadGeneral, gridBagConstraints);

        lblLastPlayTime.setText("Last time played");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.weightx = 0.5;
        gridBagConstraints.insets = new java.awt.Insets(2, 2, 2, 2);
        add(lblLastPlayTime, gridBagConstraints);

        lblLastUptime.setText("Last uptime in frames");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.weightx = 0.5;
        gridBagConstraints.insets = new java.awt.Insets(2, 2, 2, 2);
        add(lblLastUptime, gridBagConstraints);

        lblPlayerLife.setText("Number of lives");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.weightx = 0.5;
        gridBagConstraints.insets = new java.awt.Insets(2, 2, 2, 2);
        add(lblPlayerLife, gridBagConstraints);

        lblCurrentSeasonId.setText("Selected book");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.weightx = 0.5;
        gridBagConstraints.insets = new java.awt.Insets(2, 2, 2, 2);
        add(lblCurrentSeasonId, gridBagConstraints);

        spnLastPlayTime.setModel(new javax.swing.SpinnerDateModel());
        spnLastPlayTime.setEditor(new DateEditor(spnLastPlayTime, "dd.MM.yyyy HH:mm:ss"));
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

        spnLastUptime.setModel(new javax.swing.SpinnerNumberModel(0L, 0L, null, 1L));
        spnLastUptime.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                spnLastUptimeStateChanged(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.weightx = 0.5;
        gridBagConstraints.insets = new java.awt.Insets(2, 2, 2, 2);
        add(spnLastUptime, gridBagConstraints);

        spnPlayerLife.setModel(new javax.swing.SpinnerNumberModel(0, 0, 99, 1));
        spnPlayerLife.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                spnPlayerLifeStateChanged(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.weightx = 0.5;
        gridBagConstraints.insets = new java.awt.Insets(2, 2, 2, 2);
        add(spnPlayerLife, gridBagConstraints);

        cmoCurrentSeasonId.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cmoCurrentSeasonIdActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.weightx = 0.5;
        gridBagConstraints.insets = new java.awt.Insets(2, 2, 2, 2);
        add(cmoCurrentSeasonId, gridBagConstraints);

        chkExist3DWorldSaveData.setText("Super Mario 3D World save exists?");
        chkExist3DWorldSaveData.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                chkExist3DWorldSaveDataActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(2, 2, 2, 2);
        add(chkExist3DWorldSaveData, gridBagConstraints);

        chkUnlockOdysseyLevels.setText("Scanned Super Mario Odyssey amiibo?");
        chkUnlockOdysseyLevels.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                chkUnlockOdysseyLevelsActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(2, 2, 2, 2);
        add(chkUnlockOdysseyLevels, gridBagConstraints);

        chkMiiverseSetting.setText("Miiverse enabled?");
        chkMiiverseSetting.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                chkMiiverseSettingActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(2, 2, 2, 2);
        add(chkMiiverseSetting, gridBagConstraints);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.insets = new java.awt.Insets(2, 2, 2, 2);
        add(sep1, gridBagConstraints);

        lblHeadGameFlag.setFont(new java.awt.Font("Dialog", 1, 12)); // NOI18N
        lblHeadGameFlag.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lblHeadGameFlag.setText("Game progression flags");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(2, 2, 2, 2);
        add(lblHeadGameFlag, gridBagConstraints);

        chkOpenSeason2.setText("OpenSeason2");
        chkOpenSeason2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                chkOpenSeason2ActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(2, 2, 2, 2);
        add(chkOpenSeason2, gridBagConstraints);

        chkOpenSeason3.setText("OpenSeason3");
        chkOpenSeason3.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                chkOpenSeason3ActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(2, 2, 2, 2);
        add(chkOpenSeason3, gridBagConstraints);

        chkOpenSeasonSp.setText("OpenSeasonSp");
        chkOpenSeasonSp.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                chkOpenSeasonSpActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(2, 2, 2, 2);
        add(chkOpenSeasonSp, gridBagConstraints);

        chkOpenSeasonSp2.setText("OpenSeasonSp2");
        chkOpenSeasonSp2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                chkOpenSeasonSp2ActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(2, 2, 2, 2);
        add(chkOpenSeasonSp2, gridBagConstraints);

        chkShowEnding.setText("ShowEnding");
        chkShowEnding.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                chkShowEndingActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(2, 2, 2, 2);
        add(chkShowEnding, gridBagConstraints);

        chkShowSpecial2Credits.setText("ShowSpecial2Credits");
        chkShowSpecial2Credits.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                chkShowSpecial2CreditsActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(2, 2, 2, 2);
        add(chkShowSpecial2Credits, gridBagConstraints);

        chkPageTurnAfterSeason1Prologue.setText("PageTurnAfterSeason1Prologue");
        chkPageTurnAfterSeason1Prologue.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                chkPageTurnAfterSeason1PrologueActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(2, 2, 2, 2);
        add(chkPageTurnAfterSeason1Prologue, gridBagConstraints);

        chkOpenHideAndSeek.setText("OpenHideAndSeek");
        chkOpenHideAndSeek.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                chkOpenHideAndSeekActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(2, 2, 2, 2);
        add(chkOpenHideAndSeek, gridBagConstraints);

        chkOpenPixelToad.setText("OpenPixelToad");
        chkOpenPixelToad.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                chkOpenPixelToadActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(2, 2, 2, 2);
        add(chkOpenPixelToad, gridBagConstraints);

        chkOpenManeckForever.setText("OpenManeckForever");
        chkOpenManeckForever.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                chkOpenManeckForeverActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(2, 2, 2, 2);
        add(chkOpenManeckForever, gridBagConstraints);

        chkOpenSeasonSpBonus.setText("OpenSeasonSpBonus");
        chkOpenSeasonSpBonus.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                chkOpenSeasonSpBonusActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(2, 2, 2, 2);
        add(chkOpenSeasonSpBonus, gridBagConstraints);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.insets = new java.awt.Insets(2, 2, 2, 2);
        add(sep2, gridBagConstraints);

        lblHeadControlSetting.setFont(new java.awt.Font("Dialog", 1, 12)); // NOI18N
        lblHeadControlSetting.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lblHeadControlSetting.setText("Control settings");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(2, 2, 2, 2);
        add(lblHeadControlSetting, gridBagConstraints);

        chkCameraReverseVertical.setText("CameraReverseVertical");
        chkCameraReverseVertical.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                chkCameraReverseVerticalActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(2, 2, 2, 2);
        add(chkCameraReverseVertical, gridBagConstraints);

        chkCameraReverseHorizontal.setText("CameraReverseHorizontal");
        chkCameraReverseHorizontal.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                chkCameraReverseHorizontalActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(2, 2, 2, 2);
        add(chkCameraReverseHorizontal, gridBagConstraints);

        chkTruckCameraReverseVertical.setText("TruckCameraReverseVertical");
        chkTruckCameraReverseVertical.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                chkTruckCameraReverseVerticalActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(2, 2, 2, 2);
        add(chkTruckCameraReverseVertical, gridBagConstraints);

        chkCameraGyro.setText("CameraGyro");
        chkCameraGyro.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                chkCameraGyroActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(2, 2, 2, 2);
        add(chkCameraGyro, gridBagConstraints);

        chkTruckCameraGyro.setText("TruckCameraGyro");
        chkTruckCameraGyro.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                chkTruckCameraGyroActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(2, 2, 2, 2);
        add(chkTruckCameraGyro, gridBagConstraints);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.insets = new java.awt.Insets(2, 2, 2, 2);
        add(sep3, gridBagConstraints);

        lblHeadMessageFlag.setFont(new java.awt.Font("Dialog", 1, 12)); // NOI18N
        lblHeadMessageFlag.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lblHeadMessageFlag.setText("Information message flags");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(2, 2, 2, 2);
        add(lblHeadMessageFlag, gridBagConstraints);

        chkShow3DWorldSaveData.setText("Show3DWorldSaveData");
        chkShow3DWorldSaveData.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                chkShow3DWorldSaveDataActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(2, 2, 2, 2);
        add(chkShow3DWorldSaveData, gridBagConstraints);

        chkShowHideAndSeekTutorial.setText("ShowHideAndSeekTutorial");
        chkShowHideAndSeekTutorial.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                chkShowHideAndSeekTutorialActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(2, 2, 2, 2);
        add(chkShowHideAndSeekTutorial, gridBagConstraints);

        chkShow2PWelcome.setText("Show2PWelcome");
        chkShow2PWelcome.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                chkShow2PWelcomeActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(2, 2, 2, 2);
        add(chkShow2PWelcome, gridBagConstraints);

        chkShowAOCWelcome1.setText("ShowAOCWelcome1");
        chkShowAOCWelcome1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                chkShowAOCWelcome1ActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(2, 2, 2, 2);
        add(chkShowAOCWelcome1, gridBagConstraints);

        chkShowAOCWelcome2.setText("ShowAOCWelcome2");
        chkShowAOCWelcome2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                chkShowAOCWelcome2ActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(2, 2, 2, 2);
        add(chkShowAOCWelcome2, gridBagConstraints);

        chkShowVRWelcome.setText("ShowVRWelcome");
        chkShowVRWelcome.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                chkShowVRWelcomeActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(2, 2, 2, 2);
        add(chkShowVRWelcome, gridBagConstraints);

        chkShowInfoManual.setText("ShowInfoManual");
        chkShowInfoManual.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                chkShowInfoManualActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(2, 2, 2, 2);
        add(chkShowInfoManual, gridBagConstraints);

        chkShowPlayOtherCourse.setText("ShowPlayOtherCourse");
        chkShowPlayOtherCourse.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                chkShowPlayOtherCourseActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(2, 2, 2, 2);
        add(chkShowPlayOtherCourse, gridBagConstraints);

        chkShowTouchCoursePage.setText("ShowTouchCoursePage");
        chkShowTouchCoursePage.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                chkShowTouchCoursePageActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(2, 2, 2, 2);
        add(chkShowTouchCoursePage, gridBagConstraints);

        chkShowTutorial1.setText("ShowTutorial1");
        chkShowTutorial1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                chkShowTutorial1ActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(2, 2, 2, 2);
        add(chkShowTutorial1, gridBagConstraints);

        chkShowTutorial2.setText("ShowTutorial2");
        chkShowTutorial2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                chkShowTutorial2ActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(2, 2, 2, 2);
        add(chkShowTutorial2, gridBagConstraints);

        chkShowLightOnOff.setText("ShowLightOnOff");
        chkShowLightOnOff.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                chkShowLightOnOffActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(2, 2, 2, 2);
        add(chkShowLightOnOff, gridBagConstraints);

        chkShowZoom.setText("ShowZoom");
        chkShowZoom.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                chkShowZoomActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(2, 2, 2, 2);
        add(chkShowZoom, gridBagConstraints);

        chkShowReturnTitle.setText("ShowReturnTitle");
        chkShowReturnTitle.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                chkShowReturnTitleActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(2, 2, 2, 2);
        add(chkShowReturnTitle, gridBagConstraints);

        chkShowTouchControls.setText("ShowTouchControls");
        chkShowTouchControls.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                chkShowTouchControlsActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(2, 2, 2, 2);
        add(chkShowTouchControls, gridBagConstraints);

        chkShowGhostTutorial.setText("ShowGhostTutorial");
        chkShowGhostTutorial.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                chkShowGhostTutorialActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(2, 2, 2, 2);
        add(chkShowGhostTutorial, gridBagConstraints);

        chkShowTouchReminder.setText("ShowTouchReminder");
        chkShowTouchReminder.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                chkShowTouchReminderActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(2, 2, 2, 2);
        add(chkShowTouchReminder, gridBagConstraints);

        chkShowGyroControls.setText("ShowGyroControls");
        chkShowGyroControls.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                chkShowGyroControlsActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(2, 2, 2, 2);
        add(chkShowGyroControls, gridBagConstraints);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        add(filler, gridBagConstraints);
    }// </editor-fold>//GEN-END:initComponents

    private void spnLastPlayTimeStateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_spnLastPlayTimeStateChanged
        if (blockInput)
            return;
        
        gameData.lastPlayTime = ((Date)spnLastPlayTime.getValue()).getTime() / 1000;
        editor.needSaveChanges();
    }//GEN-LAST:event_spnLastPlayTimeStateChanged

    private void spnLastUptimeStateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_spnLastUptimeStateChanged
        if (blockInput)
            return;
        
        gameData.lastUptime = (long)spnLastUptime.getValue();
        editor.needSaveChanges();
    }//GEN-LAST:event_spnLastUptimeStateChanged

    private void spnPlayerLifeStateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_spnPlayerLifeStateChanged
        if (blockInput)
            return;
        
        gameData.playerLife = (int)spnPlayerLife.getValue();
        editor.needSaveChanges();
    }//GEN-LAST:event_spnPlayerLifeStateChanged

    private void cmoCurrentSeasonIdActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cmoCurrentSeasonIdActionPerformed
        if (blockInput)
            return;
        
        gameData.currentSeasonId = cmoCurrentSeasonId.getSelectedIndex() + 1;
        editor.needSaveChanges();
    }//GEN-LAST:event_cmoCurrentSeasonIdActionPerformed

    private void chkExist3DWorldSaveDataActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_chkExist3DWorldSaveDataActionPerformed
        if (blockInput)
            return;
        
        gameData.exist3DWorldSaveData = chkExist3DWorldSaveData.isSelected();
        editor.needSaveChanges();
    }//GEN-LAST:event_chkExist3DWorldSaveDataActionPerformed

    private void chkUnlockOdysseyLevelsActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_chkUnlockOdysseyLevelsActionPerformed
        if (blockInput)
            return;
        
        gameData.unlockOdysseyLevels = chkUnlockOdysseyLevels.isSelected();
        editor.needSaveChanges();
    }//GEN-LAST:event_chkUnlockOdysseyLevelsActionPerformed

    private void chkMiiverseSettingActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_chkMiiverseSettingActionPerformed
        if (blockInput)
            return;
        
        gameData.miiverseSetting = chkMiiverseSetting.isSelected();
        editor.needSaveChanges();
    }//GEN-LAST:event_chkMiiverseSettingActionPerformed

    private void chkCameraReverseVerticalActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_chkCameraReverseVerticalActionPerformed
        updateControlSetting((JCheckBox)evt.getSource(), GameData.CONTROL_CAMERA_REVERSE_VERTICAL);
    }//GEN-LAST:event_chkCameraReverseVerticalActionPerformed

    private void chkCameraReverseHorizontalActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_chkCameraReverseHorizontalActionPerformed
        updateControlSetting((JCheckBox)evt.getSource(), GameData.CONTROL_CAMERA_REVERSE_HORIZONTAL);
    }//GEN-LAST:event_chkCameraReverseHorizontalActionPerformed

    private void chkCameraGyroActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_chkCameraGyroActionPerformed
        updateControlSetting((JCheckBox)evt.getSource(), GameData.CONTROL_CAMERA_GYRO);
    }//GEN-LAST:event_chkCameraGyroActionPerformed

    private void chkTruckCameraGyroActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_chkTruckCameraGyroActionPerformed
        updateControlSetting((JCheckBox)evt.getSource(), GameData.CONTROL_TRUCK_CAMERA_GYRO);
    }//GEN-LAST:event_chkTruckCameraGyroActionPerformed

    private void chkTruckCameraReverseVerticalActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_chkTruckCameraReverseVerticalActionPerformed
        updateControlSetting((JCheckBox)evt.getSource(), GameData.CONTROL_TRUCK_CAMERA_REVERSE_VERTICAL);
    }//GEN-LAST:event_chkTruckCameraReverseVerticalActionPerformed

    private void chkOpenSeason2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_chkOpenSeason2ActionPerformed
        updateFlag((JCheckBox)evt.getSource(), GameData.FLAG_OPEN_SEASON_2);
    }//GEN-LAST:event_chkOpenSeason2ActionPerformed

    private void chkOpenSeason3ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_chkOpenSeason3ActionPerformed
        updateFlag((JCheckBox)evt.getSource(), GameData.FLAG_OPEN_SEASON_3);
    }//GEN-LAST:event_chkOpenSeason3ActionPerformed

    private void chkOpenSeasonSpActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_chkOpenSeasonSpActionPerformed
        updateFlag((JCheckBox)evt.getSource(), GameData.FLAG_OPEN_SEASON_SP);
    }//GEN-LAST:event_chkOpenSeasonSpActionPerformed

    private void chkOpenSeasonSpBonusActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_chkOpenSeasonSpBonusActionPerformed
        updateFlag((JCheckBox)evt.getSource(), GameData.FLAG_OPEN_SEASON_SP_BONUS);
    }//GEN-LAST:event_chkOpenSeasonSpBonusActionPerformed

    private void chkShowInfoManualActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_chkShowInfoManualActionPerformed
        updateFlag((JCheckBox)evt.getSource(), GameData.FLAG_SHOW_INFO_MANUAL);
    }//GEN-LAST:event_chkShowInfoManualActionPerformed

    private void chkShowPlayOtherCourseActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_chkShowPlayOtherCourseActionPerformed
        updateFlag((JCheckBox)evt.getSource(), GameData.FLAG_SHOW_PLAY_OTHER_COURSE);
    }//GEN-LAST:event_chkShowPlayOtherCourseActionPerformed

    private void chkShowTouchCoursePageActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_chkShowTouchCoursePageActionPerformed
        updateFlag((JCheckBox)evt.getSource(), GameData.FLAG_SHOW_TOUCH_COURSE_PAGE);
    }//GEN-LAST:event_chkShowTouchCoursePageActionPerformed

    private void chkShowEndingActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_chkShowEndingActionPerformed
        updateFlag((JCheckBox)evt.getSource(), GameData.FLAG_SHOW_ENDING);
    }//GEN-LAST:event_chkShowEndingActionPerformed

    private void chkShowTutorial1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_chkShowTutorial1ActionPerformed
        updateFlag((JCheckBox)evt.getSource(), GameData.FLAG_SHOW_TUTORIAL_1);
    }//GEN-LAST:event_chkShowTutorial1ActionPerformed

    private void chkShowTutorial2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_chkShowTutorial2ActionPerformed
        updateFlag((JCheckBox)evt.getSource(), GameData.FLAG_SHOW_TUTORIAL_2);
    }//GEN-LAST:event_chkShowTutorial2ActionPerformed

    private void chkShowLightOnOffActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_chkShowLightOnOffActionPerformed
        updateFlag((JCheckBox)evt.getSource(), GameData.FLAG_SHOW_LIGHT_ON_OFF);
    }//GEN-LAST:event_chkShowLightOnOffActionPerformed

    private void chkShowZoomActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_chkShowZoomActionPerformed
        updateFlag((JCheckBox)evt.getSource(), GameData.FLAG_SHOW_ZOOM);
    }//GEN-LAST:event_chkShowZoomActionPerformed

    private void chkShowReturnTitleActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_chkShowReturnTitleActionPerformed
        updateFlag((JCheckBox)evt.getSource(), GameData.FLAG_SHOW_RETURN_TITLE);
    }//GEN-LAST:event_chkShowReturnTitleActionPerformed

    private void chkOpenManeckForeverActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_chkOpenManeckForeverActionPerformed
        updateFlag((JCheckBox)evt.getSource(), GameData.FLAG_OPEN_MANECK_FOREVER);
    }//GEN-LAST:event_chkOpenManeckForeverActionPerformed

    private void chkShow3DWorldSaveDataActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_chkShow3DWorldSaveDataActionPerformed
        updateFlag((JCheckBox)evt.getSource(), GameData.FLAG_SHOW_3D_WORLD_SAVE_DATA);
    }//GEN-LAST:event_chkShow3DWorldSaveDataActionPerformed

    private void chkShowHideAndSeekTutorialActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_chkShowHideAndSeekTutorialActionPerformed
        updateFlag((JCheckBox)evt.getSource(), GameData.FLAG_SHOW_HIDE_AND_SEEK_TUTORIAL);
    }//GEN-LAST:event_chkShowHideAndSeekTutorialActionPerformed

    private void chkOpenHideAndSeekActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_chkOpenHideAndSeekActionPerformed
        updateFlag((JCheckBox)evt.getSource(), GameData.FLAG_OPEN_HIDE_AND_SEEK);
    }//GEN-LAST:event_chkOpenHideAndSeekActionPerformed

    private void chkOpenPixelToadActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_chkOpenPixelToadActionPerformed
        updateFlag((JCheckBox)evt.getSource(), GameData.FLAG_OPEN_PIXEL_TOAD);
    }//GEN-LAST:event_chkOpenPixelToadActionPerformed

    private void chkShowTouchControlsActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_chkShowTouchControlsActionPerformed
        updateFlag((JCheckBox)evt.getSource(), GameData.FLAG_SHOW_TOUCH_CONTROLS);
    }//GEN-LAST:event_chkShowTouchControlsActionPerformed

    private void chkShowGyroControlsActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_chkShowGyroControlsActionPerformed
        updateFlag((JCheckBox)evt.getSource(), GameData.FLAG_SHOW_GYRO_CONTROLS);
    }//GEN-LAST:event_chkShowGyroControlsActionPerformed

    private void chkPageTurnAfterSeason1PrologueActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_chkPageTurnAfterSeason1PrologueActionPerformed
        updateFlag((JCheckBox)evt.getSource(), GameData.FLAG_PAGE_TURN_AFTER_SEASON_1_PROLOGUE);
    }//GEN-LAST:event_chkPageTurnAfterSeason1PrologueActionPerformed

    private void chkOpenSeasonSp2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_chkOpenSeasonSp2ActionPerformed
        updateFlag((JCheckBox)evt.getSource(), GameData.FLAG_OPEN_SEASON_SP_2);
    }//GEN-LAST:event_chkOpenSeasonSp2ActionPerformed

    private void chkShow2PWelcomeActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_chkShow2PWelcomeActionPerformed
        updateFlag((JCheckBox)evt.getSource(), GameData.FLAG_SHOW_2P_WELCOME);
    }//GEN-LAST:event_chkShow2PWelcomeActionPerformed

    private void chkShowAOCWelcome1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_chkShowAOCWelcome1ActionPerformed
        updateFlag((JCheckBox)evt.getSource(), GameData.FLAG_SHOW_AOC_WELCOME_1);
    }//GEN-LAST:event_chkShowAOCWelcome1ActionPerformed

    private void chkShowAOCWelcome2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_chkShowAOCWelcome2ActionPerformed
        updateFlag((JCheckBox)evt.getSource(), GameData.FLAG_SHOW_AOC_WELCOME_2);
    }//GEN-LAST:event_chkShowAOCWelcome2ActionPerformed

    private void chkShowGhostTutorialActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_chkShowGhostTutorialActionPerformed
        updateFlag((JCheckBox)evt.getSource(), GameData.FLAG_SHOW_GHOST_TUTORIAL);
    }//GEN-LAST:event_chkShowGhostTutorialActionPerformed

    private void chkShowTouchReminderActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_chkShowTouchReminderActionPerformed
        updateFlag((JCheckBox)evt.getSource(), GameData.FLAG_SHOW_TOUCH_REMINDER);
    }//GEN-LAST:event_chkShowTouchReminderActionPerformed

    private void chkShowSpecial2CreditsActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_chkShowSpecial2CreditsActionPerformed
        updateFlag((JCheckBox)evt.getSource(), GameData.FLAG_SHOW_SPECIAL_2_CREDITS);
    }//GEN-LAST:event_chkShowSpecial2CreditsActionPerformed

    private void chkShowVRWelcomeActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_chkShowVRWelcomeActionPerformed
        updateFlag((JCheckBox)evt.getSource(), GameData.FLAG_SHOW_VR_WELCOME);
    }//GEN-LAST:event_chkShowVRWelcomeActionPerformed

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JCheckBox chkCameraGyro;
    private javax.swing.JCheckBox chkCameraReverseHorizontal;
    private javax.swing.JCheckBox chkCameraReverseVertical;
    private javax.swing.JCheckBox chkExist3DWorldSaveData;
    private javax.swing.JCheckBox chkMiiverseSetting;
    private javax.swing.JCheckBox chkOpenHideAndSeek;
    private javax.swing.JCheckBox chkOpenManeckForever;
    private javax.swing.JCheckBox chkOpenPixelToad;
    private javax.swing.JCheckBox chkOpenSeason2;
    private javax.swing.JCheckBox chkOpenSeason3;
    private javax.swing.JCheckBox chkOpenSeasonSp;
    private javax.swing.JCheckBox chkOpenSeasonSp2;
    private javax.swing.JCheckBox chkOpenSeasonSpBonus;
    private javax.swing.JCheckBox chkPageTurnAfterSeason1Prologue;
    private javax.swing.JCheckBox chkShow2PWelcome;
    private javax.swing.JCheckBox chkShow3DWorldSaveData;
    private javax.swing.JCheckBox chkShowAOCWelcome1;
    private javax.swing.JCheckBox chkShowAOCWelcome2;
    private javax.swing.JCheckBox chkShowEnding;
    private javax.swing.JCheckBox chkShowGhostTutorial;
    private javax.swing.JCheckBox chkShowGyroControls;
    private javax.swing.JCheckBox chkShowHideAndSeekTutorial;
    private javax.swing.JCheckBox chkShowInfoManual;
    private javax.swing.JCheckBox chkShowLightOnOff;
    private javax.swing.JCheckBox chkShowPlayOtherCourse;
    private javax.swing.JCheckBox chkShowReturnTitle;
    private javax.swing.JCheckBox chkShowSpecial2Credits;
    private javax.swing.JCheckBox chkShowTouchControls;
    private javax.swing.JCheckBox chkShowTouchCoursePage;
    private javax.swing.JCheckBox chkShowTouchReminder;
    private javax.swing.JCheckBox chkShowTutorial1;
    private javax.swing.JCheckBox chkShowTutorial2;
    private javax.swing.JCheckBox chkShowVRWelcome;
    private javax.swing.JCheckBox chkShowZoom;
    private javax.swing.JCheckBox chkTruckCameraGyro;
    private javax.swing.JCheckBox chkTruckCameraReverseVertical;
    private javax.swing.JCheckBox chkUnlockOdysseyLevels;
    private javax.swing.JComboBox<com.aurumsmods.ctse.LocalizeString> cmoCurrentSeasonId;
    private javax.swing.Box.Filler filler;
    private javax.swing.JLabel lblCurrentSeasonId;
    private javax.swing.JLabel lblHeadControlSetting;
    private javax.swing.JLabel lblHeadGameFlag;
    private javax.swing.JLabel lblHeadGeneral;
    private javax.swing.JLabel lblHeadMessageFlag;
    private javax.swing.JLabel lblLastPlayTime;
    private javax.swing.JLabel lblLastUptime;
    private javax.swing.JLabel lblPlayerLife;
    private javax.swing.JSeparator sep1;
    private javax.swing.JSeparator sep2;
    private javax.swing.JSeparator sep3;
    private javax.swing.JSpinner spnLastPlayTime;
    private javax.swing.JSpinner spnLastUptime;
    private javax.swing.JSpinner spnPlayerLife;
    // End of variables declaration//GEN-END:variables
}
