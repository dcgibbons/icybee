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

import org.nuclearbunny.icybee.ui.FontUtility;
import org.nuclearbunny.icybee.ui.UIMessages;
import org.nuclearbunny.icybee.ui.util.*;

import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.text.*;
import java.awt.*;
import java.awt.event.*;
import java.util.*;

class DisplayTextPanel extends JPanel {
    private final static Border EMPTY_BORDER = BorderFactory.createEmptyBorder(5, 5, 5, 5);
    private final static String[] FONT_FAMILIES = GraphicsEnvironment.getLocalGraphicsEnvironment().getAvailableFontFamilyNames();

    private Map attributeMap;
    private TextStyleModel textStyleModel;
    private StyleListener stylesListener;

    private JLabel fontFamilyLabel;
    private JLabel fontSizeLabel;
    private JLabel sampleTextLabel;
    private JList screenElementList;
    private JComboBox fontFamilyList;
    private JComboBox fontSizeField;
    private JButton backgroundColorButton;
    private JButton foregroundColorButton;
    private JCheckBox boldCheckBox;
    private JCheckBox italicCheckBox;
    private JCheckBox underlineCheckBox;

    public DisplayTextPanel() {
        super();

        attributeMap = new HashMap();
        textStyleModel = new TextStyleModel();
        stylesListener = new StyleListener();

        sampleTextLabel = new JLabel(UIMessages.PROPERTIES_DISPLAY_TEXT_SAMPLE_TEXT_LABEL);
        sampleTextLabel.setOpaque(true);
        sampleTextLabel.setHorizontalAlignment(SwingConstants.CENTER);
        sampleTextLabel.setForeground(sampleTextLabel.getBackground());

        backgroundColorButton = new JButton(new ModifyBackgroundAction());
        foregroundColorButton = new JButton(new ModifyForegroundAction());

        boldCheckBox = new JCheckBoxHelper(UIMessages.messages, "properties.display.text.bold");
        boldCheckBox.addItemListener(stylesListener);
        italicCheckBox = new JCheckBoxHelper(UIMessages.messages, "properties.display.text.italic");
        italicCheckBox.addItemListener(stylesListener);
        underlineCheckBox = new JCheckBoxHelper(UIMessages.messages, "properties.display.text.underline");
        underlineCheckBox.addItemListener(stylesListener);

        screenElementList = new JListHelper(UIMessages.messages, "properties.display.text.screen.elements", textStyleModel);
        screenElementList.setVisibleRowCount(4);
        screenElementList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        screenElementList.addListSelectionListener(new ListSelectionListener() {
            public void valueChanged(ListSelectionEvent e) {
                if (!e.getValueIsAdjusting()) {
                    int n = screenElementList.getSelectedIndex();
                    if (n != -1) {
                        String styleName = ((StyleName) textStyleModel.getElementAt(n)).getStyleName();
                        MutableAttributeSet attributes = (MutableAttributeSet) attributeMap.get(styleName);
                        fontFamilyList.setSelectedItem(StyleConstants.getFontFamily(attributes));
                        fontSizeField.setSelectedItem(Integer.toString(StyleConstants.getFontSize(attributes)));
                        boldCheckBox.setSelected(StyleConstants.isBold(attributes));
                        italicCheckBox.setSelected(StyleConstants.isItalic(attributes));
                        underlineCheckBox.setSelected(StyleConstants.isUnderline(attributes));
                        setSampleText(styleName);
                    }
                }
            }
        });

        fontFamilyList = new JComboBoxHelper(UIMessages.messages, "properties.display.text.font.family", DisplayTextPanel.FONT_FAMILIES);
        fontFamilyLabel = ((JComboBoxHelper) fontFamilyList).getDefaultLabel();
        fontFamilyList.addActionListener(
                new ActionListener() {
                    public void actionPerformed(ActionEvent e) {
                        String fontFamily = fontFamilyList.getSelectedItem().toString();
                        Iterator i = attributeMap.values().iterator();
                        while (i.hasNext()) {
                            MutableAttributeSet attributes = (MutableAttributeSet) i.next();
                            StyleConstants.setFontFamily(attributes, fontFamily);
                        }

                        int n = screenElementList.getSelectedIndex();
                        if (n != -1) {
                            String styleName = ((StyleName) textStyleModel.getElementAt(n)).getStyleName();
                            setSampleText(styleName);
                        }
                    }
                });

        // TODO: what's the proper i18n way of building the default list?
        String[] defaultSizes = {"8", "9", "10", "11", "12", "13", "14"};
        fontSizeField = new JComboBoxHelper(UIMessages.messages, "properties.display.text.font.size", defaultSizes);
        fontSizeLabel = ((JComboBoxHelper) fontSizeField).getDefaultLabel();
        fontSizeField.setEditable(true);
        fontSizeField.addActionListener(
                new ActionListener() {
                    public void actionPerformed(ActionEvent e) {
                        int fontSize = Integer.parseInt(fontSizeField.getSelectedItem().toString());
                        Iterator i = attributeMap.values().iterator();
                        while (i.hasNext()) {
                            MutableAttributeSet attributes = (MutableAttributeSet) i.next();
                            StyleConstants.setFontSize(attributes, fontSize);
                        }

                        int n = screenElementList.getSelectedIndex();
                        if (n != -1) {
                            String styleName = ((StyleName) textStyleModel.getElementAt(n)).getStyleName();
                            setSampleText(styleName);
                        }

                    }
                });

        setLayout(new BorderLayout());
        JPanel layoutPanel = createLayout();
        add(layoutPanel, BorderLayout.NORTH);
    }

