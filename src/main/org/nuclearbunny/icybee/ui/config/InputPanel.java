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
import org.nuclearbunny.icybee.ui.util.JTextFieldHelper;

import javax.swing.*;
import javax.swing.border.Border;
import java.awt.*;

class InputPanel extends JPanel {
    private static final Border EMPTY_BORDER = BorderFactory.createEmptyBorder(5, 5, 5, 5);

    // Server List group variables
    private JTextField commandPrefixField;

    public InputPanel() {
        super();

        commandPrefixField = new JTextFieldHelper(UIMessages.messages, "properties.input.command.prefix", 1);
        JLabel commandPrefixLabel = ((JTextFieldHelper) commandPrefixField).getDefaultLabel();

        JPanel labelPanel = new JPanel(new GridLayout(1, 0));
        labelPanel.setBorder(EMPTY_BORDER);
        labelPanel.add(commandPrefixLabel);

        JPanel fieldPanel = new JPanel(new GridLayout(1, 0));
        fieldPanel.setBorder(EMPTY_BORDER);
        fieldPanel.add(commandPrefixField);

        JPanel layoutPanel1 = new JPanel(new BorderLayout());
        layoutPanel1.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createTitledBorder(UIMessages.PROPERTIES_INPUT_TITLE), EMPTY_BORDER));
        layoutPanel1.add(labelPanel, BorderLayout.WEST);
        layoutPanel1.add(fieldPanel, BorderLayout.CENTER);

        setLayout(new BorderLayout());
        add(layoutPanel1, BorderLayout.NORTH);
    }

    public void setCommandPrefix(String commandPrefix) {
        commandPrefixField.setText(commandPrefix);
    }

    public String getCommandPrefix() {
        String commandPrefix = commandPrefixField.getText();
        if (commandPrefix.length() > 0) {
            return commandPrefix.substring(0, 1);
        } else {
            return "/"; // XXX kind of a hack :)
        }
    }
}
