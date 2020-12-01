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

import java.awt.Component;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.prefs.Preferences;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.JTree;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.tree.DefaultTreeCellRenderer;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreePath;
import javax.swing.tree.TreeSelectionModel;

public class SaveEditor extends javax.swing.JFrame {
    private class StageTreeCellRenderer extends DefaultTreeCellRenderer {
        @Override
        public Component getTreeCellRendererComponent(JTree tree,
                Object value, boolean selected, boolean expanded,
                boolean leaf, int row, boolean hasFocus) {
            super.getTreeCellRendererComponent(tree, value, selected, expanded, leaf, row, hasFocus);
            
            if (value == null || !(value instanceof StageNode))
                return this;
            
            setIcon(CommonAssets.ICONS_NODES[((StageNode)value).getNodeIcon()]);
            
            return this;
        }
    }
    
    //--------------------------------------------------------------------------------------------------------------------------
    
    private File saveFile;
    private SaveData saveData;
    private SaveData.StageInfo selectedStageInfo;
    private StageNode selectedStageNode;
    
    public SaveEditor() {
        saveFile = null;
        saveData = null;
        selectedStageInfo = null;
        selectedStageNode = null;
        
        initComponents();
        treeStages.getSelectionModel().setSelectionMode(TreeSelectionModel.SINGLE_TREE_SELECTION);
        
        initLocalization();
        enableComponents(false);
        showLevelComponents();
    }
    
    private void initLocalization() {
        switch(CTSe.getLocalization()) {
            case "en_US": radEnglishUs.setSelected(true); break;
            case "en_UK": radEnglishUk.setSelected(true); break;
            case "de_DE": radGerman.setSelected(true); break;
        }
        
        loadLocalization();
    }
    
    private void loadLocalization() {
        mnuFile.setText(CommonAssets.getText("editor.menu.file"));
        mniNew.setText(CommonAssets.getText("editor.menu.file.new"));
        mniOpen.setText(CommonAssets.getText("editor.menu.file.open"));
        mniSave.setText(CommonAssets.getText("editor.menu.file.save"));
        mniSaveAs.setText(CommonAssets.getText("editor.menu.file.save_as"));
        mniExit.setText(CommonAssets.getText("editor.menu.file.exit"));
        mnuLanguage.setText(CommonAssets.getText("editor.menu.language"));
        mnuHelp.setText(CommonAssets.getText("editor.menu.help"));
        mniAbout.setText(CommonAssets.getText("editor.menu.help.about"));
        
        tabAll.setTitleAt(0, CommonAssets.getText("editor.pnlStages.title"));
        tabAll.setTitleAt(1, CommonAssets.getText("editor.pnlMisc.title"));
        
        chkFlagIsOpen.setText(CommonAssets.getText("editor.chkFlagIsOpen.text"));
        chkFlagIsNew.setText(CommonAssets.getText("editor.chkFlagIsNew.text"));
        chkFlagIsBeat.setText(CommonAssets.getText("editor.chkFlagIsBeat.text"));
        chkFlagIsComplete.setText(CommonAssets.getText("editor.chkFlagIsComplete.text"));
        chkHasDotKinopio.setText(CommonAssets.getText("editor.chkHasDotKinopio.text"));
        lblCoinNum.setText(CommonAssets.getText("editor.lblCoinNum.text"));
        lblClearTime.setText(CommonAssets.getText("editor.lblClearTime.text"));
        lblLastSavedTime.setText(CommonAssets.getText("editor.lblLastSavedTime.text"));
        
        lblLastGameSavedTime.setText(CommonAssets.getText("editor.lblLastGameSavedTime.text"));
        lblNumOneUps.setText(CommonAssets.getText("editor.lblNumOneUps.text"));
    }
    
    private void enableComponents(boolean state) {
        mniSave.setEnabled(state);
        mniSaveAs.setEnabled(state);
        tabAll.setEnabled(state);
    }
    
    private void showMessageDialog(int dialogType, String text) {
        JOptionPane.showMessageDialog(this, CommonAssets.getText(text), CTSe.getTitle(), dialogType);
    }
    
    private int showConfirmDialog(int dialogType, String text) {
        return JOptionPane.showConfirmDialog(this, CommonAssets.getText(text), CTSe.getTitle(), dialogType);
    }
    
    private void populateData() {
        reloadTree(saveData.version);
        selectedStageInfo = null;
        
        spnLastGameSavedTime.setValue(new Date(saveData.lastGameSaveTime * 1000));
        spnNumOneUps.setValue(saveData.numOneUps);
    }
    
