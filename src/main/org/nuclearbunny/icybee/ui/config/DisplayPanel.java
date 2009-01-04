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
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;

class DisplayPanel extends JPanel {
    private Border emptyBorder = BorderFactory.createEmptyBorder(5, 5, 5, 5);

    private static final BufferSize[] defaultSizeValues = {
            new BufferSize(UIMessages.PROPERTIES_DISPLAY_BUFFER_SIZE_1000_LINES, 1000),
            new BufferSize(UIMessages.PROPERTIES_DISPLAY_BUFFER_SIZE_1000_LINES, 2500),
            new BufferSize(UIMessages.PROPERTIES_DISPLAY_BUFFER_SIZE_1000_LINES, 5000),
            new BufferSize(UIMessages.PROPERTIES_DISPLAY_BUFFER_SIZE_1000_LINES, 10000)
    };

    // TODO: these values should be placed in the resource bundle, as well
    private static final String[] formatData = {
            "HH:mm",
            "HH:mm:ss",
            "hh:mm",
            "hh:mm:ss",
            "hh:mm aa",
            "hh:mm:ss aa"
    };

    private JPanel displayInfoPanel;
    private JCheckBox limitDisplaySizeBox;
    private JLabel sizeLimitLabel;
    private JComboBox sizeLimitComboBox;

    private JPanel emoticonsPanel;
    private JCheckBox emoticonsBox;
    private JCheckBox animatedEmoticonsBox;

    private JPanel timestampsPanel;
    private JCheckBox timestampsBox;
    private JComboBox timestampFormatList;

    public DisplayPanel() {
        setupPanel();

        // XXX not yet supported
        limitDisplaySizeBox.setEnabled(false);
        sizeLimitLabel.setEnabled(false);
        sizeLimitComboBox.setEnabled(false);
    }

    public boolean isLimitDisplaySizeEnabled() {
        return limitDisplaySizeBox.isSelected();
    }

    public void setLimitDisplaySizeEnabled(boolean enabled) {
        limitDisplaySizeBox.setSelected(enabled);
        sizeLimitLabel.setEnabled(enabled);
        sizeLimitComboBox.setEnabled(enabled);
    }

    public int getSizeLimit() {
        BufferSize size = (BufferSize) sizeLimitComboBox.getSelectedItem();
        return size.getSize();
    }

    public void setSizeLimit(int sizeLimit) {
        BufferSize size = null;
        for (int i = 0; i < defaultSizeValues.length; i++) {
            if (defaultSizeValues[i].getSize() == sizeLimit) {
                size = defaultSizeValues[i];
                break;
            }
        }

        if (size != null) {
            sizeLimitComboBox.setSelectedItem(size);
        } else {
            // XXX - handle this case
        }
    }

    public boolean areEmoticonsEnabled() {
        return emoticonsBox.isSelected();
    }

    public void setEmoticonsEnabled(boolean enabled) {
        emoticonsBox.setSelected(enabled);
    }

    public boolean areAnimatedEmoticonsEnabled() {
        return animatedEmoticonsBox.isSelected();
    }

    public void setAnimatedEmoticonsEnabled(boolean enabled) {
        animatedEmoticonsBox.setSelected(enabled);
    }

    public boolean areMessageTimestampsEnabled() {
        return timestampsBox.isSelected();
    }

    public void setMessageTimestampsEnabled(boolean enabled) {
        timestampsBox.setSelected(enabled);
    }

    public String getMessageTimestampsFormat() {
        return timestampFormatList.getSelectedItem().toString();
    }

    public void setMessageTimestampsFormat(String messageFormat) {
        for (int i = 0; i < formatData.length; i++) {
            if (formatData[i].equals(messageFormat)) {
                timestampFormatList.setSelectedItem(formatData[i]);
                break;
            }
        }
    }

