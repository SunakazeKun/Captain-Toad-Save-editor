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
package com.aurumsmods.ctse.editor;

import com.aurumsmods.ctse.LocalizeString;
import com.aurumsmods.ctse.Localization;
import com.aurumsmods.ctse.format.SeasonData;
import com.aurumsmods.ctse.format.StageNode;
import java.util.ArrayList;
import java.util.List;
import javax.swing.SpinnerNumberModel;

/**
 * An editor widget for editing SeasonData information.
 * @author Aurum
 */
final class SeasonDataWidget extends javax.swing.JPanel {
    private final KinopioSaveEditor editor;    // parent editor context
    private final List<StageNode> actualPages; // actual selectable pages
    private final List<StageNode> bonusPages;  // pages that can hold a bonus level
    SeasonData seasonData;                     // currently edited SeasonData
    boolean blockInput;                        // blocks user input when necessary
    
    /**
     * Constructs a new SeasonDataWidget using the specified editor context.
     * @param context the save editor context.
     */
    public SeasonDataWidget(KinopioSaveEditor context) {
        initComponents();
        
        editor = context;
        actualPages = new ArrayList(35);
        bonusPages = new ArrayList(35);
        seasonData = null;
        blockInput = true;
        
        cmoBonusCourseTypeCounter.addItem(new LocalizeString("stage.name.BonusStage1"));
        cmoBonusCourseTypeCounter.addItem(new LocalizeString("bonuscountertype.name.coins_galore_alt"));
        cmoBonusCourseTypeCounter.addItem(new LocalizeString("stage.name.GhostPlayerMaze1Stage"));
        cmoDLCBonusCourseTypeCounter.addItem(new LocalizeString("stage.name.BonusDragonRideStage"));
        cmoDLCBonusCourseTypeCounter.addItem(new LocalizeString("stage.name.BonusStage1"));
        cmoDLCBonusCourseTypeCounter.addItem(new LocalizeString("stage.name.BonusStage2"));
        cmoDLCBonusCourseTypeCounter.addItem(new LocalizeString("stage.name.BonusStage3"));
    }
    
    /**
     * Reloads all information from the SeasonData. This should be invoked be the save editor after selecting a season node.
     */
    void reloadData() {
        blockInput = true;
        
        if (seasonData != null) {
            // Collect valid course pages for the current season -> update selection box
            StageNode.collectSeasonActualPages(actualPages, editor.saveData.getGameVersion(), seasonData.seasonId);
            cmoLastPlayCourseId.removeAllItems();
            for (StageNode node : actualPages)
                cmoLastPlayCourseId.addItem(node);

            // Select last played course
            int selIdx = 0;
            for (int i = 0 ; i < actualPages.size() ; i++) {
                if (actualPages.get(i).getCourseId() == seasonData.lastPlayCourseId) {
                    selIdx = i;
                    break;
                }
            }
            cmoLastPlayCourseId.setSelectedIndex(selIdx);
            
            // Get opening ended flag
            chkOpeningEnded.setSelected(seasonData.openingEnded);
            
            // If necessary, load bonus data
            boolean hasBonusData = seasonData.seasonId != 4 && seasonData.seasonId != 6;
            boolean isSeason5 = seasonData.seasonId == 5;
            
            if (hasBonusData) {
                // Set bonus course counter spinner
                // Special Episode bonus course counter takes less cycles to regenerate -> change spinner maximum
                SpinnerNumberModel bonusCourseCounterModel = (SpinnerNumberModel)spnBonusCourseCounter.getModel();
                bonusCourseCounterModel.setMaximum(isSeason5 ? 4 : 6);
                spnBonusCourseCounter.setValue(seasonData.bonusCourseCounter);

                // Collect valid bonus pages for the current season -> update selection box
                cmoBonusCoursePageId.removeAllItems();
                StageNode.collectSeasonValidBonusPages(bonusPages, editor.saveData.getGameVersion(), seasonData.seasonId);
                for (StageNode node : bonusPages)
                    cmoBonusCoursePageId.addItem(node);

                // Select bonus course page
                selIdx = 0;
                for (int i = 0 ; i < bonusPages.size() ; i++) {
                    if (bonusPages.get(i).getPageId() == seasonData.bonusCoursePageId) {
                        selIdx = i;
                        break;
                    }
                }
                cmoBonusCoursePageId.setSelectedIndex(selIdx);
                
                // Set bonus course type counters
                cmoBonusCourseTypeCounter.setSelectedIndex(seasonData.bonusCourseTypeCounter);
                cmoDLCBonusCourseTypeCounter.setSelectedIndex(seasonData.dlcBonusCourseTypeCounter);
            }
            
            // Set visibility of bonus data components
            lblBonusCoursePageId.setVisible(hasBonusData);
            lblBonusCourseCounter.setVisible(hasBonusData);
            lblBonusCourseTypeCounter.setVisible(hasBonusData && !isSeason5);
            lblDLCBonusCourseTypeCounter.setVisible(hasBonusData && isSeason5);
            cmoBonusCoursePageId.setVisible(hasBonusData);
            spnBonusCourseCounter.setVisible(hasBonusData);
            cmoBonusCourseTypeCounter.setVisible(hasBonusData && !isSeason5);
            cmoDLCBonusCourseTypeCounter.setVisible(hasBonusData && isSeason5);
            
            blockInput = false;
        }
    }
    
