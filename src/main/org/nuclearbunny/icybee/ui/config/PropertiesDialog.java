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

import org.nuclearbunny.icybee.*;
import org.nuclearbunny.icybee.ui.ClientPanel;
import org.nuclearbunny.icybee.ui.UIMessages;
import org.nuclearbunny.icybee.ui.util.ActionHelper;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.border.*;
import javax.swing.event.*;
import javax.swing.tree.*;
import javax.swing.text.*;

public class PropertiesDialog extends JDialog {
    private static final Border EMPTY_BORDER = BorderFactory.createEmptyBorder(5, 5, 5, 5);
    private static final String EMPTY_PANEL = "Empty";
    private static final String ROOT_NODE = "root";

    private DefaultMutableTreeNode connectNode;
    private DefaultMutableTreeNode connectOptionsNode;
    private DefaultMutableTreeNode displayNode;
    private DefaultMutableTreeNode displayTextNode;
    //private DefaultMutableTreeNode displayEmoticonsNode;
    private DefaultMutableTreeNode inputNode;
    private DefaultMutableTreeNode loggingNode;
    private DefaultMutableTreeNode scriptingNode;
    private DefaultMutableTreeNode advancedNode;

    private JTree tree;
    private JScrollPane panelView;
    private JPanel cardPanel;
    private CardLayout cardLayout;

    private ICBProperties props;
    private JButton okayButton;
    private JButton cancelButton;
    private boolean okSelected = false;

    private JPanel emptyPanel;
    private ConnectPanel connectPanel;
    private ConnectOptionsPanel connectOptionsPanel;
    private DisplayPanel displayPanel;
    private DisplayTextPanel displayTextPanel;
    private InputPanel inputPanel;
    private LoggingPanel loggingPanel;
    private ScriptingPanel scriptingPanel;
    private AdvancedPanel advancedPanel;

    public PropertiesDialog(JFrame parent, ICBProperties props) {
        super(parent, true);
        this.props = props;

        createDialog();
        tree.setSelectionPath(new TreePath(connectNode.getPath()));
    }

    private void createDialog() {
        setTitle(UIMessages.PROPERTIES_DIALOG_TITLE);

        okayButton = new JButton(new OkayAction());
        getRootPane().setDefaultButton(okayButton);

        Action cancelAction = new CancelAction();
        cancelButton = new JButton(cancelAction);
        KeyStroke keyStroke = KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0);
        getRootPane().getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(keyStroke, "escape");
        getRootPane().getActionMap().put("escape", cancelAction);

        JPanel p = new JPanel(new GridLayout(1, 0, 5, 5));
        p.add(okayButton);
        p.add(cancelButton);
        JPanel buttonPanel = new JPanel(new BorderLayout());
        buttonPanel.add(p, BorderLayout.EAST);
        buttonPanel.setBorder(PropertiesDialog.EMPTY_BORDER);

        // Create the nodes.
        DefaultMutableTreeNode top = new DefaultMutableTreeNode(PropertiesDialog.ROOT_NODE);

        connectNode = new DefaultMutableTreeNode(UIMessages.PROPERTIES_CONNECT_ITEM);
        top.add(connectNode);

        connectOptionsNode = new DefaultMutableTreeNode(UIMessages.PROPERTIES_CONNECT_OPTIONS_ITEM);
        connectNode.add(connectOptionsNode);

        displayNode = new DefaultMutableTreeNode(UIMessages.PROPERTIES_DISPLAY_ITEM);
        top.add(displayNode);

        displayTextNode = new DefaultMutableTreeNode(UIMessages.PROPERTIES_DISPLAY_TEXT_ITEM);
        displayNode.add(displayTextNode);

        inputNode = new DefaultMutableTreeNode(UIMessages.PROPERTIES_INPUT_ITEM);
        top.add(inputNode);

        loggingNode = new DefaultMutableTreeNode(UIMessages.PROPERTIES_LOGGING_ITEM);
        top.add(loggingNode);

        scriptingNode = new DefaultMutableTreeNode(UIMessages.PROPERTIES_SCRIPTING_ITEM);
        top.add(scriptingNode);

        advancedNode = new DefaultMutableTreeNode(UIMessages.PROPERTIES_ADVANCED_ITEM);
        top.add(advancedNode);

