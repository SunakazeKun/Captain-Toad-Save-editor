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

import com.aurumsmods.ctse.format.StageNode;
import com.aurumsmods.ajul.util.SwingUtil;
import com.aurumsmods.ctse.CTSe;
import com.aurumsmods.ctse.Localization;
import com.aurumsmods.ctse.format.KinopioSaveData;
import com.aurumsmods.ctse.format.KinopioSaveException;
import java.awt.Component;
import java.io.File;
import java.io.IOException;
import java.util.Collections;
import java.util.Map.Entry;
import java.util.prefs.Preferences;
import javax.swing.ImageIcon;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JRadioButtonMenuItem;
import javax.swing.JTree;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.tree.DefaultTreeCellRenderer;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreeNode;
import javax.swing.tree.TreePath;
import javax.swing.tree.TreeSelectionModel;

/**
 * @author Aurum
 */
public class KinopioSaveEditor extends javax.swing.JFrame {
    private static final ImageIcon[] NODE_ICONS = {
        CTSe.ASSET_LOADER.readIcon("/assets/img/node_default.png"),
        CTSe.ASSET_LOADER.readIcon("/assets/img/node_season.png"),
        CTSe.ASSET_LOADER.readIcon("/assets/img/node_chapter.png"),
        CTSe.ASSET_LOADER.readIcon("/assets/img/node_star.png"),
        CTSe.ASSET_LOADER.readIcon("/assets/img/node_shine.png"),
        CTSe.ASSET_LOADER.readIcon("/assets/img/node_crown.png"),
        CTSe.ASSET_LOADER.readIcon("/assets/img/node_toad_brigade.png"),
        CTSe.ASSET_LOADER.readIcon("/assets/img/node_ghost_player.png"),
        CTSe.ASSET_LOADER.readIcon("/assets/img/node_ghost_player_maze.png"),
        CTSe.ASSET_LOADER.readIcon("/assets/img/node_bonus.png")
    };
    
    static final class StageNodeTreeRenderer extends DefaultTreeCellRenderer {
        StageNodeTreeRenderer() {
            super();
        }
        
        @Override
        public Component getTreeCellRendererComponent(JTree tree, Object value, boolean selected, boolean expanded,
                boolean leaf, int row, boolean hasFocus)
        {
            super.getTreeCellRendererComponent(tree, value, selected, expanded, leaf, row, hasFocus);
            
            if (value != null && value instanceof StageNode) {
                ImageIcon icon = NODE_ICONS[((StageNode)value).getIconId()];
                setIcon(icon);
            }
            else
                setIcon(NODE_ICONS[0]);
            
            return this;
        }
    }
    
    // -------------------------------------------------------------------------------------------------------------------------
    
    final GameDataWidget gameDataWidget;
    final CourseInfoWidget courseInfoWidget;
    final SeasonDataWidget seasonDataWidget;
    StageNode selectedNode;
    JPanel currentWidget;
    
    final KinopioSaveData saveData;
    File saveFile;
    boolean saveChanges;

    /**
     * Creates new form KinopioSaveEditor
     */
    public KinopioSaveEditor() {
        initComponents();
        
        // Initialize save data
        saveData = new KinopioSaveData();
        saveFile = null;
        saveChanges = false;
        
        // Initialize additional components
        gameDataWidget = new GameDataWidget(this);
        courseInfoWidget = new CourseInfoWidget(this);
        seasonDataWidget = new SeasonDataWidget(this);
        scrGameData.setViewportView(gameDataWidget);
        scrGameData.getVerticalScrollBar().setUnitIncrement(8);
        scrSeasonAndCourse.getVerticalScrollBar().setUnitIncrement(8);
        
        treeStageNodes.getSelectionModel().setSelectionMode(TreeSelectionModel.SINGLE_TREE_SELECTION);
        selectedNode = null;
        currentWidget = null;
        
        // Final preparations
        initLocalizationButtons();
        localizeAll();
        
        newSaveDataFile();
    }
    
    // -------------------------------------------------------------------------------------------------------------------------
    // General-purpose helpers
    
    void needSaveChanges() {
        saveChanges = true;
    }
    
    private void showMessageDialog(int dialogType, String localid) {
        JOptionPane.showMessageDialog(this, Localization.getLocalization().getText(localid), CTSe.TITLE, dialogType);
    }
    
    private int showConfirmDialog(int dialogType, String localid) {
        return JOptionPane.showConfirmDialog(this, Localization.getLocalization().getText(localid), CTSe.TITLE, dialogType);
    }
    
    // -------------------------------------------------------------------------------------------------------------------------
    // Localization handling
    
