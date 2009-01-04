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
import org.nuclearbunny.icybee.ui.util.ActionHelper;
import org.nuclearbunny.icybee.ui.util.JSpinnerHelper;
import org.nuclearbunny.icybee.ui.util.JTextFieldHelper;

import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;

class ServerDialog extends JDialog {
    private static final Border EMPTY_BORDER = BorderFactory.createEmptyBorder(5, 5, 5, 5);

    private boolean okaySelected;
    private JButton okayButton;
    private JButton cancelButton;

    private JTextField serverAddressField;
    private JSpinner portNumberField;

    public ServerDialog() {
        super();
        initDialog();
        setPortNumber("7326"); // Default ICB port
    }

    public ServerDialog(Frame owner) {
        super(owner, true);
        initDialog();
    }

    public boolean isOkaySelected() {
        return okaySelected;
    }

    public String getServerAddress() {
        return serverAddressField.getText();
    }

    public void setServerAddress(String serverAddress) {
        serverAddressField.setText(serverAddress);
    }

    public String getPortNumber() {
        return portNumberField.getValue().toString();
    }

    public void setPortNumber(String portNumber) {
        portNumberField.setValue(new Integer(portNumber));
    }

    private void initDialog() {
        setTitle(UIMessages.PROPERTIES_SERVER_DIALOG_TITLE);

        JRootPane rootPane = getRootPane();

        okayButton = new JButton(new OkayAction());
        okayButton.setEnabled(false);
        rootPane.setDefaultButton(okayButton);

        Action cancelAction = new CancelAction();
        cancelButton = new JButton(cancelAction);
        KeyStroke keyStroke = KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0);
        rootPane.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(keyStroke, "escape");
        rootPane.getActionMap().put("escape", cancelAction);

        JPanel p = new JPanel(new GridLayout(1, 0, 5, 5));
        p.add(okayButton);
        p.add(cancelButton);
        JPanel buttonPanel = new JPanel(new BorderLayout());
        buttonPanel.add(p, BorderLayout.EAST);
        buttonPanel.setBorder(EMPTY_BORDER);

        DocumentListener textWatcher = new TextFieldWatcher();

        serverAddressField = new JTextFieldHelper(UIMessages.messages, "properties.server.address");
        serverAddressField.getDocument().addDocumentListener(textWatcher);
        JLabel serverAddressLabel = ((JTextFieldHelper) serverAddressField).getDefaultLabel();

        SpinnerNumberModel portModel = new SpinnerNumberModel(7326, 1, 65535, 1);
        portNumberField = new JSpinnerHelper(UIMessages.messages, "properties.server.port", portModel);
        ((JSpinner.DefaultEditor) portNumberField.getEditor()).getTextField().getDocument().addDocumentListener(textWatcher);
        JLabel portNumberLabel = ((JSpinnerHelper) portNumberField).getDefaultLabel();

        JPanel labelPanel = new JPanel(new GridLayout(2, 0));
        labelPanel.add(serverAddressLabel);
        labelPanel.add(portNumberLabel);

        JPanel fieldPanel = new JPanel(new GridLayout(2, 0));
        fieldPanel.add(serverAddressField);
        fieldPanel.add(portNumberField);

        JPanel p1 = new JPanel(new BorderLayout());
        p1.setBorder(EMPTY_BORDER);
        p1.add(labelPanel, BorderLayout.WEST);
        p1.add(fieldPanel, BorderLayout.CENTER);

        Container c = getContentPane();
        c.setLayout(new BorderLayout());
        c.add(p1, BorderLayout.NORTH);
        c.add(buttonPanel, BorderLayout.SOUTH);
    }

    private class OkayAction extends ActionHelper {
        public OkayAction() {
            super(UIMessages.messages, "ok.action");
        }

        public void actionPerformed(ActionEvent e) {
            okaySelected = true;
            dispose();
        }
    }

    private class CancelAction extends ActionHelper {
        public CancelAction() {
            super(UIMessages.messages, "cancel.action");
        }

        public void actionPerformed(ActionEvent e) {
            okaySelected = false;
            dispose();
        }
    }

    private class TextFieldWatcher implements DocumentListener {
        public void insertUpdate(DocumentEvent e) {
            checkFields(e);
        }

        public void removeUpdate(DocumentEvent e) {
            checkFields(e);
        }

        public void changedUpdate(DocumentEvent e) {
            checkFields(e);
        }

        private void checkFields(DocumentEvent e) {
            int addrLen = serverAddressField.getDocument().getLength();
            int portLen = ((JSpinner.DefaultEditor) portNumberField.getEditor()).getTextField().getDocument().getLength();
            boolean enableOK = (addrLen > 0 && portLen > 0);
            okayButton.setEnabled(enableOK);
        }
    }
}