        // Create a tree that allows one selection at a time.
        tree = new JTree(top);
        tree.setRootVisible(false);
        tree.getSelectionModel().setSelectionMode(TreeSelectionModel.SINGLE_TREE_SELECTION);
        for (int i = 0, n = tree.getRowCount(); i < n; i++) {
	        tree.expandRow(i);
        }

        emptyPanel = new JPanel();
        connectPanel = new ConnectPanel();
        connectOptionsPanel = new ConnectOptionsPanel();
        displayPanel = new DisplayPanel();
        displayTextPanel = new DisplayTextPanel();
        inputPanel = new InputPanel();
        loggingPanel = new LoggingPanel();
        scriptingPanel = new ScriptingPanel();
        advancedPanel = new AdvancedPanel();

        cardLayout = new CardLayout();
        cardPanel = new JPanel(cardLayout);
        cardPanel.add(emptyPanel, PropertiesDialog.EMPTY_PANEL);
        cardPanel.add(connectPanel, UIMessages.PROPERTIES_CONNECT_ITEM);
        cardPanel.add(connectOptionsPanel, UIMessages.PROPERTIES_CONNECT_OPTIONS_ITEM);
        cardPanel.add(displayPanel, UIMessages.PROPERTIES_DISPLAY_ITEM);
        cardPanel.add(displayTextPanel, UIMessages.PROPERTIES_DISPLAY_TEXT_ITEM);
        cardPanel.add(inputPanel, UIMessages.PROPERTIES_INPUT_ITEM);
        cardPanel.add(loggingPanel, UIMessages.PROPERTIES_LOGGING_ITEM);
        cardPanel.add(scriptingPanel, UIMessages.PROPERTIES_SCRIPTING_ITEM);
        cardPanel.add(advancedPanel, UIMessages.PROPERTIES_ADVANCED_ITEM);

        panelView = new JScrollPane(cardPanel);

        //Create the scroll pane and add the tree to it.
        JScrollPane treeView = new JScrollPane(tree);
        tree.addTreeSelectionListener(new TreeSelectionListener() {
            public void valueChanged(TreeSelectionEvent e) {
                DefaultMutableTreeNode node = (DefaultMutableTreeNode)tree.
                        getLastSelectedPathComponent();
                if (node == null)
                    return;

                if (node == connectNode) {
                    cardLayout.show(cardPanel, UIMessages.PROPERTIES_CONNECT_ITEM);
                } else if (node == connectOptionsNode) {
                    cardLayout.show(cardPanel, UIMessages.PROPERTIES_CONNECT_OPTIONS_ITEM);
                } else if (node == displayNode) {
                    cardLayout.show(cardPanel, UIMessages.PROPERTIES_DISPLAY_ITEM);
                } else if (node == displayTextNode) {
                    cardLayout.show(cardPanel, UIMessages.PROPERTIES_DISPLAY_TEXT_ITEM);
                } else if (node == inputNode) {
                    cardLayout.show(cardPanel, UIMessages.PROPERTIES_INPUT_ITEM);
                } else if (node == loggingNode) {
                    cardLayout.show(cardPanel, UIMessages.PROPERTIES_LOGGING_ITEM);
                } else if (node == scriptingNode) {
                    cardLayout.show(cardPanel, UIMessages.PROPERTIES_SCRIPTING_ITEM);
                } else if (node == advancedNode) {
                    cardLayout.show(cardPanel, UIMessages.PROPERTIES_ADVANCED_ITEM);
                } else {
                    cardLayout.show(cardPanel, PropertiesDialog.EMPTY_PANEL);
                }
            }
        });

        //Add the scroll panes to a split pane.
        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
        splitPane.setDividerLocation(-1);
        splitPane.setTopComponent(treeView);
        splitPane.setBottomComponent(panelView);

        Container contentPane = getContentPane();
        contentPane.setLayout(new BorderLayout());
        contentPane.add(splitPane, BorderLayout.CENTER);
        contentPane.add(buttonPanel, BorderLayout.SOUTH);