    private void initLocalizationButtons() {
        for (Entry<String, Object> entry : Localization.getLocalizations().entrySet()) {
            String id = entry.getKey();
            boolean isCurrentLanguage = id.equals(Localization.getLocalizationId());
            JRadioButtonMenuItem btn = new JRadioButtonMenuItem(entry.getValue().toString(), isCurrentLanguage);
            
            rdgLanguage.add(btn);
            mnuLanguage.add(btn);
            
            btn.addActionListener((java.awt.event.ActionEvent evt) -> {
                changeLocalization(id);
            });
        }
    }
    
    private void changeLocalization(String langid) {
        if (!Localization.getLocalizationId().equals(langid)) {
            Localization.setLocalization(langid);
            localizeAll();
            
            // Preserve selected tree path
            TreePath path = treeStageNodes.getSelectionPath();
            ((DefaultTreeModel)treeStageNodes.getModel()).reload();
            expandTree(StageNode.getRootNode());
            treeStageNodes.setSelectionPath(path);
        }
    }
    
    private void localizeAll() {
        Localization localization = Localization.getLocalization();
        mnuFile.setText(localization.getText("editor.menu.file"));
        mniNew.setText(localization.getText("editor.menu.file.new"));
        mniOpen.setText(localization.getText("editor.menu.file.open"));
        mniSave.setText(localization.getText("editor.menu.file.save"));
        mniSaveAs.setText(localization.getText("editor.menu.file.save_as"));
        mniExit.setText(localization.getText("editor.menu.file.exit"));
        mnuLanguage.setText(localization.getText("editor.menu.language"));
        mnuHelp.setText(localization.getText("editor.menu.help"));
        mniAbout.setText(localization.getText("editor.menu.help.about"));
        
        tabMain.setTitleAt(0, localization.getText("editor.scrSeasonAndCourse.title"));
        tabMain.setTitleAt(1, localization.getText("editor.scrGameData.title"));
        
        gameDataWidget.localizeAll();
        courseInfoWidget.localizeAll();
        seasonDataWidget.localizeAll();
    }
    
    // -------------------------------------------------------------------------------------------------------------------------
    // StageNode handling
    
    private void reloadTree() {
        StageNode.growTree(saveData.getGameVersion());
        clearSelectionAndExpandTree();
    }
    
    private void expandTree(StageNode node) {
        for (TreeNode n : Collections.list(node.children()))
            expandTree((StageNode)n);
        
        if (node.isRoot())
            return;
        
        treeStageNodes.expandPath(new TreePath(node.getPath()));
    }
    
    private void clearSelectionAndExpandTree() {
        treeStageNodes.setSelectionPath(null);
        ((DefaultTreeModel)treeStageNodes.getModel()).reload();
        expandTree(StageNode.getRootNode());
    }
    
    private void handleChangedSelectedStageNode() {
        selectedNode = (StageNode)treeStageNodes.getLastSelectedPathComponent();
        
        if (currentWidget != null)
            scrSeasonAndCourse.getViewport().remove(currentWidget);
        
        courseInfoWidget.blockInput = true;
        courseInfoWidget.courseInfo = null;
        seasonDataWidget.blockInput = true;
        seasonDataWidget.seasonData = null;
        
        if (selectedNode != null) {
            if (selectedNode.getCourseId() != -1) {
                scrSeasonAndCourse.setViewportView(courseInfoWidget);
                courseInfoWidget.courseInfo = saveData.getCourseInfo(selectedNode.getCourseId());
                courseInfoWidget.reloadData();
                
                currentWidget = courseInfoWidget;
            }
            else if (selectedNode.isSeason()) {
                scrSeasonAndCourse.setViewportView(seasonDataWidget);
                seasonDataWidget.seasonData = saveData.getSeasonData(selectedNode.getSeasonId() - 1);
                seasonDataWidget.reloadData();
                
                currentWidget = seasonDataWidget;
            }
        }
        
        scrSeasonAndCourse.repaint();
    }
    
    // -------------------------------------------------------------------------------------------------------------------------
    // Save file creation, loading and saving
    
    private void reloadData() {
        gameDataWidget.reloadData();
        saveChanges = false;
        reloadTree();
    }
    
    private void newSaveDataFile() {
        saveFile = null;
        saveData.init();
        reloadData();
    }
    
    private void selectLoadSaveDataFile() {
        final JFileChooser fc = new JFileChooser();
        fc.setDialogTitle(Localization.getLocalization().getText("editor.file_chooser.open"));
        fc.setFileFilter(new FileNameExtensionFilter("GameData.bin (*.bin)", "bin"));
        fc.setSelectedFile(new File(Preferences.userRoot().get("ctse.lastFile", "")));
        
        if (fc.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
            saveFile = fc.getSelectedFile();
            
            if (saveFile.isFile()) {
                Preferences.userRoot().put("ctse.lastFile", saveFile.getPath());
                loadSaveDataFile();
            }
            else
                saveFile = null;
        }
    }
    
