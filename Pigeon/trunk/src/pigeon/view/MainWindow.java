/*
 * MainWindow.java
 *
 * Created on 21 August 2005, 15:55
 */

package pigeon.view;

import java.awt.CardLayout;
import java.awt.Component;
import java.awt.Container;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectInputStream;
import java.io.ObjectOutput;
import java.io.ObjectOutputStream;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.UIManager;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.filechooser.FileFilter;
import pigeon.model.Member;
import pigeon.model.Race;
import pigeon.model.Racepoint;
import pigeon.model.Season;

/**
 *
 * @author  Paul
 */
class MainWindow extends javax.swing.JFrame implements ListSelectionListener {
    
    private static final long serialVersionUID = 42L;

    private Season season;
    
    /** Creates new form MainWindow */
    public MainWindow() {
        initComponents();
    }
    
    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc=" Generated Code ">//GEN-BEGIN:initComponents
    private void initComponents() {
        java.awt.GridBagConstraints gridBagConstraints;

        mainMenuPanel = new javax.swing.JPanel();
        loadSeasonButton = new javax.swing.JButton();
        newSeasonButton = new javax.swing.JButton();
        setupClubPanel = new javax.swing.JPanel();
        finishedButton = new javax.swing.JButton();
        clubPanel = new javax.swing.JPanel();
        clubNameLabel = new javax.swing.JLabel();
        clubNameText = new javax.swing.JTextField();
        membersPanel = new javax.swing.JPanel();
        memberListScrollPane = new javax.swing.JScrollPane();
        membersList = new javax.swing.JList();
        memberButtonPanel = new javax.swing.JPanel();
        memberAddButton = new javax.swing.JButton();
        memberEditButton = new javax.swing.JButton();
        memberDeleteButton = new javax.swing.JButton();
        racepointsPanel = new javax.swing.JPanel();
        racepointListScrollPane = new javax.swing.JScrollPane();
        racepointsList = new javax.swing.JList();
        racepointButtonPanel = new javax.swing.JPanel();
        racepointAddButton = new javax.swing.JButton();
        racepointEditButton = new javax.swing.JButton();
        racepointDeleteButton = new javax.swing.JButton();
        viewingSeason = new javax.swing.JPanel();
        raceresultPanel = new javax.swing.JPanel();
        raceresultListScrollPane = new javax.swing.JScrollPane();
        raceresultsTable = new javax.swing.JTable();
        raceresultButtonPanel = new javax.swing.JPanel();
        raceresultAddButton = new javax.swing.JButton();

        getContentPane().setLayout(new java.awt.CardLayout());

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setTitle("Pigeon");
        setLocationByPlatform(true);
        mainMenuPanel.setLayout(new java.awt.GridBagLayout());

        loadSeasonButton.setText("Load existing season");
        loadSeasonButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                loadSeasonButtonActionPerformed(evt);
            }
        });

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        mainMenuPanel.add(loadSeasonButton, gridBagConstraints);

        newSeasonButton.setText("Start new season");
        newSeasonButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                newSeasonButtonActionPerformed(evt);
            }
        });

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        mainMenuPanel.add(newSeasonButton, gridBagConstraints);

        getContentPane().add(mainMenuPanel, "mainMenu");

        setupClubPanel.setLayout(new java.awt.GridBagLayout());

        finishedButton.setText("Finished");
        finishedButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                finishedButtonActionPerformed(evt);
            }
        });

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        setupClubPanel.add(finishedButton, gridBagConstraints);

        clubPanel.setLayout(new java.awt.GridBagLayout());

        clubPanel.setBorder(new javax.swing.border.TitledBorder("Club Information"));
        clubNameLabel.setText("Club Name");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        clubPanel.add(clubNameLabel, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        clubPanel.add(clubNameText, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        setupClubPanel.add(clubPanel, gridBagConstraints);

        membersPanel.setLayout(new java.awt.BorderLayout());

        membersPanel.setBorder(new javax.swing.border.TitledBorder("Member Information"));
        membersList.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
        membersList.addListSelectionListener(this);
        memberListScrollPane.setViewportView(membersList);

        membersPanel.add(memberListScrollPane, java.awt.BorderLayout.CENTER);

        memberAddButton.setText("Add");
        memberAddButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                memberAddButtonActionPerformed(evt);
            }
        });

        memberButtonPanel.add(memberAddButton);

        memberEditButton.setText("Edit");
        memberEditButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                memberEditButtonActionPerformed(evt);
            }
        });

        memberButtonPanel.add(memberEditButton);

        memberDeleteButton.setText("Delete");
        memberDeleteButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                memberDeleteButtonActionPerformed(evt);
            }
        });

        memberButtonPanel.add(memberDeleteButton);

        membersPanel.add(memberButtonPanel, java.awt.BorderLayout.SOUTH);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        setupClubPanel.add(membersPanel, gridBagConstraints);

        racepointsPanel.setLayout(new java.awt.BorderLayout());

        racepointsPanel.setBorder(new javax.swing.border.TitledBorder("Racepoint Information"));
        racepointsList.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
        racepointsList.addListSelectionListener(this);
        racepointListScrollPane.setViewportView(racepointsList);

        racepointsPanel.add(racepointListScrollPane, java.awt.BorderLayout.CENTER);

        racepointAddButton.setText("Add");
        racepointAddButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                racepointAddButtonActionPerformed(evt);
            }
        });

        racepointButtonPanel.add(racepointAddButton);

        racepointEditButton.setText("Edit");
        racepointEditButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                racepointEditButtonActionPerformed(evt);
            }
        });

        racepointButtonPanel.add(racepointEditButton);

        racepointDeleteButton.setText("Delete");
        racepointDeleteButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                racepointDeleteButtonActionPerformed(evt);
            }
        });

        racepointButtonPanel.add(racepointDeleteButton);

        racepointsPanel.add(racepointButtonPanel, java.awt.BorderLayout.SOUTH);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        setupClubPanel.add(racepointsPanel, gridBagConstraints);

        getContentPane().add(setupClubPanel, "setupClub");

        viewingSeason.setLayout(new java.awt.GridBagLayout());

        raceresultPanel.setLayout(new java.awt.BorderLayout());

        raceresultPanel.setBorder(new javax.swing.border.TitledBorder("Race Results"));
        raceresultsTable.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null}
            },
            new String [] {
                "Title 1", "Title 2", "Title 3", "Title 4"
            }
        ));
        raceresultListScrollPane.setViewportView(raceresultsTable);

        raceresultPanel.add(raceresultListScrollPane, java.awt.BorderLayout.CENTER);

        raceresultAddButton.setText("Add");
        raceresultAddButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                raceresultAddButtonActionPerformed(evt);
            }
        });

        raceresultButtonPanel.add(raceresultAddButton);

        raceresultPanel.add(raceresultButtonPanel, java.awt.BorderLayout.SOUTH);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        viewingSeason.add(raceresultPanel, gridBagConstraints);

        getContentPane().add(viewingSeason, "viewingSeason");

        pack();
    }
    // </editor-fold>//GEN-END:initComponents

    private void raceresultAddButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_raceresultAddButtonActionPerformed
        Race race = RaceInfo.createRace(this, season.getClub());  
        season.addRace( race );
        //editResultsForRace( member );
        reloadRaceresultsTable();
    }//GEN-LAST:event_raceresultAddButtonActionPerformed

    private void loadSeasonButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_loadSeasonButtonActionPerformed
        promptLoadSeason();
    }//GEN-LAST:event_loadSeasonButtonActionPerformed

    private void finishedButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_finishedButtonActionPerformed
        String clubName = clubNameText.getText();
        season.getClub().setName( clubName );
        int memberCount = season.getClub().getNumberOfMembers();
        int racepointCount = season.getClub().getNumberOfRacepoints();
        String message =
                "Have you finished adding all of the members (currently " + memberCount + ") " +
                "and racepoints (currently " + racepointCount + ") for the club \"" + clubName + "\"?";
        int result = JOptionPane.showConfirmDialog(this, message, "Finishing club setup", JOptionPane.YES_NO_OPTION);
        switch (result) {
            case JOptionPane.YES_OPTION:
                promptSaveSeason();
                break;
            case JOptionPane.NO_OPTION:
                JOptionPane.showMessageDialog(this, "Please continue to add members and racepoints.");
                break;
            default:
                throw new IllegalStateException();
        }
    }//GEN-LAST:event_finishedButtonActionPerformed

    private void promptSaveSeason() {
        JFileChooser chooser = new JFileChooser();
        FileFilter filter = SimpleFileFilter.createSeasonFileFilter();
        chooser.addChoosableFileFilter(filter);
        chooser.setAcceptAllFileFilterUsed(false);
        int result = chooser.showSaveDialog(this);
        switch (result) {
            case JFileChooser.APPROVE_OPTION:
                File file = chooser.getSelectedFile();
                if (!file.getName().endsWith(".pcs")) {
                    file = new File(file.getParentFile(), file.getName() + ".pcs");
                }
                try {
                    writeSeasonToFile(file);
                    switchToCard("viewingSeason");
                } catch (FileNotFoundException e) {
                    JOptionPane.showMessageDialog(this, e.toString());
                } catch (IOException e) {
                    JOptionPane.showMessageDialog(this, e.toString());
                }
                break;
            case JFileChooser.CANCEL_OPTION:
                break;
            case JFileChooser.ERROR_OPTION:
            default:
                throw new IllegalStateException();
        }
    }

    private void promptLoadSeason() {
        JFileChooser chooser = new JFileChooser();
        FileFilter filter = SimpleFileFilter.createSeasonFileFilter();
        chooser.addChoosableFileFilter(filter);
        chooser.setAcceptAllFileFilterUsed(false);
        int result = chooser.showOpenDialog(this);
        switch (result) {
            case JFileChooser.APPROVE_OPTION:
                File file = chooser.getSelectedFile();
                try {
                    loadSeasonFromFile(file);
                    switchToCard("viewingSeason");
                } catch (FileNotFoundException e) {
                    JOptionPane.showMessageDialog(this, e.toString());
                } catch (IOException e) {
                    JOptionPane.showMessageDialog(this, e.toString());
                } catch (ClassNotFoundException e) {
                    JOptionPane.showMessageDialog(this, e.toString());
                }
                break;
            case JFileChooser.CANCEL_OPTION:
                break;
            case JFileChooser.ERROR_OPTION:
            default:
                throw new IllegalStateException();
        }
    }
    
    private void writeSeasonToFile(File file) throws FileNotFoundException, IOException {
        ObjectOutput out = new ObjectOutputStream(new BufferedOutputStream(new FileOutputStream(file)));
        out.writeObject(season);
        out.close();
    }

    private void loadSeasonFromFile(File file) throws FileNotFoundException, IOException, ClassNotFoundException {
        ObjectInput in = new ObjectInputStream(new BufferedInputStream(new FileInputStream(file)));
        Season loaded = (Season)in.readObject();
        in.close();
        setSeason( loaded );
    }
    
    
    private void racepointDeleteButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_racepointDeleteButtonActionPerformed
        Racepoint racepoint = (Racepoint)racepointsList.getSelectedValue();
        season.getClub().removeRacepoint( racepoint );
        reloadRacepointsList();
    }//GEN-LAST:event_racepointDeleteButtonActionPerformed

    private void memberDeleteButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_memberDeleteButtonActionPerformed
        Member member = (Member)membersList.getSelectedValue();
        season.getClub().removeMember( member );
        reloadMembersList();
    }//GEN-LAST:event_memberDeleteButtonActionPerformed

    private void racepointEditButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_racepointEditButtonActionPerformed
        int index = racepointsList.getSelectedIndex();
        Racepoint racepoint = Utilities.sortCollection( season.getClub().getRacepoints() ).get(index);
        String name = JOptionPane.showInputDialog(this, "Please enter a new name for \"" + racepoint + "\"", "Edit racepoint name", JOptionPane.QUESTION_MESSAGE);
        if (name != null) {
             name = name.trim();
             if (name.length() > 0) {
                 racepoint.setName( name );
                 editDistancesForRacepoint( racepoint );
                 reloadRacepointsList();
             } else {
                 JOptionPane.showMessageDialog(this, "You entered a blank name", "Blank name", JOptionPane.ERROR_MESSAGE);
             }
        }
    }//GEN-LAST:event_racepointEditButtonActionPerformed

    public void valueChanged(ListSelectionEvent event) {
        refreshButtons();
    }
    
    private void memberEditButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_memberEditButtonActionPerformed
        int index = membersList.getSelectedIndex();
        Member member = Utilities.sortCollection(season.getClub().getMembers()).get(index);
        MemberInfo.editMember(this, member);
        editDistancesForMember( member );
        reloadMembersList();
    }//GEN-LAST:event_memberEditButtonActionPerformed

    private void racepointAddButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_racepointAddButtonActionPerformed
         String name = JOptionPane.showInputDialog(this, "Please enter a name", "New racepoint", JOptionPane.QUESTION_MESSAGE);
         if (name != null) {
             name = name.trim();
             if (name.length() > 0) {
                 Racepoint racepoint = new Racepoint();
                 racepoint.setName(name);
                 season.getClub().addRacepoint( racepoint );
                 editDistancesForRacepoint( racepoint );
                 reloadRacepointsList();
             } else {
                 JOptionPane.showMessageDialog(this, "You entered a blank name", "Blank name", JOptionPane.ERROR_MESSAGE);
             }
         }
    }//GEN-LAST:event_racepointAddButtonActionPerformed

    private void memberAddButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_memberAddButtonActionPerformed
        Member member = MemberInfo.createMember(this);  
        season.getClub().addMember( member );
        editDistancesForMember( member );
        reloadMembersList();
    }//GEN-LAST:event_memberAddButtonActionPerformed

    private void reloadControlData() {
        clubNameText.setText(season.getClub().getName());
        reloadMembersList();
        reloadRacepointsList();
        reloadRaceresultsTable();
        refreshButtons();
    }

    private void reloadMembersList() {
        membersList.setListData(Utilities.sortCollection(season.getClub().getMembers()));
    }
    
    private void reloadRacepointsList() {
        racepointsList.setListData(Utilities.sortCollection(season.getClub().getRacepoints()));
    }
    
    private void reloadRaceresultsTable() {
        raceresultsTable.setModel(new RaceresultsTableModel(Utilities.sortCollection(season.getRaces())));
    }
    
    private void refreshButtons() {
        memberEditButton.setEnabled( membersList.getSelectedIndex() != -1 );
        memberDeleteButton.setEnabled( membersList.getSelectedIndex() != -1 );
        racepointEditButton.setEnabled( racepointsList.getSelectedIndex() != -1 );
        racepointDeleteButton.setEnabled( racepointsList.getSelectedIndex() != -1 );
    }
    
    private void switchToCard(String cardName) {
        Container parent = this.getContentPane();
        ((CardLayout)parent.getLayout()).show(parent, cardName);        
    }
    
    private void newSeasonButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_newSeasonButtonActionPerformed
        setSeason( new Season() );
        switchToCard("setupClub");
    }//GEN-LAST:event_newSeasonButtonActionPerformed
    
    private void editDistancesForMember(Member member) {
        Component parent = this.getContentPane();
        DistanceEditorPanel.editMemberDistances(parent, member, season.getClub());
    }

    private void editDistancesForRacepoint(Racepoint racepoint) {
        Component parent = this.getContentPane();
        DistanceEditorPanel.editRacepointDistances(parent, racepoint, season.getClub());
    }
    
    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        setSwingLAF();
        
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                MainWindow window = new MainWindow();
                window.setVisible(true);
            }
        });
    }

    public Season getSeason() {
        return season;
    }
    
    public void setSeason(Season season) {
        this.season = season;
        reloadControlData();
    }
    
    private static void setSwingLAF() {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) { }
    }
    
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLabel clubNameLabel;
    private javax.swing.JTextField clubNameText;
    private javax.swing.JPanel clubPanel;
    private javax.swing.JButton finishedButton;
    private javax.swing.JButton loadSeasonButton;
    private javax.swing.JPanel mainMenuPanel;
    private javax.swing.JButton memberAddButton;
    private javax.swing.JPanel memberButtonPanel;
    private javax.swing.JButton memberDeleteButton;
    private javax.swing.JButton memberEditButton;
    private javax.swing.JScrollPane memberListScrollPane;
    private javax.swing.JList membersList;
    private javax.swing.JPanel membersPanel;
    private javax.swing.JButton newSeasonButton;
    private javax.swing.JButton racepointAddButton;
    private javax.swing.JPanel racepointButtonPanel;
    private javax.swing.JButton racepointDeleteButton;
    private javax.swing.JButton racepointEditButton;
    private javax.swing.JScrollPane racepointListScrollPane;
    private javax.swing.JList racepointsList;
    private javax.swing.JPanel racepointsPanel;
    private javax.swing.JButton raceresultAddButton;
    private javax.swing.JPanel raceresultButtonPanel;
    private javax.swing.JScrollPane raceresultListScrollPane;
    private javax.swing.JPanel raceresultPanel;
    private javax.swing.JTable raceresultsTable;
    private javax.swing.JPanel setupClubPanel;
    private javax.swing.JPanel viewingSeason;
    // End of variables declaration//GEN-END:variables
    
}
