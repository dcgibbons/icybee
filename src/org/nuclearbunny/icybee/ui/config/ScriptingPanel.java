/*
 * $Id$
 *
 * IcyBee - http://www.nuclearbunny.org/icybee/
 * A client for the Internet CB Network - http://www.icb.net/
 *
 * Copyright © 2000-2003 David C. Gibbons, dcg@nuclearbunny.org
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.
 */

package org.nuclearbunny.icybee.ui.config;

import org.nuclearbunny.icybee.ui.UIMessages;
import org.nuclearbunny.icybee.ui.util.*;

import javax.swing.*;
import javax.swing.border.Border;
import java.awt.*;
import java.awt.event.*;

class ScriptingPanel extends JPanel {
    private final static Border EMPTY_BORDER = BorderFactory.createEmptyBorder(5, 5, 5, 5);

    private JLabel initScriptLabel;
    private JCheckBox executeInitScriptBox;
    private JTextField initScriptField;
    private JButton browseButton;

    public ScriptingPanel() {
        super();

        executeInitScriptBox = new JCheckBoxHelper(UIMessages.messages, "properties.scripting.execute.startup.script");
        executeInitScriptBox.addItemListener(new ItemListener() {
            public void itemStateChanged(ItemEvent e) {
                boolean enabled = (e.getStateChange() == ItemEvent.SELECTED);
                initScriptLabel.setEnabled(enabled);
                initScriptField.setEnabled(enabled);
                browseButton.setEnabled(enabled);
            }
        });

        initScriptField = new JTextFieldHelper(UIMessages.messages, "properties.scripting.init.script");
        initScriptLabel = ((JTextFieldHelper) initScriptField).getDefaultLabel();

        JPanel labelPanel = new JPanel(new GridLayout(2, 0));
        labelPanel.setBorder(EMPTY_BORDER);
        labelPanel.add(new JLabel());
        labelPanel.add(initScriptLabel);

        JPanel fieldPanel = new JPanel(new GridLayout(2, 0));
        fieldPanel.setBorder(EMPTY_BORDER);
        fieldPanel.add(executeInitScriptBox);

        fieldPanel.add(initScriptField);

        JPanel layoutPanel1 = new JPanel(new BorderLayout());
        layoutPanel1.setBorder(EMPTY_BORDER);
        layoutPanel1.add(labelPanel, BorderLayout.WEST);
        layoutPanel1.add(fieldPanel, BorderLayout.CENTER);

        browseButton = new JButton(new BrowseAction());

        JPanel buttonP1 = new JPanel(new GridLayout(1, 0, 5, 5));
        buttonP1.add(browseButton);
        JPanel buttonP2 = new JPanel(new BorderLayout());
        buttonP2.add(buttonP1, BorderLayout.EAST);
        buttonP2.setBorder(EMPTY_BORDER);
        JPanel buttonP3 = new JPanel(new BorderLayout());
        buttonP3.add(buttonP2, BorderLayout.SOUTH);

        Box layoutPanel2 = new Box(BoxLayout.Y_AXIS);
        layoutPanel2.add(layoutPanel1);
        layoutPanel2.add(buttonP3);

        JPanel layoutPanel3 = new JPanel(new BorderLayout());
        layoutPanel3.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createTitledBorder(UIMessages.PROPERTIES_SCRIPTING_TITLE), layoutPanel3.getBorder()));
        layoutPanel3.add(layoutPanel2);

        setLayout(new BorderLayout());
        add(layoutPanel3, BorderLayout.NORTH);
    }

    public void setExecuteInitScriptEnabled(boolean enabled) {
        executeInitScriptBox.setSelected(enabled);
        initScriptLabel.setEnabled(enabled);
        initScriptField.setEnabled(enabled);
        browseButton.setEnabled(enabled);
    }

    public boolean isExecuteInitScriptEnabled() {
        return executeInitScriptBox.isSelected();
    }

    public void setInitScript(String initScript) {
        initScriptField.setText(initScript);
    }

    public String getInitScript() {
        return initScriptField.getText();
    }

    private class BrowseAction extends ActionHelper {
        public BrowseAction() {
            super(UIMessages.messages, "properties.scripting.browse.button");
        }

        public void actionPerformed(ActionEvent e) {
            Cursor savedCursor = getCursor();
            Cursor c = Cursor.getPredefinedCursor(Cursor.HAND_CURSOR);
            setCursor(c);

            JFileChooser chooser = new JFileChooser();
            int returnVal = chooser.showDialog(ScriptingPanel.this, UIMessages.PROPERTIES_SCRIPTING_SELECT_SCRIPT_TITLE);
            if (returnVal == JFileChooser.APPROVE_OPTION) {
                initScriptField.setText(chooser.getSelectedFile().getAbsolutePath());
            }

            setCursor(savedCursor);
        }
    }
}