        loadValues();
    }

    public boolean isOK() {
        return okSelected;
    }

    private void loadValues() {
        connectPanel.setServers(props.getServers());
        connectPanel.setDefaultServer(props.getDefaultServer());
    	connectPanel.setUserName(props.getUserID());
    	connectPanel.setNickName(props.getUserNick());
    	connectPanel.setAlternativeNickName(props.getUserAlternativeNick());
    	connectPanel.setGroupName(props.getUserGroup());
    	connectPanel.setPassword(props.getUserPassword());

    	connectOptionsPanel.setConnectOnStartup(props.getAutoConnect());
    	connectOptionsPanel.setKeepConnectionAlive(props.isKeepConnectionAliveEnabled());
    	connectOptionsPanel.setKeepConnectionAliveInterval(props.getKeepConnectionAliveInterval());
    	connectOptionsPanel.setEchoback(props.getEchoback());

    	displayPanel.setLimitDisplaySizeEnabled(props.isDisplaySizeLimited());
    	displayPanel.setSizeLimit(props.getDisplaySizeLimit());
    	displayPanel.setEmoticonsEnabled(props.areEmoticonsEnabled());
    	displayPanel.setAnimatedEmoticonsEnabled(props.areAnimatedEmoticonsEnabled());
    	displayPanel.setMessageTimestampsEnabled(props.areMessageTimestampsEnabled());
    	displayPanel.setMessageTimestampsFormat(props.getMessageTimestampsFormat());

        displayTextPanel.addStyle(props.getTextStyle(ClientPanel.TEXT_OPEN));
        displayTextPanel.addStyle(props.getTextStyle(ClientPanel.TEXT_OPEN_NICK));
        displayTextPanel.addStyle(props.getTextStyle(ClientPanel.TEXT_PERSONAL));
        displayTextPanel.addStyle(props.getTextStyle(ClientPanel.TEXT_PERSONAL_NICK));
        displayTextPanel.addStyle(props.getTextStyle(ClientPanel.TEXT_COMMAND_OUTPUT));
        displayTextPanel.addStyle(props.getTextStyle(ClientPanel.TEXT_ERROR));
        displayTextPanel.addStyle(props.getTextStyle(ClientPanel.TEXT_ERROR_HEADER));
        displayTextPanel.addStyle(props.getTextStyle(ClientPanel.TEXT_STATUS));
        displayTextPanel.addStyle(props.getTextStyle(ClientPanel.TEXT_STATUS_HEADER));
        displayTextPanel.addStyle(props.getTextStyle(ClientPanel.TEXT_TIMESTAMP));
        displayTextPanel.setInitialValues();

        inputPanel.setCommandPrefix(props.getCommandPrefix());

        loggingPanel.setAutomaticLogging(props.getLogAutomatically());
        loggingPanel.setAppendToLog(props.getLogAppend());
        loggingPanel.setLogFileName(props.getLogFileName());

        scriptingPanel.setExecuteInitScriptEnabled(props.isExecuteInitScriptEnabled());
        scriptingPanel.setInitScript(props.getInitScript());

        advancedPanel.setPersonalWindowsEnabled(props.arePersonalWindowsEnabled());
        advancedPanel.setAddIncomingToHistoryEnabled(props.isAddIncomingToHistoryEnabled());
        advancedPanel.setAddOutgoingToHistoryEnabled(props.isAddOutgoingToHistoryEnabled());
        advancedPanel.setTextEncoding(props.getTextEncoding());
    }

    private void saveValues() {
        props.setDefaultServer(connectPanel.getDefaultServer());
		props.setServers(connectPanel.getServers());
    	props.setUserID(connectPanel.getUserName());
    	props.setUserNick(connectPanel.getNickName());
        props.setUserAlternativeNick(connectPanel.getAlternativeNickName());
    	props.setUserGroup(connectPanel.getGroupName());
    	props.setUserPassword(connectPanel.getPassword());

		props.setAutoConnect(connectOptionsPanel.isConnectOnStartupEnabled());
		props.setKeepConnectionAliveEnabled(connectOptionsPanel.isKeepConnectionAliveEnabled());
		props.setKeepConnectionAliveInterval(connectOptionsPanel.getKeepConnectionAliveInterval());
		props.setEchoback(connectOptionsPanel.getEchoback());

		props.setDisplaySizeLimited(displayPanel.isLimitDisplaySizeEnabled());
		props.setDisplaySizeLimit(displayPanel.getSizeLimit());
		props.setEmoticonsEnabled(displayPanel.areEmoticonsEnabled());
		props.setAnimatedEmoticonsEnabled(displayPanel.areAnimatedEmoticonsEnabled());
		props.setMessageTimestampsEnabled(displayPanel.areMessageTimestampsEnabled());
		props.setMessageTimestampsFormat(displayPanel.getMessageTimestampsFormat());

        StyleContext styleContext = StyleContext.getDefaultStyleContext();

        Style s = styleContext.getStyle(ClientPanel.TEXT_OPEN);
        s.addAttributes(displayTextPanel.getStyleAttributes(ClientPanel.TEXT_OPEN));
        props.setTextStyle(s);

        s = styleContext.getStyle(ClientPanel.TEXT_OPEN_NICK);
        s.addAttributes(displayTextPanel.getStyleAttributes(ClientPanel.TEXT_OPEN_NICK));
        props.setTextStyle(s);

        s = styleContext.getStyle(ClientPanel.TEXT_PERSONAL);
        s.addAttributes(displayTextPanel.getStyleAttributes(ClientPanel.TEXT_PERSONAL));
        props.setTextStyle(s);

        s = styleContext.getStyle(ClientPanel.TEXT_PERSONAL_NICK);
        s.addAttributes(displayTextPanel.getStyleAttributes(ClientPanel.TEXT_PERSONAL_NICK));
        props.setTextStyle(s);

        s = styleContext.getStyle(ClientPanel.TEXT_COMMAND_OUTPUT);
        s.addAttributes(displayTextPanel.getStyleAttributes(ClientPanel.TEXT_COMMAND_OUTPUT));
        props.setTextStyle(s);

        s = styleContext.getStyle(ClientPanel.TEXT_ERROR);
        s.addAttributes(displayTextPanel.getStyleAttributes(ClientPanel.TEXT_ERROR));
        props.setTextStyle(s);

        s = styleContext.getStyle(ClientPanel.TEXT_ERROR_HEADER);
        s.addAttributes(displayTextPanel.getStyleAttributes(ClientPanel.TEXT_ERROR_HEADER));
        props.setTextStyle(s);

        s = styleContext.getStyle(ClientPanel.TEXT_STATUS);
        s.addAttributes(displayTextPanel.getStyleAttributes(ClientPanel.TEXT_STATUS));
        props.setTextStyle(s);

        s = styleContext.getStyle(ClientPanel.TEXT_STATUS_HEADER);
        s.addAttributes(displayTextPanel.getStyleAttributes(ClientPanel.TEXT_STATUS_HEADER));
        props.setTextStyle(s);

        s = styleContext.getStyle(ClientPanel.TEXT_TIMESTAMP);
        s.addAttributes(displayTextPanel.getStyleAttributes(ClientPanel.TEXT_TIMESTAMP));
        props.setTextStyle(s);

        props.setCommandPrefix(inputPanel.getCommandPrefix());

        props.setLogAutomatically(loggingPanel.isAutomaticLoggingEnabled());
        props.setLogAppend(loggingPanel.isAppendToLogEnabled());
        props.setLogFileName(loggingPanel.getLogFileName());

		props.setExecuteInitScriptEnabled(scriptingPanel.isExecuteInitScriptEnabled());
        props.setInitScript(scriptingPanel.getInitScript());

        props.setPersonalWindowsEnabled(advancedPanel.arePersonalWindowsEnabled());
        props.setAddIncomingToHistoryEnabled(advancedPanel.isAddIncomingToHistoryEnabled());
        props.setAddOutgoingToHistoryEnabled(advancedPanel.isAddOutgoingToHistoryEnabled());
        props.setTextEncoding(advancedPanel.getTextEncoding());
    }

    private class OkayAction extends ActionHelper {
        public OkayAction() {
            super(UIMessages.messages, "ok.action");
        }

        public void actionPerformed(ActionEvent e) {
            okSelected = true;
            saveValues();
            dispose();
        }
    }

    private class CancelAction extends ActionHelper {
        public CancelAction() {
            super(UIMessages.messages, "cancel.action");
        }

        public void actionPerformed(ActionEvent e) {
            okSelected = false;
            dispose();
        }
    }
}