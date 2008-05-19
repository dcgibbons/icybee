/*
 * IcyBee - http://www.nuclearbunny.org/icybee/
 * A client for the Internet CB Network - http://www.icb.net/
 *
 * Copyright (C) 2000-2008 David C. Gibbons
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
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import java.awt.*;
import java.awt.event.ActionEvent;

class ConnectPanel extends JPanel {
    private Border emptyBorder = BorderFactory.createEmptyBorder(5, 5, 5, 5);

    // Server List group variables
    private JPanel serversPanel;
    private JPanel userInfoPanel;
    private JPanel serverListPanel;
    private JList serverList;
    private DefaultListModel serverModel;
    private JPanel serverButtonsPanel;
    private JButton addServerButton;
    private JButton editServerButton;
    private JButton deleteServerButton;

    // User Info group variables
    private JTextField userNameField;
    private JTextField nickNameField;
    private JTextField alternativeField;
    private JTextField groupNameField;
    private JPasswordField passwordField;

    public ConnectPanel() {
        setupPanel();
    }

    public String[] getServers() {
        int n = serverModel.getSize();
        String[] servers = new String[n];
        for (int i = 0; i < n; i++) {
            servers[i] = (String) serverModel.getElementAt(i);
        }
        return servers;
    }

    public void setServers(String[] servers) {
        serverModel.removeAllElements();
        for (int i = 0; i < servers.length; i++) {
            serverModel.addElement(servers[i]);
        }
    }

    public String getDefaultServer() {
        return (String) serverList.getSelectedValue();
    }

    public void setDefaultServer(String defaultServer) {
        serverList.setSelectedValue(defaultServer, true);
    }

    public String getUserName() {
        return userNameField.getText();
    }

    public void setUserName(String userName) {
        userNameField.setText(userName);
    }

    public String getNickName() {
        return nickNameField.getText();
    }

    public void setNickName(String nickName) {
        nickNameField.setText(nickName);
    }

    public String getAlternativeNickName() {
        return alternativeField.getText();
    }

    public void setAlternativeNickName(String alternativeNickName) {
        alternativeField.setText(alternativeNickName);
    }

    public String getGroupName() {
        return groupNameField.getText();
    }

    public void setGroupName(String groupName) {
        groupNameField.setText(groupName);
    }

    public String getPassword() {
        // XXX this is OK, since we aren't very concerned with password
        // security in this type of app...
        return new String(passwordField.getPassword());
    }

    public void setPassword(String password) {
        passwordField.setText(password);
    }

    private void setupPanel() {
        serversPanel = createServersPanel();
        userInfoPanel = createUserInfoPanel();

        JPanel p1 = new JPanel(new BorderLayout());
        p1.add(serversPanel, BorderLayout.NORTH);

        JPanel p2 = new JPanel(new BorderLayout());
        p2.add(userInfoPanel, BorderLayout.NORTH);

        Box p3 = new Box(BoxLayout.Y_AXIS);
        p3.add(p1);
        p3.add(p2);

        setLayout(new BorderLayout());
        add(p3, BorderLayout.NORTH);
    }

    private JPanel createServersPanel() {
        serverModel = new DefaultListModel();
        serverList = new JListHelper(UIMessages.messages, "properties.connect.server.list", serverModel);
        JLabel serverListLabel = ((JListHelper) serverList).getDefaultLabel();

        serverList.addListSelectionListener(new ListSelectionListener() {
            public void valueChanged(ListSelectionEvent e) {
                boolean selected = (serverList.getSelectedIndex() != -1);
                editServerButton.setEnabled(selected);
                deleteServerButton.setEnabled(selected);
            }
        });

        JScrollPane serverScrollPane = new JScrollPane(serverList);

        serverListPanel = new JPanel(new BorderLayout());
        serverListPanel.setBorder(emptyBorder);
        serverListPanel.add(serverListLabel, BorderLayout.NORTH);
        serverListPanel.add(serverScrollPane, BorderLayout.CENTER);

        addServerButton = new JButton(new AddAction());
        editServerButton = new JButton(new EditAction());
        deleteServerButton = new JButton(new DeleteAction());

        serverButtonsPanel = new JPanel(new GridLayout(3, 0, 5, 0));
        serverButtonsPanel.setBorder(emptyBorder);
        serverButtonsPanel.add(addServerButton);
        serverButtonsPanel.add(editServerButton);
        serverButtonsPanel.add(deleteServerButton);

        JPanel serverButtonsPanel2 = new JPanel(new BorderLayout());
        serverButtonsPanel2.add(serverButtonsPanel, BorderLayout.NORTH);

        JPanel serversPanel = new JPanel(new BorderLayout());
        serversPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createTitledBorder(UIMessages.PROPERTIES_ICB_SERVER_MGMT_TITLE), emptyBorder));
        serversPanel.add(serverListPanel, BorderLayout.CENTER);
        serversPanel.add(serverButtonsPanel2, BorderLayout.EAST);

        return serversPanel;
    }

    private JPanel createUserInfoPanel() {
        userNameField = new JTextFieldHelper(UIMessages.messages, "properties.connect.user.name");
        JLabel userNameLabel = ((JTextFieldHelper) userNameField).getDefaultLabel();

        nickNameField = new JTextFieldHelper(UIMessages.messages, "properties.connect.nick.name");
        JLabel nickNameLabel = ((JTextFieldHelper) nickNameField).getDefaultLabel();

        alternativeField = new JTextFieldHelper(UIMessages.messages, "properties.connect.alternative.nick.name");
        JLabel alternativeLabel = ((JTextFieldHelper) alternativeField).getDefaultLabel();

        groupNameField = new JTextFieldHelper(UIMessages.messages, "properties.connect.group.name");
        JLabel groupNameLabel = ((JTextFieldHelper) groupNameField).getDefaultLabel();

        passwordField = new JPasswordFieldHelper(UIMessages.messages, "properties.connect.password");
        JLabel passwordLabel = ((JPasswordFieldHelper) passwordField).getDefaultLabel();

        JPanel labelPanel = new JPanel(new GridLayout(6, 0));
        labelPanel.add(userNameLabel);
        labelPanel.add(nickNameLabel);
        labelPanel.add(alternativeLabel);
        labelPanel.add(groupNameLabel);
        labelPanel.add(passwordLabel);

        JPanel fieldPanel = new JPanel(new GridLayout(6, 0));
        fieldPanel.add(userNameField);
        fieldPanel.add(nickNameField);
        fieldPanel.add(alternativeField);
        fieldPanel.add(groupNameField);
        fieldPanel.add(passwordField);

        JPanel p1 = new JPanel(new BorderLayout());
        p1.setBorder(emptyBorder);
        p1.add(labelPanel, BorderLayout.WEST);
        p1.add(fieldPanel, BorderLayout.CENTER);

        JPanel p2 = new JPanel(new BorderLayout());
        p2.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createTitledBorder(UIMessages.PROPERTIES_USER_INFO_TITLE), emptyBorder));
        p2.add(p1, BorderLayout.NORTH);

        return p2;
    }

    private void addOrEditServer(boolean editServer) {
        ServerDialog dlg = new ServerDialog();

        if (editServer) {
            String currentServer = (String) serverList.getSelectedValue();
            int separator = currentServer.indexOf(':');
            String host = currentServer.substring(0, separator);
            String port = currentServer.substring(separator + 1);

            dlg.setServerAddress(host);
            dlg.setPortNumber(port);
        }

        dlg.setModal(true);
        dlg.setLocationRelativeTo(ConnectPanel.this);
        dlg.setSize(300, 180);
        dlg.setVisible(true);

        if (dlg.isOkaySelected()) {
            StringBuffer buffer = new StringBuffer();
            buffer.append(dlg.getServerAddress()).append(':').append(dlg.getPortNumber());
            String newServer = buffer.toString();

            if (editServer) {
                String currentServer = (String) serverList.getSelectedValue();
                serverModel.removeElement(currentServer);

            }
            serverModel.addElement(newServer);
            serverList.setSelectedValue(newServer, true);
        }
    }

    private class AddAction extends ActionHelper {
        public AddAction() {
            super(UIMessages.messages, "properties.connect.action.add");
        }

        public void actionPerformed(ActionEvent e) {
            addOrEditServer(false);
        }
    }

    private class EditAction extends ActionHelper {
        public EditAction() {
            super(UIMessages.messages, "properties.connect.action.edit");
        }

        public void actionPerformed(ActionEvent e) {
            addOrEditServer(true);
        }
    }

    private class DeleteAction extends ActionHelper {
        public DeleteAction() {
            super(UIMessages.messages, "properties.connect.action.delete");
        }

        public void actionPerformed(ActionEvent e) {
            String currentServer = (String) serverList.getSelectedValue();
            if (currentServer != null) {
                serverModel.removeElement(currentServer);
            }
        }
    }
}