    private void setupPanel() {
        displayInfoPanel = createDisplayInfoPanel();
        emoticonsPanel = createEmoticonsPanel();
        timestampsPanel = createTimestampsPanel();

        Box p1 = new Box(BoxLayout.Y_AXIS);
        p1.add(displayInfoPanel);
        p1.add(emoticonsPanel);
        p1.add(timestampsPanel);

        setLayout(new BorderLayout());
        add(p1, BorderLayout.NORTH);
    }

    private JPanel createDisplayInfoPanel() {
        limitDisplaySizeBox = new JCheckBoxHelper(UIMessages.messages, "properties.display.limitdisplaysize");
        limitDisplaySizeBox.addItemListener(new ItemListener() {
            public void itemStateChanged(ItemEvent e) {
                boolean enabled = (e.getStateChange() == ItemEvent.SELECTED);
                sizeLimitLabel.setEnabled(enabled);
                sizeLimitComboBox.setEnabled(enabled);
            }
        });

        sizeLimitComboBox = new JComboBoxHelper(UIMessages.messages, "properties.display.buffersize", defaultSizeValues);
        sizeLimitLabel = ((JComboBoxHelper) sizeLimitComboBox).getDefaultLabel();

        JPanel p2 = new JPanel(new GridLayout(0, 2));
        p2.add(sizeLimitLabel);
        p2.add(sizeLimitComboBox);

        JPanel p1 = new JPanel(new GridLayout(2, 0));
        p1.add(limitDisplaySizeBox);
        p1.add(p2);

        JPanel displayPanel = new JPanel(new BorderLayout());
        displayPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createTitledBorder(UIMessages.PROPERTIES_DISPLAY_BUFFER_TITLE), emptyBorder));
        displayPanel.add(p1, BorderLayout.NORTH);

        return displayPanel;
    }

    private JPanel createEmoticonsPanel() {
        emoticonsBox = new JCheckBoxHelper(UIMessages.messages, "properties.display.emoticons");
        emoticonsBox.addItemListener(new ItemListener() {
            public void itemStateChanged(ItemEvent e) {
                if (e.getStateChange() == ItemEvent.SELECTED) {
                    animatedEmoticonsBox.setEnabled(true);
                } else {
                    animatedEmoticonsBox.setEnabled(false);
                }
            }
        });

        animatedEmoticonsBox = new JCheckBoxHelper(UIMessages.messages, "properties.display.animated.emoticons");

        JPanel emoticonsPanel = new JPanel(new GridLayout(2, 0));
        emoticonsPanel.add(emoticonsBox);
        emoticonsPanel.add(animatedEmoticonsBox);
        emoticonsPanel.setBorder(emptyBorder);
        emoticonsPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createTitledBorder(UIMessages.PROPERTIES_DISPLAY_EMOTICONS_TITLE),
                emoticonsPanel.getBorder()));

        return emoticonsPanel;
    }


    private JPanel createTimestampsPanel() {
        timestampsBox = new JCheckBoxHelper(UIMessages.messages, "properties.display.message.timestamps");
        timestampsBox.addItemListener(new ItemListener() {
            public void itemStateChanged(ItemEvent e) {
                if (e.getStateChange() == ItemEvent.SELECTED) {
                    timestampFormatList.setEnabled(true);
                } else {
                    timestampFormatList.setEnabled(false);
                }
            }
        });

        timestampFormatList = new JComboBox(formatData);

        JPanel timestampPanel = new JPanel(new GridLayout(2, 0));
        timestampPanel.add(timestampsBox);
        JPanel p1 = new JPanel(new BorderLayout());
        p1.add(timestampFormatList, BorderLayout.WEST);
        timestampPanel.add(p1);
        timestampPanel.setBorder(emptyBorder);
        timestampPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createTitledBorder(UIMessages.PROPERTIES_DISPLAY_MESSAGE_TIMESTAMPS_TITLE),
                timestampPanel.getBorder()));

        return timestampPanel;
    }

    private static class BufferSize {
        private String description;
        private int size;

        public BufferSize(String description, int size) {
            this.description = description;
            this.size = size;
        }

        public int getSize() {
            return size;
        }

        public String toString() {
            return description;
        }
    }
}