    private void loadSaveDataFile() {
        try {
            saveData.read(saveFile);
        }
        catch(IOException | KinopioSaveException ex) {
            SwingUtil.showExceptionBox(this, ex, CTSe.TITLE);
            saveFile = null;
        }
        
        if (saveFile != null) {
            if (saveData.getGameVersion() < KinopioSaveData.VERSION_SWITCH_VR) {
                int result = showConfirmDialog(JOptionPane.YES_NO_OPTION, "editor.message.outdated_saveformat");
                
                if (result == JOptionPane.YES_OPTION) {
                    saveData.updateVersionToSwitchVR();
                    showMessageDialog(JOptionPane.INFORMATION_MESSAGE, "editor.message.updated_saveformat");
                }
            }
            
            reloadData();
        }
    }
    
    private void selectWriteSaveDataFile() {
        final JFileChooser fc = new JFileChooser();
        fc.setDialogTitle(Localization.getLocalization().getText("editor.file_chooser.save"));
        fc.setFileFilter(new FileNameExtensionFilter("GameData.bin (*.bin)", "bin"));
        fc.setSelectedFile(new File(Preferences.userRoot().get("ctse.lastFile", "")));
        
        if (fc.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
            saveFile = fc.getSelectedFile();
            Preferences.userRoot().put("ctse.lastFile", saveFile.getPath());
        }
    }
    
    private void writeSaveDataFile() {
        if (saveFile != null) {
            try {
                saveData.write(saveFile);
                saveChanges = false;
            }
            catch (IOException ex) {
                SwingUtil.showExceptionBox(this, ex, CTSe.TITLE);
            }
        }
    }
    
    private boolean dropChanges() {
        if (saveChanges && saveData != null)
            return showConfirmDialog(JOptionPane.YES_NO_OPTION, "editor.message.already_editing") == JOptionPane.YES_OPTION;
        return true;
    }
    
    // -------------------------------------------------------------------------------------------------------------------------