    private void populateLevelData() {
        showLevelComponents();
        lblLevelPreview.setIcon(null);
        
        if (selectedStageInfo != null && selectedStageNode.getStageType() != StageNode.TYPE_NOT_A_STAGE) {
            if (selectedStageNode.hasPreviewImage()) {
                String stageName = selectedStageNode.getStageName();
                if (stageName.startsWith("VR"))
                    stageName = stageName.substring(2);
                lblLevelPreview.setIcon(CommonAssets.loadIcon(String.format("img/stages/%s.png", stageName)));
            }
            
            // Update stage-specific UI stuff
            int collectItemIconIdx = selectedStageNode.hasDlcCollectItem() ? 2 : 1;
            chkHasCollectItem1.setSelectedIcon(CommonAssets.ICONS_COLLECT_ITEMS[collectItemIconIdx]);
            chkHasCollectItem2.setSelectedIcon(CommonAssets.ICONS_COLLECT_ITEMS[collectItemIconIdx]);
            chkHasCollectItem3.setSelectedIcon(CommonAssets.ICONS_COLLECT_ITEMS[collectItemIconIdx]);
            chkHasBadgeCondition.setText(CommonAssets.getText(String.format("stage.badgeCondition.%s", selectedStageNode.getStageName())));
            lblChallengeTime.setText(String.format("/ %03d", selectedStageNode.getChallengeTime()));
            
            // Populate player's stage data
            int collectItemFlags = selectedStageInfo.getCollectItemFlags();
            chkHasCollectItem1.setSelected(BitUtil.test(collectItemFlags, 0));
            chkHasCollectItem2.setSelected(BitUtil.test(collectItemFlags, 1));
            chkHasCollectItem3.setSelected(BitUtil.test(collectItemFlags, 2));
            chkHasBadgeCondition.setSelected(selectedStageInfo.getHasBadgeCondition());
            chkHasDotKinopio.setSelected(selectedStageInfo.getHasDotKinopio());
            spnCoinNum.setValue(selectedStageInfo.getCoinNum());
            spnClearTime.setValue(selectedStageInfo.getClearTime());
            spnLastSavedTime.setValue(new Date(selectedStageInfo.getLastSavedTime() * 1000));
            chkFlagIsOpen.setSelected(selectedStageInfo.getIsOpen());
            chkFlagIsBeat.setSelected(selectedStageInfo.getIsBeaten());
            chkFlag2.setSelected(selectedStageInfo.getFlag2());
            chkFlagIsNew.setSelected(selectedStageInfo.getIsNew());
            chkFlag4.setSelected(selectedStageInfo.getFlag4());
            chkFlagIsComplete.setSelected(selectedStageInfo.getIsCompleted());
            spnUnkC.setValue(selectedStageInfo.getUnkC());
        }
    }
    
    //--------------------------------------------------------------------------------------------------------------------------
    
    final void showLevelComponents() {
        boolean showPreview = false;
        boolean showHasCollectItem = false;
        boolean showHasBadgeCondition = false;
        boolean showHasDotKinopio = false;
        boolean showCoinsAndUnkC = false;
        boolean showTime = false;
        boolean showFlags = false;
        
        if (selectedStageNode != null && selectedStageNode.getStageType() != StageNode.TYPE_NOT_A_STAGE) {
            int nodeType = selectedStageNode.getNodeType();
            showPreview = selectedStageNode.hasPreviewImage();
            showHasCollectItem = selectedStageNode.hasCollectItem();
            showHasBadgeCondition = selectedStageNode.hasBadgeCondition();
            showHasDotKinopio = selectedStageNode.hasDotKinopio();
            showCoinsAndUnkC = nodeType == StageNode.NODE_STAGE;
            showTime = selectedStageNode.hasChallengeTime();
            showFlags = showCoinsAndUnkC || nodeType == StageNode.NODE_CHAPTER;
        }
        
        pnlLevelPreview.setVisible(showPreview);
        lblLevelPreview.setVisible(showPreview);
        pnlCollectItem.setVisible(showHasCollectItem);
        chkHasCollectItem1.setVisible(showHasCollectItem);
        chkHasCollectItem2.setVisible(showHasCollectItem);
        chkHasCollectItem3.setVisible(showHasCollectItem);
        chkHasBadgeCondition.setVisible(showHasBadgeCondition);
        chkHasDotKinopio.setVisible(showHasDotKinopio);
        lblCoinNum.setVisible(showCoinsAndUnkC);
        spnCoinNum.setVisible(showCoinsAndUnkC);
        lblClearTime.setVisible(showTime);
        spnClearTime.setVisible(showTime);
        lblChallengeTime.setVisible(showTime);
        lblLastSavedTime.setVisible(showCoinsAndUnkC);
        spnLastSavedTime.setVisible(showCoinsAndUnkC);
        chkFlagIsOpen.setVisible(showFlags);
        chkFlagIsBeat.setVisible(showFlags);
        chkFlag2.setVisible(showFlags);
        chkFlagIsNew.setVisible(showFlags);
        chkFlag4.setVisible(showFlags);
        chkFlagIsComplete.setVisible(showFlags);
        lblUnkC.setVisible(showCoinsAndUnkC);
        spnUnkC.setVisible(showCoinsAndUnkC);
        filLevelInfo.setVisible(true);
    }
    
