/*
 * IcyBee - http://www.nuclearbunny.org/icybee/
 * A client for the Internet CB Network - http://www.icb.net/
 *
 * Copyright (C) 2000-2009 David C. Gibbons
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
import org.nuclearbunny.icybee.ui.util.JCheckBoxHelper;
import org.nuclearbunny.icybee.ui.util.JComboBoxHelper;

import javax.swing.*;
import javax.swing.border.Border;
import java.awt.*;

class AdvancedPanel extends JPanel {
    private final static Border EMPTY_BORDER = BorderFactory.createEmptyBorder(5, 5, 5, 5);

    private JCheckBox personalWindowsBox;
    private JCheckBox incomingHistoryBox;
    private JCheckBox outgoingHistoryBox;

    private JComboBox encodingComboBox;

    public AdvancedPanel() {
        super();

        personalWindowsBox = new JCheckBoxHelper(UIMessages.messages, "properties.personals.show.personals.in.window");
        incomingHistoryBox = new JCheckBoxHelper(UIMessages.messages, "properties.personals.add.incoming.to.history");
        outgoingHistoryBox = new JCheckBoxHelper(UIMessages.messages, "properties.personals.add.outgoing.to.history");

        JPanel personalsPanel = new JPanel(new GridLayout(3, 0));
        personalsPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createTitledBorder(UIMessages.PROPERTIES_PERSONALS_TITLE), EMPTY_BORDER));
        personalsPanel.add(personalWindowsBox);
        personalsPanel.add(incomingHistoryBox);
        personalsPanel.add(outgoingHistoryBox);

        String[] encodings = {
                UIMessages.PROPERTIES_ENCODING_US_ASCII,
                UIMessages.PROPERTIES_ENCODING_UTF7
        };
        encodingComboBox = new JComboBoxHelper(UIMessages.messages, "properties.encoding.encodings", encodings);
        JLabel encodingsLabel = ((JComboBoxHelper) encodingComboBox).getDefaultLabel();

        JPanel encodingPanel = new JPanel(new GridLayout(2, 0));
        encodingPanel.setBorder(EMPTY_BORDER);
        encodingPanel.add(encodingsLabel);
        encodingPanel.add(encodingComboBox);

        Box boxPanel = new Box(BoxLayout.Y_AXIS);
        boxPanel.add(personalsPanel);
        boxPanel.add(encodingPanel);

        setLayout(new BorderLayout());
        add(boxPanel, BorderLayout.NORTH);
    }

    public boolean arePersonalWindowsEnabled() {
        return personalWindowsBox.isSelected();
    }

    public void setPersonalWindowsEnabled(boolean enabled) {
        personalWindowsBox.setSelected(enabled);
    }

    public boolean isAddIncomingToHistoryEnabled() {
        return incomingHistoryBox.isSelected();
    }

    public void setAddIncomingToHistoryEnabled(boolean enabled) {
        incomingHistoryBox.setSelected(enabled);
    }

    public boolean isAddOutgoingToHistoryEnabled() {
        return outgoingHistoryBox.isSelected();
    }

    public void setAddOutgoingToHistoryEnabled(boolean enabled) {
        outgoingHistoryBox.setSelected(enabled);
    }

    public String getTextEncoding() {
        return encodingComboBox.getSelectedItem().toString();
    }

    public void setTextEncoding(String encoding) {
        for (int i = 0, n = encodingComboBox.getMaximumRowCount(); i < n; i++) {
            if (encodingComboBox.getItemAt(i).toString().equals(encoding)) {
                encodingComboBox.setSelectedIndex(i);
                break;
            }
        }
    }
}