    /**
     * Updates the localized text strings for display components.
     */
    void localizeAll() {
        blockInput = true;
        Localization localization = Localization.getLocalization();
        
        lblLastPlayCourseId.setText(localization.getText("seasondatawidget.lblLastPlayCourseId.text"));
        lblBonusCoursePageId.setText(localization.getText("seasondatawidget.lblBonusCoursePageId.text"));
        lblBonusCourseCounter.setText(localization.getText("seasondatawidget.lblBonusCourseCounter.text"));
        lblBonusCourseTypeCounter.setText(localization.getText("seasondatawidget.lblBonusCourseTypeCounter.text"));
        lblDLCBonusCourseTypeCounter.setText(localization.getText("seasondatawidget.lblBonusCourseTypeCounter.text"));
        chkOpeningEnded.setText(localization.getText("seasondatawidget.chkOpeningEnded.text"));
        
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

        lblLastPlayCourseId = new javax.swing.JLabel();
        lblBonusCoursePageId = new javax.swing.JLabel();
        lblBonusCourseCounter = new javax.swing.JLabel();
        lblBonusCourseTypeCounter = new javax.swing.JLabel();
        lblDLCBonusCourseTypeCounter = new javax.swing.JLabel();
        cmoLastPlayCourseId = new javax.swing.JComboBox<>();
        cmoBonusCoursePageId = new javax.swing.JComboBox<>();
        spnBonusCourseCounter = new javax.swing.JSpinner();
        cmoBonusCourseTypeCounter = new javax.swing.JComboBox<>();
        cmoDLCBonusCourseTypeCounter = new javax.swing.JComboBox<>();
        chkOpeningEnded = new javax.swing.JCheckBox();
        filler1 = new javax.swing.Box.Filler(new java.awt.Dimension(0, 0), new java.awt.Dimension(0, 0), new java.awt.Dimension(0, 32767));

        setLayout(new java.awt.GridBagLayout());

        lblLastPlayCourseId.setText("Last selected page");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 0.5;
        gridBagConstraints.insets = new java.awt.Insets(2, 2, 2, 2);
        add(lblLastPlayCourseId, gridBagConstraints);

        lblBonusCoursePageId.setText("Last bonus level page");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 0.5;
        gridBagConstraints.insets = new java.awt.Insets(2, 2, 2, 2);
        add(lblBonusCoursePageId, gridBagConstraints);

        lblBonusCourseCounter.setText("Counter until bonus level");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 0.5;
        gridBagConstraints.insets = new java.awt.Insets(2, 2, 2, 2);
        add(lblBonusCourseCounter, gridBagConstraints);

        lblBonusCourseTypeCounter.setText("Bonus type counter");
        lblBonusCourseTypeCounter.setToolTipText("");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 0.5;
        gridBagConstraints.insets = new java.awt.Insets(2, 2, 2, 2);
        add(lblBonusCourseTypeCounter, gridBagConstraints);

        lblDLCBonusCourseTypeCounter.setText("Special Episode bonus type counter");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 0.5;
        gridBagConstraints.insets = new java.awt.Insets(2, 2, 2, 2);
        add(lblDLCBonusCourseTypeCounter, gridBagConstraints);

        cmoLastPlayCourseId.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cmoLastPlayCourseIdActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 0.5;
        gridBagConstraints.insets = new java.awt.Insets(2, 2, 2, 2);
        add(cmoLastPlayCourseId, gridBagConstraints);

        cmoBonusCoursePageId.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cmoBonusCoursePageIdActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 0.5;
        gridBagConstraints.insets = new java.awt.Insets(2, 2, 2, 2);
        add(cmoBonusCoursePageId, gridBagConstraints);

        spnBonusCourseCounter.setModel(new javax.swing.SpinnerNumberModel(0, 0, 6, 1));
        spnBonusCourseCounter.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                spnBonusCourseCounterStateChanged(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 0.5;
        gridBagConstraints.insets = new java.awt.Insets(2, 2, 2, 2);
        add(spnBonusCourseCounter, gridBagConstraints);

        cmoBonusCourseTypeCounter.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cmoBonusCourseTypeCounterActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 0.5;
        gridBagConstraints.insets = new java.awt.Insets(2, 2, 2, 2);
        add(cmoBonusCourseTypeCounter, gridBagConstraints);

        cmoDLCBonusCourseTypeCounter.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cmoDLCBonusCourseTypeCounterActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 0.5;
        gridBagConstraints.insets = new java.awt.Insets(2, 2, 2, 2);
        add(cmoDLCBonusCourseTypeCounter, gridBagConstraints);

        chkOpeningEnded.setText("Has opening ended?");
        chkOpeningEnded.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                chkOpeningEndedActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(2, 2, 2, 2);
        add(chkOpeningEnded, gridBagConstraints);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        add(filler1, gridBagConstraints);
    }// </editor-fold>//GEN-END:initComponents

    private void cmoLastPlayCourseIdActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cmoLastPlayCourseIdActionPerformed
        if (blockInput)
            return;
        seasonData.lastPlayCourseId = ((StageNode)cmoLastPlayCourseId.getSelectedItem()).getCourseId();
        editor.needSaveChanges();
    }//GEN-LAST:event_cmoLastPlayCourseIdActionPerformed

    private void cmoBonusCoursePageIdActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cmoBonusCoursePageIdActionPerformed
        if (blockInput)
            return;
        seasonData.bonusCoursePageId = ((StageNode)cmoBonusCoursePageId.getSelectedItem()).getPageId();
        editor.needSaveChanges();
    }//GEN-LAST:event_cmoBonusCoursePageIdActionPerformed

    private void spnBonusCourseCounterStateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_spnBonusCourseCounterStateChanged
        if (blockInput)
            return;
        seasonData.bonusCourseCounter = (int)spnBonusCourseCounter.getValue();
        editor.needSaveChanges();
    }//GEN-LAST:event_spnBonusCourseCounterStateChanged

    private void cmoBonusCourseTypeCounterActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cmoBonusCourseTypeCounterActionPerformed
        if (blockInput)
            return;
        seasonData.bonusCourseTypeCounter = cmoBonusCourseTypeCounter.getSelectedIndex();
        editor.needSaveChanges();
    }//GEN-LAST:event_cmoBonusCourseTypeCounterActionPerformed

    private void cmoDLCBonusCourseTypeCounterActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cmoDLCBonusCourseTypeCounterActionPerformed
        if (blockInput)
            return;
        seasonData.dlcBonusCourseTypeCounter = cmoDLCBonusCourseTypeCounter.getSelectedIndex();
        editor.needSaveChanges();
    }//GEN-LAST:event_cmoDLCBonusCourseTypeCounterActionPerformed

    private void chkOpeningEndedActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_chkOpeningEndedActionPerformed
        if (blockInput)
            return;
        seasonData.openingEnded = chkOpeningEnded.isSelected();
        editor.needSaveChanges();
    }//GEN-LAST:event_chkOpeningEndedActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JCheckBox chkOpeningEnded;
    private javax.swing.JComboBox<StageNode> cmoBonusCoursePageId;
    private javax.swing.JComboBox<com.aurumsmods.ctse.LocalizeString> cmoBonusCourseTypeCounter;
    private javax.swing.JComboBox<com.aurumsmods.ctse.LocalizeString> cmoDLCBonusCourseTypeCounter;
    private javax.swing.JComboBox<StageNode> cmoLastPlayCourseId;
    private javax.swing.Box.Filler filler1;
    private javax.swing.JLabel lblBonusCourseCounter;
    private javax.swing.JLabel lblBonusCoursePageId;
    private javax.swing.JLabel lblBonusCourseTypeCounter;
    private javax.swing.JLabel lblDLCBonusCourseTypeCounter;
    private javax.swing.JLabel lblLastPlayCourseId;
    private javax.swing.JSpinner spnBonusCourseCounter;
    // End of variables declaration//GEN-END:variables
}