    final void changeMenuLanguage(String lang) {
        if (!CTSe.getLocalization().equals(lang)) {
            CTSe.setLocalization(lang);
            CommonAssets.initLocalization();
            
            loadLocalization();
            ((DefaultTreeModel)treeStages.getModel()).reload();
            expandTree(CommonAssets.getLevelNodeRoot());
            
            selectedStageNode = null;
            showLevelComponents();
        }
    }
    
    final void reloadTree(int ver) {
        CommonAssets.reloadTree(ver);
        treeStages.setSelectionPath(null);
        ((DefaultTreeModel)treeStages.getModel()).reload();
        expandTree(CommonAssets.getLevelNodeRoot());
    }
    
    final void expandTree(StageNode node) {
        for (StageNode n : (ArrayList<StageNode>)Collections.list(node.children()))
            expandTree(n);
        
        if (node.isRoot())
            return;
        
        TreePath path = new TreePath(node.getPath());
        treeStages.expandPath(path);
    }
    
    //--------------------------------------------------------------------------------------------------------------------------
    
    final boolean dropChanges() {
        boolean ret = true;
        if (saveData != null) {
            int result = showConfirmDialog(JOptionPane.YES_NO_OPTION, "editor.message.already_editing");
            ret = result == JOptionPane.YES_OPTION;
        }
        return ret;
    }
    
    final void chooseSaveFile(String title) {
        final JFileChooser fc = new JFileChooser();
        fc.setDialogTitle(CommonAssets.getText(title));
        fc.setFileFilter(new FileNameExtensionFilter("Binary (*.bin)", ".bin", "bin"));
        
        String lastdir = Preferences.userRoot().get("ctse_lastFile", null);
        if (lastdir != null)
            fc.setSelectedFile(new File(lastdir));
        if (fc.showOpenDialog(this) != JFileChooser.APPROVE_OPTION)
            return;
        
        saveFile = fc.getSelectedFile();
        if (!(saveFile.exists() && saveFile.isFile()))
            return;
        Preferences.userRoot().put("ctse_lastFile", saveFile.getPath());
    }
    
    final void loadSaveDataFromFile() {
        if (saveFile == null)
            return;
        
        saveData = null;
        
        try (FileInputStream in = new FileInputStream(saveFile)) {
            int bufsize = in.available();
            
            if (bufsize == SaveData.SAVE_FILE_SIZE) {
                byte[] raw = new byte[bufsize];
                in.read(raw);
                saveData = new SaveData(raw);

                if (!saveData.isValid()) {
                    showMessageDialog(JOptionPane.ERROR_MESSAGE, "editor.message.invalid_save_file");
                    saveData = null;
                }
                else {
                    /*if (saveData.isOutdated() && showConfirmDialog(JOptionPane.YES_NO_OPTION, "editor.message.update_save_file") == JOptionPane.YES_OPTION)
                        saveData.update();*/
                    populateData();
                }
            }
            else
                showMessageDialog(JOptionPane.ERROR_MESSAGE, "editor.message.invalid_save_size");
        }
        catch(IOException ex) {
            System.err.println(ex);
        }
        
        enableComponents(saveData != null);
    }
    
    final void writeSaveDataToFile() {
        if (saveFile == null)
            return;
        
        try (FileOutputStream out = new FileOutputStream(saveFile)) {
            out.write(saveData.save());
            out.flush();
        }
        catch(IOException ex) {
            System.err.println(ex);
        }
    }
    
    //--------------------------------------------------------------------------------------------------------------------------
    
    final void toggleCollectItemFlag(int bit, boolean state) {
        selectedStageInfo.setCollectItemFlags(BitUtil.toggle(selectedStageInfo.getCollectItemFlags(), bit, state));
    }
    