    public void setInitialValues() {
        screenElementList.setSelectedIndex(0);
    }

    /**
     * Adds a named <code>Style</code> to the available text options. The
     * style name will appear as an available user customizable text option.
     *
     * @param s a <code>Style</code> object
     * @see Style
     */
    public void addStyle(Style s) {
        SimpleAttributeSet attributes = new SimpleAttributeSet(s.copyAttributes());
        attributeMap.put(s.getName(), attributes);
        textStyleModel.addStyleName(s.getName());
    }

    /**
     * Retrieves the new <code>AttributeSet</code> object for the specified
     * style name.
     *
     * @param styleName the name of the style to retrieve attributes for
     * @return AttributeSet the new attributes for the specified style
     * @see AttributeSet
     */
    public AttributeSet getStyleAttributes(String styleName) {
        return (AttributeSet) attributeMap.get(styleName);
    }

    /**
     * Creates a JPanel containing the layout of all controls for the
     * DisplayTextPanel.
     */
    private JPanel createLayout() {
        // create the sub-pane for the sample text output
        JPanel samplePanel = new JPanel();
        samplePanel.add(sampleTextLabel);
        samplePanel.setBorder(BorderFactory.createCompoundBorder(
                EMPTY_BORDER, BorderFactory.createLineBorder(Color.black)));


        // create the sub-pane to show the list of available text items
        JScrollPane scrollPane = new JScrollPane(screenElementList,
                                                 ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS,
                                                 ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        JLabel screenElementLabel = ((JListHelper) screenElementList).getDefaultLabel();
        JPanel screenElementPane = new JPanel(new BorderLayout());
        screenElementPane.add(screenElementLabel, BorderLayout.NORTH);
        screenElementPane.add(scrollPane, BorderLayout.CENTER);


        // create the font items pane
        JPanel labelsPane = new JPanel(new GridLayout(2, 0));
        labelsPane.add(fontFamilyLabel);
        labelsPane.add(fontSizeLabel);
        labelsPane.setBorder(EMPTY_BORDER);

        JPanel fieldsPane = new JPanel(new GridLayout(2, 0));
        JPanel p1 = new JPanel(new BorderLayout());
        p1.add(fontFamilyList, BorderLayout.NORTH);
        fieldsPane.add(p1);
        JPanel p2 = new JPanel(new BorderLayout());
        p2.add(fontSizeField, BorderLayout.NORTH);
        fieldsPane.add(p2);
        fieldsPane.setBorder(EMPTY_BORDER);

        JPanel fontItemsPane = new JPanel(new BorderLayout());
        fontItemsPane.add(labelsPane, BorderLayout.WEST);
        fontItemsPane.add(fieldsPane, BorderLayout.CENTER);

        JPanel backButtonP1 = new JPanel(new GridLayout(1, 0, 5, 5));
        backButtonP1.add(backgroundColorButton);
        JPanel backButtonP2 = new JPanel(new BorderLayout());
        backButtonP2.add(backButtonP1, BorderLayout.EAST);
        backButtonP2.setBorder(EMPTY_BORDER);
        JPanel backButtonP3 = new JPanel(new BorderLayout());
        backButtonP3.add(backButtonP2, BorderLayout.SOUTH);

        Box fontItemsPane2 = new Box(BoxLayout.Y_AXIS);
        fontItemsPane2.add(fontItemsPane);
        fontItemsPane2.add(backButtonP2);

        JPanel fontItemsPane3 = new JPanel(new BorderLayout());
        fontItemsPane3.add(fontItemsPane2, BorderLayout.NORTH);
        fontItemsPane3.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createTitledBorder(UIMessages.PROPERTIES_DISPLAY_TEXT_COMMON_TEXT_TITLE),
                fontItemsPane3.getBorder()));


