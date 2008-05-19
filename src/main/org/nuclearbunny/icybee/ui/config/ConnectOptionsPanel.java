/*
 * $Id$
 *
 * IcyBee - http://www.nuclearbunny.org/icybee/
 * A client for the Internet CB Network - http://www.icb.net/
 *
 * Copyright © 2000-2004 David C. Gibbons, dcg@nuclearbunny.org
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

import org.nuclearbunny.icybee.Echoback;
import org.nuclearbunny.icybee.ui.UIMessages;
import org.nuclearbunny.icybee.ui.util.*;

import javax.swing.*;
import javax.swing.border.Border;
import java.awt.*;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;

class ConnectOptionsPanel extends JPanel {
    private Border emptyBorder = BorderFactory.createEmptyBorder(5, 5, 5, 5);

    private JCheckBox connectOnStartup;
    private JCheckBox keepConnectionAlive;
    private JLabel keepAliveIntervalLabel;
    private JComboBox keepAliveInterval;

    private JRadioButton echobackNone;
    private JRadioButton echobackOn;
    private JRadioButton echobackVerboseServer;

    public ConnectOptionsPanel() {
        setupPanel();
    }

    public boolean isConnectOnStartupEnabled() {
        return connectOnStartup.isSelected();
    }

    public void setConnectOnStartup(boolean enabled) {
        connectOnStartup.setSelected(enabled);
    }

    public boolean isKeepConnectionAliveEnabled() {
        return keepConnectionAlive.isSelected();
    }

    public void setKeepConnectionAlive(boolean enabled) {
        keepConnectionAlive.setSelected(enabled);
        keepAliveIntervalLabel.setEnabled(enabled);
        keepAliveInterval.setEnabled(enabled);
    }

    public int getKeepConnectionAliveInterval() {
        return Integer.parseInt(keepAliveInterval.getSelectedItem().toString());
    }

    public void setKeepConnectionAliveInterval(int interval) {
        keepAliveInterval.setSelectedItem(Integer.toString(interval));
    }

    public int getEchoback() {
        if (echobackOn.isSelected()) {
            return Echoback.ECHOBACK_ON;
        } else if (echobackVerboseServer.isSelected()) {
            return Echoback.ECHOBACK_VERBOSE_SERVER;
        } else {
            return Echoback.ECHOBACK_NONE;
        }
    }

    public void setEchoback(int echoback) {
        switch (echoback) {
            case Echoback.ECHOBACK_ON:
                echobackOn.setSelected(true);
                break;
            case Echoback.ECHOBACK_VERBOSE_SERVER:
                echobackVerboseServer.setSelected(true);
                break;
            case Echoback.ECHOBACK_NONE:
            default:
                echobackNone.setSelected(true);
                break;
        }
    }

    private void setupPanel() {
        JPanel optionsPanel = createOptionsPanel();
        JPanel echobackPanel = createEchobackPanel();

        JPanel p1 = new JPanel(new BorderLayout());
        p1.setBorder(emptyBorder);
        p1.add(optionsPanel, BorderLayout.NORTH);

        JPanel p2 = new JPanel(new BorderLayout());
        p2.setBorder(emptyBorder);
        p2.add(echobackPanel, BorderLayout.NORTH);

        Box p3 = new Box(BoxLayout.Y_AXIS);
        p3.add(p1);
        p3.add(p2);

        setLayout(new BorderLayout());
        add(p3, BorderLayout.NORTH);
    }

    private JPanel createOptionsPanel() {
        connectOnStartup = new JCheckBoxHelper(UIMessages.messages, "properties.connect.options.autoconnect");
        keepConnectionAlive = new JCheckBoxHelper(UIMessages.messages, "properties.connect.options.sendkeepalives");
        keepConnectionAlive.addItemListener(new ItemListener() {
            public void itemStateChanged(ItemEvent e) {
                boolean enabled = (e.getStateChange() == ItemEvent.SELECTED);
                keepAliveIntervalLabel.setEnabled(enabled);
                keepAliveInterval.setEnabled(enabled);
            }
        });

        String[] intervalExamples = {"300", "900", "1800", "3600"}; // TODO: do these belong in the resource bundle?
        keepAliveInterval = new JComboBoxHelper(UIMessages.messages, "properties.connect.options.keepalive", intervalExamples);
        keepAliveInterval.setEditable(true);
        keepAliveIntervalLabel = ((JComboBoxHelper) keepAliveInterval).getDefaultLabel();

        JPanel optionsPanel = new JPanel(new GridLayout(4, 0));
        optionsPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createTitledBorder(UIMessages.PROPERTIES_CONNECT_OPTIONS_TITLE), emptyBorder));

        JPanel p1 = new JPanel(new GridLayout(0, 2));
        p1.add(keepAliveIntervalLabel);
        p1.add(keepAliveInterval);

        optionsPanel.add(connectOnStartup);
        optionsPanel.add(new JLabel());
        optionsPanel.add(keepConnectionAlive);
        optionsPanel.add(p1);

        return optionsPanel;
    }

    private JPanel createEchobackPanel() {
        echobackNone = new JRadioButtonHelper(UIMessages.messages, "properties.echoback.none");
        echobackOn = new JRadioButtonHelper(UIMessages.messages, "properties.echoback.public");
        echobackVerboseServer = new JRadioButtonHelper(UIMessages.messages, "properties.echoback.all");

        ButtonGroup group = new ButtonGroup();
        group.add(echobackNone);
        group.add(echobackOn);
        group.add(echobackVerboseServer);

        JPanel ebp1 = new JPanel(new GridLayout(3, 0));
        ebp1.add(echobackNone);
        ebp1.add(echobackOn);
        ebp1.add(echobackVerboseServer);

        JPanel ebp2 = new JPanel(new BorderLayout());
        ebp2.add(ebp1, BorderLayout.NORTH);
        ebp2.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createTitledBorder(UIMessages.PROPERTIES_ECHOBACK_TITLE),
                ebp2.getBorder()));

        return ebp2;
    }
}