    //--------------------------------------------------------------------------------------------------------------------------
    
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {
        java.awt.GridBagConstraints gridBagConstraints;

        rdgLanguage = new javax.swing.ButtonGroup();
        tabAll = new javax.swing.JTabbedPane();
        pnlStages = new javax.swing.JPanel();
        scrTreeStages = new javax.swing.JScrollPane();
        treeStages = new javax.swing.JTree();
        pnlLevelInfo = new javax.swing.JPanel();
        pnlLevelPreview = new javax.swing.JPanel();
        lblLevelPreview = new javax.swing.JLabel();
        chkFlagIsOpen = new javax.swing.JCheckBox();
        chkFlagIsNew = new javax.swing.JCheckBox();
        chkFlagIsBeat = new javax.swing.JCheckBox();
        chkFlagIsComplete = new javax.swing.JCheckBox();
        chkFlag2 = new javax.swing.JCheckBox();
        chkFlag4 = new javax.swing.JCheckBox();
        pnlCollectItem = new javax.swing.JPanel();
        chkHasCollectItem1 = new javax.swing.JCheckBox();
        chkHasCollectItem2 = new javax.swing.JCheckBox();
        chkHasCollectItem3 = new javax.swing.JCheckBox();
        chkHasBadgeCondition = new javax.swing.JCheckBox();
        chkHasDotKinopio = new javax.swing.JCheckBox();
        lblCoinNum = new javax.swing.JLabel();
        spnCoinNum = new javax.swing.JSpinner();
        lblClearTime = new javax.swing.JLabel();
        spnClearTime = new javax.swing.JSpinner();
        lblChallengeTime = new javax.swing.JLabel();
        lblLastSavedTime = new javax.swing.JLabel();
        spnLastSavedTime = new javax.swing.JSpinner();
        lblUnkC = new javax.swing.JLabel();
        spnUnkC = new javax.swing.JSpinner();
        filLevelInfo = new javax.swing.Box.Filler(new java.awt.Dimension(0, 0), new java.awt.Dimension(0, 0), new java.awt.Dimension(0, 32767));
        pnlMisc = new javax.swing.JPanel();
        lblLastGameSavedTime = new javax.swing.JLabel();
        spnLastGameSavedTime = new javax.swing.JSpinner();
        lblNumOneUps = new javax.swing.JLabel();
        spnNumOneUps = new javax.swing.JSpinner();
        filLevelInfo1 = new javax.swing.Box.Filler(new java.awt.Dimension(0, 0), new java.awt.Dimension(0, 0), new java.awt.Dimension(0, 32767));
        filLevelInfo2 = new javax.swing.Box.Filler(new java.awt.Dimension(0, 0), new java.awt.Dimension(0, 0), new java.awt.Dimension(0, 32767));
        mnbMain = new javax.swing.JMenuBar();
        mnuFile = new javax.swing.JMenu();
        mniNew = new javax.swing.JMenuItem();
        mniOpen = new javax.swing.JMenuItem();
        mniSave = new javax.swing.JMenuItem();
        mniSaveAs = new javax.swing.JMenuItem();
        mspFile = new javax.swing.JPopupMenu.Separator();
        mniExit = new javax.swing.JMenuItem();
        mnuLanguage = new javax.swing.JMenu();
        radEnglishUs = new javax.swing.JRadioButtonMenuItem();
        radEnglishUk = new javax.swing.JRadioButtonMenuItem();
        radGerman = new javax.swing.JRadioButtonMenuItem();
        mnuHelp = new javax.swing.JMenu();
        mniAbout = new javax.swing.JMenuItem();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setTitle(CTSe.getTitle());
        setIconImage(CommonAssets.PROGRAM_ICON);
        setResizable(false);
        addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowClosing(java.awt.event.WindowEvent evt) {
                formWindowClosing(evt);
            }
        });

        treeStages.setModel(new DefaultTreeModel(CommonAssets.getLevelNodeRoot()));
        treeStages.setCellRenderer(new StageTreeCellRenderer());
        treeStages.setRootVisible(false);
        treeStages.setRowHeight(24);
        treeStages.addTreeSelectionListener(new javax.swing.event.TreeSelectionListener() {
            public void valueChanged(javax.swing.event.TreeSelectionEvent evt) {
                treeStagesValueChanged(evt);
            }
        });
        scrTreeStages.setViewportView(treeStages);

        pnlLevelInfo.setLayout(new java.awt.GridBagLayout());

        pnlLevelPreview.add(lblLevelPreview);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.gridwidth = 3;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(1, 1, 1, 1);
        pnlLevelInfo.add(pnlLevelPreview, gridBagConstraints);

        chkFlagIsOpen.setText("Page open?");
        chkFlagIsOpen.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                chkFlagIsOpenActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 0.5;
        gridBagConstraints.insets = new java.awt.Insets(1, 1, 1, 1);
        pnlLevelInfo.add(chkFlagIsOpen, gridBagConstraints);

        chkFlagIsNew.setText("New page?");
        chkFlagIsNew.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                chkFlagIsNewActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 0.5;
        gridBagConstraints.insets = new java.awt.Insets(1, 1, 1, 1);
        pnlLevelInfo.add(chkFlagIsNew, gridBagConstraints);

        chkFlagIsBeat.setText("Beat level?");
        chkFlagIsBeat.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                chkFlagIsBeatActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 0.5;
        gridBagConstraints.insets = new java.awt.Insets(1, 1, 1, 1);
        pnlLevelInfo.add(chkFlagIsBeat, gridBagConstraints);

        chkFlagIsComplete.setText("Completed level?");
        chkFlagIsComplete.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                chkFlagIsCompleteActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(1, 1, 1, 1);
        pnlLevelInfo.add(chkFlagIsComplete, gridBagConstraints);

        chkFlag2.setText("Flag 2");
        chkFlag2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                chkFlag2ActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(1, 1, 1, 1);
        pnlLevelInfo.add(chkFlag2, gridBagConstraints);

        chkFlag4.setText("Flag 4");
        chkFlag4.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                chkFlag4ActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(1, 1, 1, 1);
        pnlLevelInfo.add(chkFlag4, gridBagConstraints);

        chkHasCollectItem1.setIcon(CommonAssets.ICONS_COLLECT_ITEMS[0]);
        chkHasCollectItem1.setSelectedIcon(CommonAssets.ICONS_COLLECT_ITEMS[1]);
        chkHasCollectItem1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                chkHasCollectItem1ActionPerformed(evt);
            }
        });
        pnlCollectItem.add(chkHasCollectItem1);

        chkHasCollectItem2.setIcon(CommonAssets.ICONS_COLLECT_ITEMS[0]);
        chkHasCollectItem2.setSelectedIcon(CommonAssets.ICONS_COLLECT_ITEMS[1]);
        chkHasCollectItem2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                chkHasCollectItem2ActionPerformed(evt);
            }
        });
        pnlCollectItem.add(chkHasCollectItem2);

        chkHasCollectItem3.setIcon(CommonAssets.ICONS_COLLECT_ITEMS[0]);
        chkHasCollectItem3.setSelectedIcon(CommonAssets.ICONS_COLLECT_ITEMS[1]);
        chkHasCollectItem3.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                chkHasCollectItem3ActionPerformed(evt);
            }
        });
        pnlCollectItem.add(chkHasCollectItem3);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.gridwidth = 3;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(1, 1, 1, 1);
        pnlLevelInfo.add(pnlCollectItem, gridBagConstraints);

        chkHasBadgeCondition.setText("Bonus condition?");
        chkHasBadgeCondition.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                chkHasBadgeConditionActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 5;
        gridBagConstraints.gridwidth = 3;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(1, 1, 1, 1);
        pnlLevelInfo.add(chkHasBadgeCondition, gridBagConstraints);

        chkHasDotKinopio.setText("Found Pixel Toad?");
        chkHasDotKinopio.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                chkHasDotKinopioActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 6;
        gridBagConstraints.gridwidth = 3;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(1, 1, 1, 1);
        pnlLevelInfo.add(chkHasDotKinopio, gridBagConstraints);

        lblCoinNum.setText("Best coin run");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 7;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 0.25;
        gridBagConstraints.insets = new java.awt.Insets(1, 1, 1, 1);
        pnlLevelInfo.add(lblCoinNum, gridBagConstraints);

        spnCoinNum.setModel(new javax.swing.SpinnerNumberModel(0, 0, 9999, 1));
        spnCoinNum.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                spnCoinNumStateChanged(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 7;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 0.5;
        gridBagConstraints.insets = new java.awt.Insets(1, 1, 1, 1);
        pnlLevelInfo.add(spnCoinNum, gridBagConstraints);

        lblClearTime.setText("Fastest time");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 8;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 0.25;
        gridBagConstraints.insets = new java.awt.Insets(1, 1, 1, 1);
        pnlLevelInfo.add(lblClearTime, gridBagConstraints);

        spnClearTime.setModel(new javax.swing.SpinnerNumberModel(0, 0, 999, 1));
        spnClearTime.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                spnClearTimeStateChanged(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 8;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 0.5;
        gridBagConstraints.insets = new java.awt.Insets(1, 1, 1, 1);
        pnlLevelInfo.add(spnClearTime, gridBagConstraints);

        lblChallengeTime.setText("/ 0");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 8;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.insets = new java.awt.Insets(1, 1, 1, 1);
        pnlLevelInfo.add(lblChallengeTime, gridBagConstraints);

        lblLastSavedTime.setText("Played");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 9;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 0.25;
        gridBagConstraints.insets = new java.awt.Insets(1, 1, 1, 1);
        pnlLevelInfo.add(lblLastSavedTime, gridBagConstraints);

        spnLastSavedTime.setModel(new javax.swing.SpinnerDateModel());
        spnLastSavedTime.setEditor(new javax.swing.JSpinner.DateEditor(spnLastSavedTime, "dd.MM.yyyy HH:mm:ss"));
        spnLastSavedTime.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                spnLastSavedTimeStateChanged(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 9;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 0.75;
        gridBagConstraints.insets = new java.awt.Insets(1, 1, 1, 1);
        pnlLevelInfo.add(spnLastSavedTime, gridBagConstraints);

        lblUnkC.setText("Unk. 0xC");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 10;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 0.25;
        gridBagConstraints.insets = new java.awt.Insets(1, 1, 1, 1);
        pnlLevelInfo.add(lblUnkC, gridBagConstraints);

        spnUnkC.setModel(new javax.swing.SpinnerNumberModel());
        spnUnkC.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                spnUnkCStateChanged(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 10;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 0.75;
        gridBagConstraints.insets = new java.awt.Insets(1, 1, 1, 1);
        pnlLevelInfo.add(spnUnkC, gridBagConstraints);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridwidth = 3;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        pnlLevelInfo.add(filLevelInfo, gridBagConstraints);

        javax.swing.GroupLayout pnlStagesLayout = new javax.swing.GroupLayout(pnlStages);
        pnlStages.setLayout(pnlStagesLayout);
        pnlStagesLayout.setHorizontalGroup(
            pnlStagesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlStagesLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(scrTreeStages, javax.swing.GroupLayout.PREFERRED_SIZE, 300, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(pnlLevelInfo, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
        );
        pnlStagesLayout.setVerticalGroup(
            pnlStagesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, pnlStagesLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(pnlStagesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(scrTreeStages, javax.swing.GroupLayout.DEFAULT_SIZE, 445, Short.MAX_VALUE)
                    .addComponent(pnlLevelInfo, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );

        tabAll.addTab("Episodes & levels", pnlStages);

        pnlMisc.setLayout(new java.awt.GridBagLayout());

        lblLastGameSavedTime.setText("Last time saved");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.insets = new java.awt.Insets(4, 4, 4, 4);
        pnlMisc.add(lblLastGameSavedTime, gridBagConstraints);

        spnLastGameSavedTime.setModel(new javax.swing.SpinnerDateModel());
        spnLastGameSavedTime.setEditor(new javax.swing.JSpinner.DateEditor(spnLastGameSavedTime, "dd.MM.yyyy HH:mm:ss"));
        spnLastGameSavedTime.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                spnLastGameSavedTimeStateChanged(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.insets = new java.awt.Insets(4, 4, 4, 4);
        pnlMisc.add(spnLastGameSavedTime, gridBagConstraints);

        lblNumOneUps.setText("1-UPs");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.insets = new java.awt.Insets(4, 4, 4, 4);
        pnlMisc.add(lblNumOneUps, gridBagConstraints);

        spnNumOneUps.setModel(new javax.swing.SpinnerNumberModel(0, 0, 99, 1));
        spnNumOneUps.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                spnNumOneUpsStateChanged(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.insets = new java.awt.Insets(4, 4, 4, 4);
        pnlMisc.add(spnNumOneUps, gridBagConstraints);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weighty = 1.0;
        pnlMisc.add(filLevelInfo1, gridBagConstraints);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.gridheight = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        pnlMisc.add(filLevelInfo2, gridBagConstraints);

        tabAll.addTab("Miscellaneous", pnlMisc);

        mnuFile.setMnemonic('F');
        mnuFile.setText("File");

        mniNew.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_N, java.awt.event.InputEvent.CTRL_MASK));
        mniNew.setMnemonic('N');
        mniNew.setText("New");
        mniNew.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mniNewActionPerformed(evt);
            }
        });
        mnuFile.add(mniNew);

        mniOpen.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_O, java.awt.event.InputEvent.CTRL_MASK));
        mniOpen.setMnemonic('O');
        mniOpen.setText("Open");
        mniOpen.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mniOpenActionPerformed(evt);
            }
        });
        mnuFile.add(mniOpen);

        mniSave.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_S, java.awt.event.InputEvent.CTRL_MASK));
        mniSave.setMnemonic('S');
        mniSave.setText("Save");
        mniSave.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mniSaveActionPerformed(evt);
            }
        });
        mnuFile.add(mniSave);

        mniSaveAs.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_S, java.awt.event.InputEvent.SHIFT_MASK | java.awt.event.InputEvent.CTRL_MASK));
        mniSaveAs.setMnemonic('A');
        mniSaveAs.setText("Save as");
        mniSaveAs.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mniSaveAsActionPerformed(evt);
            }
        });
        mnuFile.add(mniSaveAs);
        mnuFile.add(mspFile);

        mniExit.setMnemonic('E');
        mniExit.setText("Exit");
        mniExit.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mniExitActionPerformed(evt);
            }
        });
        mnuFile.add(mniExit);

        mnbMain.add(mnuFile);

        mnuLanguage.setMnemonic('L');
        mnuLanguage.setText("Language");

        rdgLanguage.add(radEnglishUs);
        radEnglishUs.setText("English (US)");
        radEnglishUs.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                radEnglishUsActionPerformed(evt);
            }
        });
        mnuLanguage.add(radEnglishUs);

        rdgLanguage.add(radEnglishUk);
        radEnglishUk.setSelected(true);
        radEnglishUk.setText("English (UK)");
        radEnglishUk.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                radEnglishUkActionPerformed(evt);
            }
        });
        mnuLanguage.add(radEnglishUk);

        rdgLanguage.add(radGerman);
        radGerman.setText("Deutsch");
        radGerman.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                radGermanActionPerformed(evt);
            }
        });
        mnuLanguage.add(radGerman);

        mnbMain.add(mnuLanguage);

        mnuHelp.setMnemonic('H');
        mnuHelp.setText("Help");

        mniAbout.setText("About");
        mniAbout.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mniAboutActionPerformed(evt);
            }
        });
        mnuHelp.add(mniAbout);

        mnbMain.add(mnuHelp);

        setJMenuBar(mnbMain);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(tabAll, javax.swing.GroupLayout.DEFAULT_SIZE, 593, Short.MAX_VALUE)
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(tabAll)
                .addContainerGap())
        );

        pack();
        setLocationRelativeTo(null);
    }// </editor-fold>//GEN-END:initComponents

    private void formWindowClosing(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_formWindowClosing
        CTSe.saveSettings();
    }//GEN-LAST:event_formWindowClosing

    private void mniNewActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mniNewActionPerformed
        if (dropChanges()) {
            saveFile = null;
            saveData = new SaveData();
            populateData();
            enableComponents(true);
        }
    }//GEN-LAST:event_mniNewActionPerformed

    private void mniOpenActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mniOpenActionPerformed
        if (dropChanges()) {
            chooseSaveFile("editor.file_chooser.open");
            loadSaveDataFromFile();
        }
    }//GEN-LAST:event_mniOpenActionPerformed

    private void mniSaveActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mniSaveActionPerformed
        if (saveFile == null)
            chooseSaveFile("editor.file_chooser.save");
        writeSaveDataToFile();
    }//GEN-LAST:event_mniSaveActionPerformed

    private void mniSaveAsActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mniSaveAsActionPerformed
        if (saveData == null)
            return;
        chooseSaveFile("editor.file_chooser.save");
        writeSaveDataToFile();
    }//GEN-LAST:event_mniSaveAsActionPerformed

    private void mniExitActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mniExitActionPerformed
        dispose();
    }//GEN-LAST:event_mniExitActionPerformed

    private void radEnglishUkActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_radEnglishUkActionPerformed
        changeMenuLanguage("en_UK");
    }//GEN-LAST:event_radEnglishUkActionPerformed

    private void radEnglishUsActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_radEnglishUsActionPerformed
        changeMenuLanguage("en_US");
    }//GEN-LAST:event_radEnglishUsActionPerformed

    private void radGermanActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_radGermanActionPerformed
        changeMenuLanguage("de_DE");
    }//GEN-LAST:event_radGermanActionPerformed

    private void mniAboutActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mniAboutActionPerformed
        showMessageDialog(JOptionPane.INFORMATION_MESSAGE, CTSe.getFullTitle());
    }//GEN-LAST:event_mniAboutActionPerformed

    private void treeStagesValueChanged(javax.swing.event.TreeSelectionEvent evt) {//GEN-FIRST:event_treeStagesValueChanged
        selectedStageNode = (StageNode)treeStages.getLastSelectedPathComponent();
        
        if (selectedStageNode == null)
            return;
        
        int courseId = selectedStageNode.getCourseId();
        selectedStageInfo = courseId < 0 ? null : saveData.stageInfos[courseId];
        populateLevelData();
    }//GEN-LAST:event_treeStagesValueChanged

    private void chkFlagIsOpenActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_chkFlagIsOpenActionPerformed
        selectedStageInfo.setIsOpen(chkFlagIsOpen.isSelected());
    }//GEN-LAST:event_chkFlagIsOpenActionPerformed

    private void chkFlagIsNewActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_chkFlagIsNewActionPerformed
        selectedStageInfo.setIsNew(chkFlagIsNew.isSelected());
    }//GEN-LAST:event_chkFlagIsNewActionPerformed

    private void chkFlagIsBeatActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_chkFlagIsBeatActionPerformed
        selectedStageInfo.setIsBeaten(chkFlagIsBeat.isSelected());
    }//GEN-LAST:event_chkFlagIsBeatActionPerformed

    private void chkFlagIsCompleteActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_chkFlagIsCompleteActionPerformed
        selectedStageInfo.setIsCompleted(chkFlagIsComplete.isSelected());
    }//GEN-LAST:event_chkFlagIsCompleteActionPerformed

    private void chkFlag2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_chkFlag2ActionPerformed
        selectedStageInfo.setFlag2(chkFlag2.isSelected());
    }//GEN-LAST:event_chkFlag2ActionPerformed

    private void chkFlag4ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_chkFlag4ActionPerformed
        selectedStageInfo.setFlag4(chkFlag4.isSelected());
    }//GEN-LAST:event_chkFlag4ActionPerformed

    private void chkHasCollectItem1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_chkHasCollectItem1ActionPerformed
        toggleCollectItemFlag(0, chkHasCollectItem1.isSelected());
    }//GEN-LAST:event_chkHasCollectItem1ActionPerformed

    private void chkHasCollectItem2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_chkHasCollectItem2ActionPerformed
        toggleCollectItemFlag(1, chkHasCollectItem2.isSelected());
    }//GEN-LAST:event_chkHasCollectItem2ActionPerformed

    private void chkHasCollectItem3ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_chkHasCollectItem3ActionPerformed
        toggleCollectItemFlag(2, chkHasCollectItem3.isSelected());
    }//GEN-LAST:event_chkHasCollectItem3ActionPerformed

    private void chkHasBadgeConditionActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_chkHasBadgeConditionActionPerformed
        selectedStageInfo.setHasBadgeCondition(chkHasBadgeCondition.isSelected());
    }//GEN-LAST:event_chkHasBadgeConditionActionPerformed

    private void chkHasDotKinopioActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_chkHasDotKinopioActionPerformed
        selectedStageInfo.setHasDotKinopio(chkHasDotKinopio.isSelected());
    }//GEN-LAST:event_chkHasDotKinopioActionPerformed

    private void spnCoinNumStateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_spnCoinNumStateChanged
        selectedStageInfo.setCoinNum((int)spnCoinNum.getValue());
    }//GEN-LAST:event_spnCoinNumStateChanged

    private void spnClearTimeStateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_spnClearTimeStateChanged
        selectedStageInfo.setClearTime((int)spnClearTime.getValue());
    }//GEN-LAST:event_spnClearTimeStateChanged

    private void spnLastSavedTimeStateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_spnLastSavedTimeStateChanged
        selectedStageInfo.setLastSavedTime(((Date)spnLastSavedTime.getValue()).getTime() / 1000);
    }//GEN-LAST:event_spnLastSavedTimeStateChanged

    private void spnUnkCStateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_spnUnkCStateChanged
        selectedStageInfo.setUnkC((int)spnUnkC.getValue());
    }//GEN-LAST:event_spnUnkCStateChanged

    private void spnLastGameSavedTimeStateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_spnLastGameSavedTimeStateChanged
        saveData.lastGameSaveTime = ((Date)spnLastGameSavedTime.getValue()).getTime() / 1000;
    }//GEN-LAST:event_spnLastGameSavedTimeStateChanged

    private void spnNumOneUpsStateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_spnNumOneUpsStateChanged
        saveData.numOneUps = (int)spnNumOneUps.getValue();
    }//GEN-LAST:event_spnNumOneUpsStateChanged
    
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JCheckBox chkFlag2;
    private javax.swing.JCheckBox chkFlag4;
    private javax.swing.JCheckBox chkFlagIsBeat;
    private javax.swing.JCheckBox chkFlagIsComplete;
    private javax.swing.JCheckBox chkFlagIsNew;
    private javax.swing.JCheckBox chkFlagIsOpen;
    private javax.swing.JCheckBox chkHasBadgeCondition;
    private javax.swing.JCheckBox chkHasCollectItem1;
    private javax.swing.JCheckBox chkHasCollectItem2;
    private javax.swing.JCheckBox chkHasCollectItem3;
    private javax.swing.JCheckBox chkHasDotKinopio;
    private javax.swing.Box.Filler filLevelInfo;
    private javax.swing.Box.Filler filLevelInfo1;
    private javax.swing.Box.Filler filLevelInfo2;
    private javax.swing.JLabel lblChallengeTime;
    private javax.swing.JLabel lblClearTime;
    private javax.swing.JLabel lblCoinNum;
    private javax.swing.JLabel lblLastGameSavedTime;
    private javax.swing.JLabel lblLastSavedTime;
    private javax.swing.JLabel lblLevelPreview;
    private javax.swing.JLabel lblNumOneUps;
    private javax.swing.JLabel lblUnkC;
    private javax.swing.JMenuBar mnbMain;
    private javax.swing.JMenuItem mniAbout;
    private javax.swing.JMenuItem mniExit;
    private javax.swing.JMenuItem mniNew;
    private javax.swing.JMenuItem mniOpen;
    private javax.swing.JMenuItem mniSave;
    private javax.swing.JMenuItem mniSaveAs;
    private javax.swing.JMenu mnuFile;
    private javax.swing.JMenu mnuHelp;
    private javax.swing.JMenu mnuLanguage;
    private javax.swing.JPopupMenu.Separator mspFile;
    private javax.swing.JPanel pnlCollectItem;
    private javax.swing.JPanel pnlLevelInfo;
    private javax.swing.JPanel pnlLevelPreview;
    private javax.swing.JPanel pnlMisc;
    private javax.swing.JPanel pnlStages;
    private javax.swing.JRadioButtonMenuItem radEnglishUk;
    private javax.swing.JRadioButtonMenuItem radEnglishUs;
    private javax.swing.JRadioButtonMenuItem radGerman;
    private javax.swing.ButtonGroup rdgLanguage;
    private javax.swing.JScrollPane scrTreeStages;
    private javax.swing.JSpinner spnClearTime;
    private javax.swing.JSpinner spnCoinNum;
    private javax.swing.JSpinner spnLastGameSavedTime;
    private javax.swing.JSpinner spnLastSavedTime;
    private javax.swing.JSpinner spnNumOneUps;
    private javax.swing.JSpinner spnUnkC;
    private javax.swing.JTabbedPane tabAll;
    private javax.swing.JTree treeStages;
    // End of variables declaration//GEN-END:variables
}