        // create the style options pane
        JPanel styleOptionsPane = new JPanel(new BorderLayout());
        JPanel styleOptionsPane2 = new JPanel(new GridLayout(3, 0));
        styleOptionsPane2.add(boldCheckBox);
        styleOptionsPane2.add(italicCheckBox);
        styleOptionsPane2.add(underlineCheckBox);
        styleOptionsPane.add(styleOptionsPane2, BorderLayout.NORTH);
        styleOptionsPane.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createTitledBorder(UIMessages.PROPERTIES_DISPLAY_TEXT_STYLE_TITLE),
                styleOptionsPane.getBorder()));

        JPanel buttonP1 = new JPanel(new GridLayout(1, 0, 5, 5));
        buttonP1.add(foregroundColorButton);
        JPanel buttonP2 = new JPanel(new BorderLayout());
        buttonP2.add(buttonP1, BorderLayout.EAST);
        buttonP2.setBorder(EMPTY_BORDER);
        JPanel buttonP3 = new JPanel(new BorderLayout());
        buttonP3.add(buttonP2, BorderLayout.SOUTH);

        Box customTextPane = new Box(BoxLayout.Y_AXIS);
        customTextPane.add(styleOptionsPane);
        customTextPane.add(buttonP3);

        JPanel gridPanel = new JPanel(new GridLayout(0, 2));
        JPanel screenElementPane2 = new JPanel(new BorderLayout());
        screenElementPane2.add(screenElementPane, BorderLayout.NORTH);
        gridPanel.add(screenElementPane2);
        JPanel customTextPane2 = new JPanel(new BorderLayout());
        customTextPane2.add(customTextPane, BorderLayout.NORTH);
        gridPanel.add(customTextPane2);

        JPanel gridPanel2 = new JPanel(new BorderLayout());
        gridPanel2.add(gridPanel, BorderLayout.NORTH);
        gridPanel2.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createTitledBorder(UIMessages.PROPERTIES_DISPLAY_TEXT_CUSTOM_TEXT_TITLE),
                gridPanel2.getBorder()));

        Box myp3 = new Box(BoxLayout.Y_AXIS);
        myp3.add(fontItemsPane3);
        myp3.add(gridPanel2);

        JPanel myp4 = new JPanel(new BorderLayout());
        myp4.add(myp3, BorderLayout.NORTH);
        myp4.add(samplePanel, BorderLayout.SOUTH);

        return myp4;
    }

    /**
     * Sets a new background color for all of the available text items.
     */
    private void setBackgroundColor(Color c) {
        Iterator i = attributeMap.values().iterator();
        while (i.hasNext()) {
            MutableAttributeSet attributes = (MutableAttributeSet) i.next();
            StyleConstants.setBackground(attributes, c);
        }

        int n = screenElementList.getSelectedIndex();
        if (n != -1) {
            String styleName = ((StyleName) textStyleModel.getElementAt(n)).getStyleName();
            setSampleText(styleName);
        }
    }

    /**
     * Sets a new foreground color for the currently selected text item, if
     * any.
     */
    private void setForegroundColor(Color c) {
        int n = screenElementList.getSelectedIndex();
        if (n != -1) {
            String styleName = ((StyleName) textStyleModel.getElementAt(n)).getStyleName();
            MutableAttributeSet attributes = (MutableAttributeSet) attributeMap.get(styleName);
            StyleConstants.setForeground(attributes, c);
            setSampleText(styleName);
        }
    }

    /**
     * Updates the sample text with the attributes from the specified style
     * name.
     */
    private void setSampleText(String styleName) {
        AttributeSet attributes = (AttributeSet) attributeMap.get(styleName);
        if (attributes == null) {
            return;
        }

        sampleTextLabel.setBackground(StyleConstants.getBackground(attributes));
        sampleTextLabel.setForeground(StyleConstants.getForeground(attributes));

        Font f = FontUtility.getFont(attributes);
        sampleTextLabel.setFont(f);
    }

    class TextStyleModel extends AbstractListModel {
        private ArrayList textStyles = new ArrayList();

        public TextStyleModel() {
            textStyles = new ArrayList();
        }

        public void addStyleName(String styleName) {
            StyleName s = new StyleName(styleName);
            textStyles.add(s);
            int n = textStyles.size();
            fireIntervalAdded(this, n, n);
        }

        /**
         * Returns the length of the list.
         */
        public int getSize() {
            return textStyles.size();
        }

        /**
         * Returns the value at the specified index.
         */
        public Object getElementAt(int index) {
            return textStyles.get(index);
        }
    }

    class StyleName {
        private String styleName;
        private String localizedName;

        public StyleName(String styleName) {
            this.styleName = styleName;
            this.localizedName = UIMessages.messages.getString(styleName);
        }

        public String getStyleName() {
            return styleName;
        }

        public String toString() {
            return localizedName;
        }
    }

    class StyleListener implements ItemListener {
        public void itemStateChanged(ItemEvent e) {
            int n = screenElementList.getSelectedIndex();
            if (n != -1) {
                String styleName = ((StyleName) textStyleModel.getElementAt(n)).getStyleName();
                MutableAttributeSet attributes = (MutableAttributeSet) attributeMap.get(styleName);

                Object source = e.getItemSelectable();
                boolean selected = (e.getStateChange() == ItemEvent.SELECTED);
                if (source == boldCheckBox) {
                    StyleConstants.setBold(attributes, selected);
                } else if (source == italicCheckBox) {
                    StyleConstants.setItalic(attributes, selected);
                } else if (source == underlineCheckBox) {
                    StyleConstants.setUnderline(attributes, selected);
                }

                setSampleText(styleName);
            }
        }
    }

    private class ModifyBackgroundAction extends ActionHelper {
        public ModifyBackgroundAction() {
            super(UIMessages.messages, "properties.display.text.modify.background.button");
        }

        public void actionPerformed(ActionEvent e) {
            int n = screenElementList.getSelectedIndex();
            if (n != -1) {
                Color newColor = JColorChooser.showDialog(DisplayTextPanel.this,
                                                          UIMessages.CHOOSE_BACKGROUND_TITLE,
                                                          sampleTextLabel.getBackground());
                if (newColor != null) {
                    setBackgroundColor(newColor);
                }
            }
        }
    }

    private class ModifyForegroundAction extends ActionHelper {
        public ModifyForegroundAction() {
            super(UIMessages.messages, "properties.display.text.modify.foreground.button");
        }

        public void actionPerformed(ActionEvent e) {
            int n = screenElementList.getSelectedIndex();
            if (n != -1) {
                Color newColor = JColorChooser.showDialog(DisplayTextPanel.this,
                                                          UIMessages.CHOOSE_FOREGROUND_TITLE,
                                                          sampleTextLabel.getForeground());
                if (newColor != null) {
                    setForegroundColor(newColor);
                }
            }
        }
    }
}
