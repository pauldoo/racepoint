/*
    Copyright (c) 2012 Paul Richards <paul.richards@gmail.com>

    Permission to use, copy, modify, and/or distribute this software for any
    purpose with or without fee is hereby granted, provided that the above
    copyright notice and this permission notice appear in all copies.

    THE SOFTWARE IS PROVIDED "AS IS" AND THE AUTHOR DISCLAIMS ALL WARRANTIES
    WITH REGARD TO THIS SOFTWARE INCLUDING ALL IMPLIED WARRANTIES OF
    MERCHANTABILITY AND FITNESS. IN NO EVENT SHALL THE AUTHOR BE LIABLE FOR
    ANY SPECIAL, DIRECT, INDIRECT, OR CONSEQUENTIAL DAMAGES OR ANY DAMAGES
    WHATSOEVER RESULTING FROM LOSS OF USE, DATA OR PROFITS, WHETHER IN AN
    ACTION OF CONTRACT, NEGLIGENCE OR OTHER TORTIOUS ACTION, ARISING OUT OF
    OR IN CONNECTION WITH THE USE OR PERFORMANCE OF THIS SOFTWARE.
*/
package pigeon.view;

import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.SortedSet;
import javax.swing.JCheckBox;
import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;
import pigeon.model.ValidationException;

/**
 @author pauldoo
*/
public class MultipleCheckBoxesPanel<T extends Comparable<T>> extends javax.swing.JPanel {
    private static final long serialVersionUID = 4428293282361765217L;
    
    public static interface Creator<T> {
        public T createFromString(String value) throws ValidationException;

        public String friendlyName();
    }

    private final Map<T, JCheckBox> checkBoxes;
    private final Creator<T> creator;
        
    /**
     Creates new form MultiSelectionComboBoxesPanel
     */
    public MultipleCheckBoxesPanel(SortedSet<T> available, Set<T> selected, Creator<T> creator) {
        initComponents();

        this.addButton.setText(addButton.getText() + " " + creator.friendlyName());
        this.creator = creator;
        checkBoxes = new HashMap<T, JCheckBox>();
        for (T t: available) {
            JCheckBox box = addSingleCheckBox(t);
            box.setSelected(selected.contains(t));
        }
    }

    private JCheckBox addSingleCheckBox(T t) {
        JCheckBox box = new JCheckBox(t.toString());
        checkBoxes.put(t, box);
        checkBoxPanel.add(box);
        // TODO: Repainting needed for WinXP + Java 1.7.0_05 (others too?)
        checkBoxPanel.revalidate();
        return box;
    }

    /**
     This method is called from within the constructor to initialize the form.
     WARNING: Do NOT modify this code. The content of this method is always
     regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        addButton = new javax.swing.JButton();
        checkBoxPanel = new javax.swing.JPanel();

        setLayout(new java.awt.BorderLayout());

        addButton.setText("Add");
        addButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                addButtonActionPerformed(evt);
            }
        });
        add(addButton, java.awt.BorderLayout.SOUTH);

        checkBoxPanel.setLayout(new javax.swing.BoxLayout(checkBoxPanel, javax.swing.BoxLayout.Y_AXIS));
        add(checkBoxPanel, java.awt.BorderLayout.CENTER);
    }// </editor-fold>//GEN-END:initComponents

    private void addButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_addButtonActionPerformed
        final String friendlyName = creator.friendlyName();
        final String message = String.format("Name for %s", friendlyName);
        final String title = String.format("Add %s", friendlyName);
        String valueAsString = JOptionPane.showInputDialog(this, message, title, JOptionPane.QUESTION_MESSAGE,null,null, "").toString();
        if (valueAsString != null) {
            try {
                T newValue = creator.createFromString(valueAsString);
                if (checkBoxes.containsKey(newValue) == false) {
                    addSingleCheckBox(newValue);
                }
                checkBoxes.get(newValue).setSelected(true);
            } catch (ValidationException e) {
                e.displayErrorDialog(this);
            }
        }
    }//GEN-LAST:event_addButtonActionPerformed

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton addButton;
    private javax.swing.JPanel checkBoxPanel;
    // End of variables declaration//GEN-END:variables

    public Set<T> getSelected() {
        Set<T> result = new HashSet<T>();
        for (Map.Entry<T, JCheckBox> pair: checkBoxes.entrySet()) {
            if (pair.getValue().isSelected()) {
                result.add(pair.getKey());
            }
        }
        return Collections.unmodifiableSet(result);
    }

}