    /**
     * This method is called from within the constructor to initialize the form. WARNING: Do NOT modify this code. The content
     * of this method is always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        rdgLanguage = new javax.swing.ButtonGroup();
        tabMain = new javax.swing.JTabbedPane();
        splitSeasonAndCourse = new javax.swing.JSplitPane();
        scrStageNodes = new javax.swing.JScrollPane();
        treeStageNodes = new javax.swing.JTree();
        scrSeasonAndCourse = new javax.swing.JScrollPane();
        scrGameData = new javax.swing.JScrollPane();
        jMenuBar1 = new javax.swing.JMenuBar();
        mnuFile = new javax.swing.JMenu();
        mniNew = new javax.swing.JMenuItem();
        mniOpen = new javax.swing.JMenuItem();
        mniSave = new javax.swing.JMenuItem();
        mniSaveAs = new javax.swing.JMenuItem();
        mniExit = new javax.swing.JMenuItem();
        mnuLanguage = new javax.swing.JMenu();
        mnuHelp = new javax.swing.JMenu();
        mniAbout = new javax.swing.JMenuItem();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle(CTSe.FULL_TITLE);
        setIconImage(CTSe.PROGRAM_ICON);
        setResizable(false);
        addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowClosing(java.awt.event.WindowEvent evt) {
                formWindowClosing(evt);
            }
        });

        splitSeasonAndCourse.setDividerLocation(300);

        treeStageNodes.setModel(new DefaultTreeModel(StageNode.getRootNode()));
        treeStageNodes.setCellRenderer(new StageNodeTreeRenderer());
        treeStageNodes.setRootVisible(false);
        treeStageNodes.setRowHeight(24);
        treeStageNodes.addTreeSelectionListener(new javax.swing.event.TreeSelectionListener() {
            public void valueChanged(javax.swing.event.TreeSelectionEvent evt) {
                treeStageNodesValueChanged(evt);
            }
        });
        scrStageNodes.setViewportView(treeStageNodes);

        splitSeasonAndCourse.setLeftComponent(scrStageNodes);
        splitSeasonAndCourse.setRightComponent(scrSeasonAndCourse);

        tabMain.addTab("Episodes & levels", splitSeasonAndCourse);
        tabMain.addTab("Game data", scrGameData);

        mnuFile.setMnemonic('F');
        mnuFile.setText("File");

        mniNew.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_N, java.awt.event.InputEvent.CTRL_DOWN_MASK));
        mniNew.setMnemonic('N');
        mniNew.setText("New");
        mniNew.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mniNewActionPerformed(evt);
            }
        });
        mnuFile.add(mniNew);

        mniOpen.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_O, java.awt.event.InputEvent.CTRL_DOWN_MASK));
        mniOpen.setMnemonic('O');
        mniOpen.setText("Open");
        mniOpen.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mniOpenActionPerformed(evt);
            }
        });
        mnuFile.add(mniOpen);

        mniSave.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_S, java.awt.event.InputEvent.CTRL_DOWN_MASK));
        mniSave.setMnemonic('S');
        mniSave.setText("Save");
        mniSave.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mniSaveActionPerformed(evt);
            }
        });
        mnuFile.add(mniSave);

        mniSaveAs.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_S, java.awt.event.InputEvent.ALT_DOWN_MASK | java.awt.event.InputEvent.CTRL_DOWN_MASK));
        mniSaveAs.setMnemonic('A');
        mniSaveAs.setText("Save as");
        mniSaveAs.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mniSaveAsActionPerformed(evt);
            }
        });
        mnuFile.add(mniSaveAs);

        mniExit.setMnemonic('E');
        mniExit.setText("Exit");
        mniExit.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mniExitActionPerformed(evt);
            }
        });
        mnuFile.add(mniExit);

        jMenuBar1.add(mnuFile);

        mnuLanguage.setMnemonic('L');
        mnuLanguage.setText("Language");
        jMenuBar1.add(mnuLanguage);

        mnuHelp.setMnemonic('H');
        mnuHelp.setText("Help");

        mniAbout.setMnemonic('A');
        mniAbout.setText("About");
        mniAbout.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mniAboutActionPerformed(evt);
            }
        });
        mnuHelp.add(mniAbout);

        jMenuBar1.add(mnuHelp);

        setJMenuBar(jMenuBar1);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(tabMain, javax.swing.GroupLayout.DEFAULT_SIZE, 800, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(tabMain, javax.swing.GroupLayout.PREFERRED_SIZE, 560, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 0, Short.MAX_VALUE))
        );

        pack();
        setLocationRelativeTo(null);
    }// </editor-fold>//GEN-END:initComponents

    private void mniNewActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mniNewActionPerformed
        if (dropChanges())
            newSaveDataFile();
    }//GEN-LAST:event_mniNewActionPerformed

    private void mniOpenActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mniOpenActionPerformed
        if (dropChanges())
            selectLoadSaveDataFile();
    }//GEN-LAST:event_mniOpenActionPerformed

    private void mniSaveActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mniSaveActionPerformed
        if (saveFile == null)
            selectWriteSaveDataFile();
        writeSaveDataFile();
    }//GEN-LAST:event_mniSaveActionPerformed

    private void mniSaveAsActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mniSaveAsActionPerformed
        selectWriteSaveDataFile();
        writeSaveDataFile();
    }//GEN-LAST:event_mniSaveAsActionPerformed

    private void mniExitActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mniExitActionPerformed
        System.exit(0);
    }//GEN-LAST:event_mniExitActionPerformed

    private void mniAboutActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mniAboutActionPerformed
        showMessageDialog(JOptionPane.INFORMATION_MESSAGE, CTSe.FULL_TITLE);
    }//GEN-LAST:event_mniAboutActionPerformed

    private void treeStageNodesValueChanged(javax.swing.event.TreeSelectionEvent evt) {//GEN-FIRST:event_treeStageNodesValueChanged
        handleChangedSelectedStageNode();
    }//GEN-LAST:event_treeStageNodesValueChanged

    private void formWindowClosing(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_formWindowClosing
        CTSe.saveSettings();
    }//GEN-LAST:event_formWindowClosing

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JMenuBar jMenuBar1;
    private javax.swing.JMenuItem mniAbout;
    private javax.swing.JMenuItem mniExit;
    private javax.swing.JMenuItem mniNew;
    private javax.swing.JMenuItem mniOpen;
    private javax.swing.JMenuItem mniSave;
    private javax.swing.JMenuItem mniSaveAs;
    private javax.swing.JMenu mnuFile;
    private javax.swing.JMenu mnuHelp;
    private javax.swing.JMenu mnuLanguage;
    private javax.swing.ButtonGroup rdgLanguage;
    private javax.swing.JScrollPane scrGameData;
    private javax.swing.JScrollPane scrSeasonAndCourse;
    private javax.swing.JScrollPane scrStageNodes;
    private javax.swing.JSplitPane splitSeasonAndCourse;
    private javax.swing.JTabbedPane tabMain;
    private javax.swing.JTree treeStageNodes;
    // End of variables declaration//GEN-END:variables
}
