/*
 * $Id: MainFrame.java,v 1.13 2003/01/13 04:33:08 dcgibbons Exp $
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

package org.nuclearbunny.icybee.ui;

import org.nuclearbunny.icybee.*;
import org.nuclearbunny.icybee.ui.macosx.MacOSXIntegrator;
import org.nuclearbunny.icybee.protocol.*;
import org.nuclearbunny.util.*;

import java.awt.*;
import java.awt.datatransfer.*;
import java.awt.event.*;
import java.io.*;
import java.net.*;
import java.text.*;
import java.util.*;
import java.util.regex.Pattern;
import java.util.regex.Matcher;

import javax.swing.*;
import javax.swing.plaf.*;

public class MainFrame extends JFrame implements ActionListener, WindowListener {
    private static final int SHORTCUT_KEY_MASK = Toolkit.getDefaultToolkit().getMenuShortcutKeyMask();

    private ICBClient client;

    private Clipboard clipboard;
    private JMenuBar menuBar;
    private StatusBar statusBar;
    private JToolBar toolBar;
    private ClientPanel groupPanel;
    private JTabbedPane tabbedPanel;
    private int currentTabIndex;
    private JPopupMenu tabPopupMenu;
    private JMenuItem closeTabItem;
    private JMenuItem clearTabItem;
    private JMenuItem saveTabItem;
    private Map personalPanels;
    private URLGrabber urlWatcher;

    private JMenu fileMenu;
    private JMenu editMenu;
    private JMenu viewMenu;
    private JMenu helpMenu;

    private Action connectAction;
    private Action disconnectAction;
    private Action loggingAction;
    private Action pauseAction;
    private Action openTclScriptAction;
    private Action preferencesAction;
    private Action exitAction;
    private Action copyAction;
    private Action pasteAction;
    private Action pasteSpecialAction;
    private Action toolbarAction;
    private Action statusbarAction;
    private Action urlGrabberAction;
    private Action reportBugAction;
    private Action mailingListAction;
    private Action sourceForgeProjectAction;
    private Action whatsnewAction;
    private Action aboutAction;
    private Action emoticonAction;

    private JButton emoticonButton;

    private static Pattern groupPattern = Pattern.compile(
            "(?:You are now in group|renamed group to) (\\w+)");

    public MainFrame(ICBClient client) {
        this.client = client;

        clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
        personalPanels = new HashMap();
        client.addStatusListener(new StatusWatcher());
        client.addMessageListener(new MessageWatcher());
        addWindowListener(this);
        setupFrame();
    }

    public void windowActivated(WindowEvent event) {
        tabbedPanel.getSelectedComponent().requestFocus();
    }

    public void windowClosed(WindowEvent event) {
    }

    public void windowClosing(WindowEvent event) {
    }

    public void windowDeactivated(WindowEvent event) {
    }

    public void windowDeiconified(WindowEvent event) {
    }

    public void windowIconified(WindowEvent event) {
    }

    public void windowOpened(WindowEvent event) {
        // Set the initial focus of the application to the main group panel
        groupPanel.requestFocus();
    }

    private void setupFrame() {
        ICBProperties props = client.getProperties();

        String lookAndFeelClassName = props.getLookAndFeel();
        if (lookAndFeelClassName == null ||
                lookAndFeelClassName.trim().equals("")) {
            lookAndFeelClassName = UIManager.getSystemLookAndFeelClassName();
        }

        try {
            UIManager.setLookAndFeel(lookAndFeelClassName);
        } catch (Exception ex) {
            System.err.println("Exception caught attempting to set user's Look & Feel, " +
                    ex.getMessage());
            System.err.println("Resetting to default System L&F");
            props.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        }


        ClassLoader loader = getClass().getClassLoader();
        ImageIcon icon = new ImageIcon(loader.getResource("images/icybee-icon.jpg"));
        this.setIconImage(icon.getImage());

        setTitle(UIMessages.messages.getString(UIMessages.ICYBEE_APP_NAME));
        // TODO: verify that these coordinates are still visible in case the user's display has changed
        setLocation(props.getFrameXPos(), props.getFrameYPos());
        setSize(props.getFrameWidth(), props.getFrameHeight());

        /* add a window exit handler */
        addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent evt) {
                exitApp();
            }
        });

        /* create the main menu bar */
        menuBar = new JMenuBar();
        setJMenuBar(menuBar);

        /* create the actions */
        connectAction = new ConnectAction();
        disconnectAction = new DisconnectAction();
        openTclScriptAction = new OpenTclScriptAction();
        loggingAction = new LoggingAction();
        pauseAction = new PauseAction();
        preferencesAction = new PreferencesAction();
        exitAction = new ExitAction();
        copyAction = new CopyAction();
        pasteAction = new PasteAction();
        pasteSpecialAction = new PasteSpecialAction();
        toolbarAction = new ToolbarAction();
        statusbarAction = new StatusbarAction();
        urlGrabberAction = new URLGrabberAction();
        reportBugAction = new ReportBugAction();
        mailingListAction = new MailingListAction();
        sourceForgeProjectAction = new SourceForgeProjectAction();
        whatsnewAction = new WhatsNewAction();
        aboutAction = new AboutAction();
        emoticonAction = new EmoticonAction();

        if (MacOSXIntegrator.isMacOSX()) {
            MacOSXIntegrator osx = MacOSXIntegrator.getInstance();
            osx.setAboutAction(aboutAction);
            osx.setPreferencesAction(preferencesAction);
            osx.setQuitAction(exitAction);
        }

        /* populate the menus */
        fileMenu = new JMenu(UIMessages.messages.getString(UIMessages.MENU_FILE_NAME));
        fileMenu.setMnemonic(KeyEvent.VK_F);
        fileMenu.add(connectAction);
        fileMenu.add(disconnectAction).setMnemonic('D'); // XXX I18N?
        fileMenu.addSeparator();
        fileMenu.add(openTclScriptAction).setMnemonic('O');
        fileMenu.addSeparator();
        fileMenu.add(loggingAction).setMnemonic('L');
        if (!MacOSXIntegrator.isMacOSX()) {
            fileMenu.addSeparator();
            fileMenu.add(preferencesAction).setMnemonic('R');
            fileMenu.addSeparator();
            fileMenu.add(exitAction).setMnemonic('X');
        }
        menuBar.add(fileMenu);

        editMenu = new JMenu(UIMessages.messages.getString(UIMessages.MENU_EDIT_NAME));
        editMenu.setMnemonic(KeyEvent.VK_E);
        editMenu.add(copyAction).setMnemonic('C');
        editMenu.add(pasteAction).setMnemonic('P');
        editMenu.add(pasteSpecialAction);
        editMenu.addSeparator();
        menuBar.add(editMenu);

        ActionListener lfAction = new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                UIManager.LookAndFeelInfo[] lfInfo = UIManager.getInstalledLookAndFeels();
                for (int i = 0; i < lfInfo.length; i++) {
                    final String lnfName = lfInfo[i].getName();
                    final String lnfClassName = lfInfo[i].getClassName();
                    if (e.getActionCommand().equals(lnfName)) {
                        try {
                            UIManager.setLookAndFeel(lnfClassName);
                            client.getProperties().setLookAndFeel(lfInfo[i].
                                    getClassName());
                            SwingUtilities.updateComponentTreeUI(MainFrame.this);
                        } catch (Exception ex) {
                        }
                        break;
                    }
                }
            }
        };
        JMenu lookAndFeelMenu = new JMenu("Look And Feel");
        UIManager.LookAndFeelInfo[] lfInfo = UIManager.getInstalledLookAndFeels();
        ButtonGroup group = new ButtonGroup();
        for (int i = 0; i < lfInfo.length; i++) {
            JRadioButtonMenuItem item = new JRadioButtonMenuItem(lfInfo[i].
                    getName());
            item.addActionListener(lfAction);
            group.add(item);
            lookAndFeelMenu.add(item);
            if (UIManager.getLookAndFeel().getName().equals(lfInfo[i].getName())) {
                item.setSelected(true);
            }
        }


        viewMenu = new JMenu(UIMessages.messages.getString(UIMessages.MENU_VIEW_NAME));
        viewMenu.setMnemonic(KeyEvent.VK_V);
        viewMenu.add(toolbarAction).setMnemonic('T');
        viewMenu.add(statusbarAction).setMnemonic('S');
        viewMenu.add(urlGrabberAction).setMnemonic('U');
        viewMenu.addSeparator();
        viewMenu.add(lookAndFeelMenu);
        menuBar.add(viewMenu);

        helpMenu = new JMenu(UIMessages.messages.getString(UIMessages.MENU_HELP_NAME));
        /*
         * Contents
         * Index
         * Search
         * About IcyBee
         */
        helpMenu.setMnemonic(KeyEvent.VK_H);
        helpMenu.add(reportBugAction).setMnemonic('B');
        helpMenu.add(mailingListAction).setMnemonic('L');
        helpMenu.add(sourceForgeProjectAction).setMnemonic('S');
        helpMenu.add(whatsnewAction).setMnemonic('N');
        if (!MacOSXIntegrator.isMacOSX()) {
            helpMenu.addSeparator();
            helpMenu.add(aboutAction).setMnemonic('A');
        }
        menuBar.add(helpMenu);

        /* create the toolbar */
        toolBar = new JToolBar();
        toolBar.add(connectAction);
        toolBar.add(disconnectAction);
        toolBar.addSeparator();
        toolBar.add(openTclScriptAction);
        toolBar.add(preferencesAction);
        toolBar.addSeparator();
        toolBar.add(copyAction);
        toolBar.add(pasteAction);
        toolBar.addSeparator();
        toolBar.add(pauseAction);
        toolBar.addSeparator();
        emoticonButton = toolBar.add(emoticonAction);

        statusBar = new StatusBar(client);
        groupPanel = new ClientPanel(client);

        urlWatcher = new URLGrabber();
        urlWatcher.setLocation(this.getX() + 100, this.getY() + 100); // ICK! save this location instead
        urlWatcher.setSize(320, 240);
        urlWatcher.setVisible(client.getProperties().isURLGrabberVisible());
        urlWatcher.addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent evt) {
                ICBProperties props = client.getProperties();
                props.setURLGrabberVisible(false);
            }
        });
        groupPanel.addURLListener(urlWatcher);

        /* add the primary components to the root panel */
        JPanel contentPane = (JPanel) getContentPane();
        contentPane.setLayout(new BorderLayout());
        contentPane.add(toolBar, BorderLayout.NORTH);
        contentPane.add(statusBar, BorderLayout.SOUTH);

        toolBar.setVisible(client.getProperties().isToolbarVisible());
        statusBar.setVisible(client.getProperties().isStatusbarVisible());

        tabbedPanel = new JTabbedPane();
        tabbedPanel.add(groupPanel, 0);
        tabbedPanel.setTitleAt(0, "Current Group");

        tabbedPanel.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent evt) {
                int mods = evt.getModifiers();

                if ((mods & InputEvent.BUTTON3_MASK) != 0) {
                    TabbedPaneUI ui = tabbedPanel.getUI();
                    currentTabIndex = ui.tabForCoordinate(tabbedPanel, evt.getX(), evt.getY());
                    if (currentTabIndex != -1) {
                        // disable the Close option on the current group tab
                        closeTabItem.setEnabled(currentTabIndex != 0);
                        tabPopupMenu.show(tabbedPanel, evt.getX(), evt.getY());
                    }
                }
            }
        });

        closeTabItem = new JMenuItem("Close", KeyEvent.VK_C);
        closeTabItem.addActionListener(this);
        clearTabItem = new JMenuItem("Clear", KeyEvent.VK_L);
        clearTabItem.addActionListener(this);
        saveTabItem = new JMenuItem("Save", KeyEvent.VK_S);
        saveTabItem.addActionListener(this);

        tabPopupMenu = new JPopupMenu();
        tabPopupMenu.add(closeTabItem);
        tabPopupMenu.add(clearTabItem);
        tabPopupMenu.add(saveTabItem);
        contentPane.add(tabbedPanel, BorderLayout.CENTER);

        /* setup the initial state (disconnected) */
        setDisconnected();
    }

    private ClientPanel getPersonalPanel(String nick) {
        String lowerCaseNick = nick.toLowerCase();
        ClientPanel p = (ClientPanel) personalPanels.get(lowerCaseNick);
        if (p == null) {
            PersonalPanel pp = new PersonalPanel(client, nick);
            pp.addURLListener(urlWatcher);
            personalPanels.put(lowerCaseNick, pp);
            tabbedPanel.add(nick, pp);
            pp.clear();
            p = pp;
        }
        return p;
    }

    public void actionPerformed(ActionEvent event) {
        Object source = event.getSource();
        if (source.equals(closeTabItem)) {
            if (currentTabIndex >= 1) {
                Component c = tabbedPanel.getComponentAt(currentTabIndex);
                String nick = tabbedPanel.getTitleAt(currentTabIndex).toLowerCase();
                tabbedPanel.removeTabAt(currentTabIndex);
                currentTabIndex = -1;
                c.setVisible(false);
                personalPanels.remove(nick);
            }
        } else if (source.equals(clearTabItem)) {
            ClientPanel p = (ClientPanel) tabbedPanel.getComponentAt(currentTabIndex);
            p.clear();
        } else if (source.equals(saveTabItem)) {
            // TODO: implement me!
        }
    }

    private void exitApp() {
        if (client != null) {
            ICBProperties props = client.getProperties();
            props.setFrameXPos(getLocation().x);
            props.setFrameYPos(getLocation().y);
            props.setFrameWidth(getWidth());
            props.setFrameHeight(getHeight());
            props.saveParameters();

            if (client.isConnected()) {
                client.disconnect();
            }
        }

        // TODO: save window location/size properties
        setVisible(false);
        dispose();
        System.exit(0);
    }

    private void setConnected() {
        connectAction.setEnabled(false);
        disconnectAction.setEnabled(true);
    }

    private void setConnecting() {
        connectAction.setEnabled(false);
        disconnectAction.setEnabled(false);
    }

    private void setDisconnected() {
        disconnectAction.setEnabled(false);
        connectAction.setEnabled(true);
    }

    private void setDisconnecting() {
        disconnectAction.setEnabled(false);
        connectAction.setEnabled(false);
    }

    class ConnectAction extends AbstractAction {
        public ConnectAction() {
            String name = UIMessages.messages.getString(UIMessages.CONNECT_NAME);
            String shortDesc = UIMessages.messages.getString(UIMessages.CONNECT_SHORT_DESCRIPTION);

            putValue(Action.NAME, name);
            putValue(Action.SHORT_DESCRIPTION, shortDesc);
            putValue(Action.MNEMONIC_KEY, new Integer('C'));
            putValue(Action.ACCELERATOR_KEY, KeyStroke.getKeyStroke(KeyEvent.VK_C, SHORTCUT_KEY_MASK | KeyEvent.SHIFT_MASK));

            URL iconURL = this.getClass().getClassLoader().getResource("toolbarButtonGraphics/media/Play16.gif");
            putValue(Action.SMALL_ICON, new ImageIcon(iconURL));
        }

        public void actionPerformed(ActionEvent evt) {
            try {
                ICBProperties props = client.getProperties();
                String server = props.getDefaultServer();
                client.connect(server);
            } catch (IOException ex) {
                System.err.println("unable to connect! " + ex.getMessage());
                ex.printStackTrace();
            }
        }
    }

    class DisconnectAction extends AbstractAction {
        public DisconnectAction() {
            putValue(Action.NAME, "Disconnect");
            putValue(Action.SHORT_DESCRIPTION, "Disconnects from the server");
            putValue(Action.ACCELERATOR_KEY, KeyStroke.getKeyStroke(KeyEvent.VK_D, SHORTCUT_KEY_MASK | KeyEvent.SHIFT_MASK));
            URL iconURL = this.getClass().getClassLoader().getResource("toolbarButtonGraphics/media/Stop16.gif");
            putValue(Action.SMALL_ICON, new ImageIcon(iconURL));
        }

        public void actionPerformed(ActionEvent evt) {
            client.disconnect();
        }
    }

    class OpenTclScriptAction extends AbstractAction {
        public OpenTclScriptAction() {
            putValue(Action.NAME, "Open Tcl Script");
            putValue(Action.SHORT_DESCRIPTION, "Opens and executes a Tcl Script");
            putValue(Action.ACCELERATOR_KEY, KeyStroke.getKeyStroke(KeyEvent.VK_O, SHORTCUT_KEY_MASK));
            URL iconURL = this.getClass().getClassLoader().getResource("toolbarButtonGraphics/general/Open16.gif");
            putValue(Action.SMALL_ICON, new ImageIcon(iconURL));
        }

        public void actionPerformed(ActionEvent ect) {
            Frame parent = MainFrame.this;
            FileDialog dlg = new FileDialog(parent, "Open Tcl Script", FileDialog.LOAD);
            dlg.show();
            File f = new File(dlg.getDirectory(), dlg.getFile());

            Cursor oldCursor = getCursor();
            setCursor(new Cursor(Cursor.WAIT_CURSOR));
            client.loadTclScript(f.getAbsolutePath());
            setCursor(oldCursor);
        }
    }

    class LoggingAction extends AbstractAction {
        public LoggingAction() {
            putValue(Action.NAME, "Toggle Logging");
            putValue(Action.SHORT_DESCRIPTION, "Starts or stops logging of ICB to an output file.");
            putValue(Action.ACCELERATOR_KEY, KeyStroke.getKeyStroke(KeyEvent.VK_L, SHORTCUT_KEY_MASK | KeyEvent.SHIFT_MASK));
        }

        public void actionPerformed(ActionEvent evt) {
            if (client.isLoggingEnabled()) {
                client.stopLogging();
            } else {
                try {
                    client.startLogging();
                } catch (IllegalStateException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    class PauseAction extends AbstractAction {
        public PauseAction() {
            putValue(Action.NAME, "Pause Output");
            putValue(Action.SHORT_DESCRIPTION, "Toggles the scrolling of the client output windows.");

            if (!MacOSXIntegrator.isMacOSX()) {
                URL iconURL = this.getClass().getClassLoader().getResource("toolbarButtonGraphics/media/Pause16.gif");
                putValue(Action.SMALL_ICON, new ImageIcon(iconURL));
                putValue(Action.MNEMONIC_KEY, new Integer('P'));
            }
        }

        public void actionPerformed(ActionEvent evt) {
            client.toggleOutputPause();
        }
    }

    class PreferencesAction extends AbstractAction {
        public PreferencesAction() {
            putValue(Action.NAME, "Preferences");
            putValue(Action.SHORT_DESCRIPTION, "Displays application preferences");
            putValue(Action.ACCELERATOR_KEY, KeyStroke.getKeyStroke(KeyEvent.VK_COMMA, SHORTCUT_KEY_MASK));
            URL iconURL = this.getClass().getClassLoader().getResource("toolbarButtonGraphics/general/Preferences16.gif");
            putValue(Action.SMALL_ICON, new ImageIcon(iconURL));
        }

        public void actionPerformed(ActionEvent evt) {
            ICBProperties props = client.getProperties();

            Cursor oldCursor = getCursor();
            setCursor(new Cursor(Cursor.WAIT_CURSOR));

            org.nuclearbunny.icybee.ui.config.PropertiesDialog dlg =
                    new org.nuclearbunny.icybee.ui.config.PropertiesDialog(MainFrame.this, props);
            dlg.pack();
            dlg.setLocationRelativeTo(MainFrame.this);
            dlg.setVisible(true);

            setCursor(oldCursor);

            groupPanel.updateStyles();
            Iterator i = personalPanels.keySet().iterator();
            while (i.hasNext()) {
                String nick = (String) i.next();
                ClientPanel p = (ClientPanel) personalPanels.get(nick);
                p.updateStyles();
            }
        }
    }

    class ExitAction extends AbstractAction {
        public ExitAction() {
            putValue(Action.NAME, UIMessages.messages.getString(UIMessages.ACTION_EXIT_NAME));
            putValue(Action.SHORT_DESCRIPTION, UIMessages.messages.getString(UIMessages.ACTION_EXIT_SHORT_DESCRIPTION));
        }

        public void actionPerformed(ActionEvent evt) {
            exitApp();
        }
    }

    class CopyAction extends AbstractAction {
        public CopyAction() {
            putValue(Action.NAME, UIMessages.messages.getString(UIMessages.ACTION_COPY_NAME));
            putValue(Action.SHORT_DESCRIPTION, UIMessages.messages.getString(UIMessages.ACTION_COPY_SHORT_DESCRIPTION));
            putValue(Action.ACCELERATOR_KEY, KeyStroke.getKeyStroke(KeyEvent.VK_C, SHORTCUT_KEY_MASK));
            URL iconURL = this.getClass().getClassLoader().getResource("toolbarButtonGraphics/general/Copy16.gif");
            putValue(Action.SMALL_ICON, new ImageIcon(iconURL));
        }

        public void actionPerformed(ActionEvent evt) {
            ClientPanel p = (ClientPanel) tabbedPanel.getSelectedComponent();
            String selectedText = p.getSelectedText();
            if (selectedText != null) {
                StringSelection selection = new StringSelection(selectedText);
                clipboard.setContents(selection, null);
            }
        }
    }

    class PasteAction extends AbstractAction {
        public PasteAction() {
            putValue(Action.NAME, UIMessages.messages.getString(UIMessages.ACTION_PASTE_NAME));
            putValue(Action.SHORT_DESCRIPTION, UIMessages.messages.getString(UIMessages.ACTION_PASTE_SHORT_DESCRIPTION));
            putValue(Action.ACCELERATOR_KEY, KeyStroke.getKeyStroke(KeyEvent.VK_V, SHORTCUT_KEY_MASK));
            URL iconURL = this.getClass().getClassLoader().getResource("toolbarButtonGraphics/general/Paste16.gif");
            putValue(Action.SMALL_ICON, new ImageIcon(iconURL));
        }

        public void actionPerformed(ActionEvent evt) {
            try {
                String text = (String) clipboard.getContents(this).getTransferData(DataFlavor.stringFlavor);
                ClientPanel p = (ClientPanel) tabbedPanel.getSelectedComponent();
                p.pasteText(text);
            } catch (Exception ex) {
            }
        }
    }

    class PasteSpecialAction extends AbstractAction {
        public PasteSpecialAction() {
            putValue(Action.NAME, "Paste Special");
            putValue(Action.SHORT_DESCRIPTION, "Performs a special paste from the clipboard");
            putValue(Action.ACCELERATOR_KEY, KeyStroke.getKeyStroke(KeyEvent.VK_V, SHORTCUT_KEY_MASK | KeyEvent.SHIFT_MASK));
        }

        public void actionPerformed(ActionEvent evt) {
            try {
                Cursor oldCursor = getCursor();
                setCursor(new Cursor(Cursor.WAIT_CURSOR));

                String text = (String) clipboard.getContents(this).getTransferData(DataFlavor.stringFlavor);
                ClientPanel p = (ClientPanel) tabbedPanel.getSelectedComponent();
                p.pasteSpecial(text);

                setCursor(oldCursor);
            } catch (Exception ex) {
            }
        }
    }

    class ToolbarAction extends AbstractAction {
        public ToolbarAction() {
            putValue(Action.NAME, UIMessages.messages.getString(UIMessages.ACTION_TOOLBAR_NAME));
            putValue(Action.SHORT_DESCRIPTION, UIMessages.messages.getString(UIMessages.ACTION_TOOLBAR_SHORT_DESCRIPTION));
        }

        public void actionPerformed(ActionEvent evt) {
            boolean visible = !toolBar.isVisible();
            toolBar.setVisible(visible);
            ICBProperties props = client.getProperties();
            props.setToolbarVisible(visible);
        }
    }

    class StatusbarAction extends AbstractAction {
        public StatusbarAction() {
            putValue(Action.NAME, UIMessages.messages.getString(UIMessages.ACTION_STATUSBAR_NAME));
            putValue(Action.SHORT_DESCRIPTION, UIMessages.messages.getString(UIMessages.ACTION_STATUSBAR_SHORT_DESCRIPTION));
        }

        public void actionPerformed(ActionEvent evt) {
            boolean visible = !statusBar.isVisible();
            statusBar.setVisible(visible);
            ICBProperties props = client.getProperties();
            props.setStatusbarVisible(visible);
        }
    }

    class URLGrabberAction extends AbstractAction {
        public URLGrabberAction() {
            putValue(Action.NAME, "URL Grabber");
            putValue(Action.SHORT_DESCRIPTION, "Show or hide the URL Grabber Window");
        }

        public void actionPerformed(ActionEvent evt) {
            boolean visible = !urlWatcher.isVisible();
            urlWatcher.setVisible(visible);
            ICBProperties props = client.getProperties();
            props.setURLGrabberVisible(visible);
        }
    }

    class ReportBugAction extends AbstractAction {
        public ReportBugAction() {
            putValue(Action.NAME, UIMessages.messages.getString(UIMessages.ACTION_REPORTBUG_NAME));
            putValue(Action.SHORT_DESCRIPTION, UIMessages.messages.getString(UIMessages.ACTION_REPORTBUG_SHORT_DESCRIPTION));
        }

        public void actionPerformed(ActionEvent evt) {
            String url = ICBHelp.REPORT_BUG_URL;
            if (!BrowserControl.displayURL(url)) {
                String errorMsgDesc = UIMessages.messages.getString(UIMessages.ERROR_REPORTBUG_BROWSER);
                Object[] args = {url};
                String errorMsg = MessageFormat.format(errorMsgDesc, args);
                JOptionPane.showMessageDialog(MainFrame.this, errorMsg, "Unable to display URL", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    class MailingListAction extends AbstractAction {
        public MailingListAction() {
            putValue(Action.NAME, "Mailing List");
            putValue(Action.SHORT_DESCRIPTION, "Takes you to the mailing list management web page");
        }

        public void actionPerformed(ActionEvent evt) {
            String url = ICBHelp.MAILING_LIST_URL;
            if (!BrowserControl.displayURL(url)) {
                String msg = "Please browse to " + url + " in order to view mailing list information.";
                JOptionPane.showMessageDialog(MainFrame.this, msg, "Unable to display URL", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    class SourceForgeProjectAction extends AbstractAction {
        public SourceForgeProjectAction() {
            putValue(Action.NAME, "SourceForge Project");
            putValue(Action.SHORT_DESCRIPTION, "Takes you to the SourceForge Project page for IcyBee");
        }

        public void actionPerformed(ActionEvent evt) {
            String url = ICBHelp.SOURCEFORGE_PROJECT_URL;
            if (!BrowserControl.displayURL(url)) {
                String msg = "Please browse to " + url + " in order to view SourceForge Project information.";
                JOptionPane.showMessageDialog(MainFrame.this, msg, "Unable to display URL", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    class WhatsNewAction extends AbstractAction {
        public WhatsNewAction() {
            putValue(Action.NAME, "What's New?");
            putValue(Action.SHORT_DESCRIPTION,
                    "Describes what is new in this release of IcyBee");
        }

        public void actionPerformed(ActionEvent evt) {
            Cursor oldCursor = getCursor();
            setCursor(new Cursor(Cursor.WAIT_CURSOR));

            URL whatsnewURL = this.getClass().getClassLoader().getResource("help/whatsnew.html");
            JDialog whatsnewDialog = new HtmlDialog(MainFrame.this, whatsnewURL);
            whatsnewDialog.setSize(400, 400); // XXX ack!
            whatsnewDialog.setLocationRelativeTo(MainFrame.this);
            whatsnewDialog.show();

            setCursor(oldCursor);
        }
    }

    class AboutAction extends AbstractAction {
        public AboutAction() {
            putValue(Action.NAME, "About");
            putValue(Action.SHORT_DESCRIPTION, "Views information about this application");
        }

        public void actionPerformed(ActionEvent evt) {
            Cursor oldCursor = getCursor();
            setCursor(new Cursor(Cursor.WAIT_CURSOR));

            JDialog dialog = new AboutDialog(MainFrame.this);
            dialog.setSize(500, 400);
            dialog.setLocationRelativeTo(MainFrame.this);
            dialog.show();

            setCursor(oldCursor);
        }
    }

    private class EmoticonActionListener implements ActionListener {
        private String emoticonText;

        public EmoticonActionListener(String emoticonText) {
            this.emoticonText = emoticonText;
        }

        public void actionPerformed(ActionEvent evt) {
            ClientPanel panel = (ClientPanel) tabbedPanel.getSelectedComponent();
            panel.inputArea.append(emoticonText);
            panel.inputArea.append(" ");
            panel.inputArea.requestFocus();
        }
    }

    class EmoticonAction extends AbstractAction {
        JPopupMenu emoticonPopupMenu;

        public EmoticonAction() {
            IconManager iconMgr = IconManager.getInstance();

            putValue(Action.NAME, "Emoticons");
            putValue(Action.SHORT_DESCRIPTION, "Inserts an emoticon into the input window");
            putValue(Action.SMALL_ICON, iconMgr.getIcon(":)"));

            emoticonPopupMenu = new JPopupMenu();

            java.util.List emoticonList = iconMgr.getEmoticonList();
            Iterator emoticonIterator = emoticonList.iterator();
            while (emoticonIterator.hasNext()) {
                IconManager.Emoticon emoticon = (IconManager.Emoticon) emoticonIterator.next();

                String itemName = emoticon.description;
                JMenuItem menuItem = new JMenuItem(itemName);
                menuItem.setIcon(emoticon.imageIcon);
                menuItem.addActionListener(new EmoticonActionListener(emoticon.emoticonText));
                emoticonPopupMenu.add(menuItem);
            }
        }

        public void actionPerformed(ActionEvent evt) {
            // XXX all we really want is the location the click came in from...
            int x = emoticonButton.getX() + emoticonButton.getWidth();
            int y = emoticonButton.getY() + emoticonButton.getHeight();
            emoticonPopupMenu.show(MainFrame.this, x, y);
        }
    }

    class MessageWatcher implements MessageListener {
        public void messageReceived(final MessageEvent e) {
            SwingUtilities.invokeLater(new Runnable() {
                public void run() {
                    ICBProperties props = client.getProperties();
                    boolean personalWindows = props.arePersonalWindowsEnabled();
                    boolean ignoreServerEchoback = props.isIgnoreServerEchoback();
                    Packet p = e.getPacket();

                    // convert any CommandOutputPacket messages that are really
                    // echoback messages into an appropriate PrintPacket
                    if ((p instanceof CommandOutputPacket)) {
                        String cmdOut = ((CommandOutputPacket) p).getCommandOutput();
                        if (cmdOut.startsWith("<*to: ")) {
                            String msg = cmdOut.substring(6);
                            int n = msg.indexOf('*');
                            if (n > 0) {
                                String nick = msg.substring(0, n);
                                PrintPacket pp = new PrintPacket(PrintPacket.MSG_TYPE_PERSONAL,
                                        msg.substring(n + 3), // XXX DANGEROUS! skip over "*> "
                                        new String[]{nick});
                                p = pp;
                                e.setPacket(p);
                            }
                        }
                    }

                    // ignore echoback messages from the Server user, if requested
                    if ((p instanceof PrintPacket) && ignoreServerEchoback) {
                        PrintPacket pp = (PrintPacket) p;
                        if (pp.getMsgType() == PrintPacket.MSG_TYPE_PERSONAL) {
                            String[] args = pp.getArgs();
                            if (args.length > 0 && args[0].equalsIgnoreCase("server")) {
                                return;
                            }
                        }
                    }


                    // always route packets to the main panel
                    // TODO: check option and don't display to main panel if personal message
                    //       and option is selected
                    groupPanel.messageReceived(e);

                    // route personal messages to the right subpanel
                    if ((p instanceof PersonalPacket) && personalWindows) {
                        PersonalPacket pp = (PersonalPacket) p;
                        String nick = pp.getNick();

                        getPersonalPanel(nick).messageReceived(e);
                    }

                    // route PrintPacket output messages for are echoback to the
                    // appropriate sub-panel
                    if ((p instanceof PrintPacket) && personalWindows) {
                        PrintPacket pp = (PrintPacket) p;
                        if (pp.getMsgType() == PrintPacket.MSG_TYPE_PERSONAL) {
                            String[] args = pp.getArgs();
                            if (args.length > 0) {
                                String nick = args[0];
                                getPersonalPanel(nick).messageReceived(e);
                            }
                        }
                    }

                    if ((p instanceof StatusPacket)) {
                        StatusPacket pkt = (StatusPacket) p;
                        String statusText = pkt.getStatusText();

                        String groupName = null;
                        boolean patternMatched = false;
                        Matcher matcher = groupPattern.matcher(statusText);
                        if (matcher.matches()) {
                            groupName = matcher.group(1);
                        }

                        if (patternMatched && groupName != null) {
                            tabbedPanel.setTitleAt(0, "Group: " + groupName); // XXX messy
                        }
                    }
                }
            });
        }
    }


    class StatusWatcher implements StatusListener {
        public void statusConnecting(EventObject e) {
            setConnecting();
        }

        public void statusConnected(EventObject e) {
            setConnected();
        }

        public void statusDisconnecting(EventObject e) {
            setDisconnecting();
        }

        public void statusDisconnected(EventObject e) {
            setDisconnected();
        }

        public void statusLoggingStarted(EventObject e) {
        }

        public void statusLoggingStopped(EventObject e) {
        }

        public void statusOutputPaused(EventObject e) {
        }

        public void statusOutputUnpaused(EventObject e) {
        }
    }
}
