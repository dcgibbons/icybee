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

package org.nuclearbunny.icybee.ui;

import org.nuclearbunny.icybee.*;
import org.nuclearbunny.icybee.protocol.*;
import org.nuclearbunny.util.*;

import java.awt.*;
import java.awt.datatransfer.*;
import java.awt.event.*;
import java.net.*;
import java.text.*;
import java.util.*;
import java.util.regex.*;
import javax.swing.*;
import javax.swing.text.*;

public class ClientPanel extends JPanel implements MessageListener {
    public static final String TEXT_OPEN = "text.open";
    public static final String TEXT_OPEN_NICK = "text.open.nick";
    public static final String TEXT_OPEN_URL = "text.open.url";
    public static final String TEXT_PERSONAL = "text.personal";
    public static final String TEXT_PERSONAL_NICK = "text.personal.nick";
    public static final String TEXT_PERSONAL_URL = "text.personal.url";
    public static final String TEXT_COMMAND_OUTPUT = "text.command.output";
    public static final String TEXT_COMMAND_URL = "text.command.url";
    public static final String TEXT_ERROR = "text.error";
    public static final String TEXT_ERROR_HEADER = "text.error.header";
    public static final String TEXT_STATUS = "text.status";
    public static final String TEXT_STATUS_HEADER = "text.status.header";
    public static final String TEXT_TIMESTAMP = "text.timestamp";

    public static final String TEXT_ATTRIBUTE_URL = "url";
    public static final String TEXT_ATTRIBUTE_NICK = "nick";
    public static final String TEXT_ATTRIBUTE_EMOTICON = "emoticon";

    private static final String CLIENT_POPUP_OPEN_PERSONAL = UIMessages.messages.getString(UIMessages.CLIENT_POPUP_OPEN_PERSONAL);
    private static final String CLIENT_POPUP_WHOIS = UIMessages.messages.getString(UIMessages.CLIENT_POPUP_WHOIS);
    private static final String CLIENT_DISCONNECTED_MSG = UIMessages.messages.getString(UIMessages.CLIENT_DISCONNECTED_MSG);

    protected ICBClient client;
    protected OutputTextPane outputArea;
    protected JScrollPane outputScrollPane;
    protected InputTextPane inputArea;
    protected StyledDocument document;
    private Clipboard clipboard;
    private JPopupMenu nickContextMenu;
    private JPopupMenu popupMenu;
    private JMenuItem copyItem;
    private JMenuItem selectAllItem;
    private String clickedText = null;
    private String selectedNick = null;
    private LinkedList urlListeners = new LinkedList();

    // Used to indicate if the output area is currently being auto scrolled
    // whenever data is appended to the area. 
    protected boolean autoScrolling = false;

    public ClientPanel(ICBClient theClient) {
        this.client = theClient;

        clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
        
        /* create the output area */
        document = new DefaultStyledDocument();

        outputArea = new OutputTextPane(this, document);
        outputArea.setEditable(false);
        outputArea.setCaret(new DefaultCaret() {
            // Do not allow the caret to automatically update the viewport
            // position.
            protected void adjustVisibility(Rectangle nloc) {
            }
        });
        outputArea.addMouseListener(new MouseWatcher());

        copyItem = new JMenuItem(UIMessages.MENU_COPY, KeyEvent.VK_C);
        copyItem.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                StringSelection selection = new StringSelection(clickedText);
                clipboard.setContents(selection, null);

                Caret c = outputArea.getCaret();
                c.setSelectionVisible(false);
            }
        });
        selectAllItem = new JMenuItem(UIMessages.MENU_SELECT_ALL, KeyEvent.VK_A);
        selectAllItem.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                Caret c = outputArea.getCaret();
                c.setDot(0);
                c.moveDot(outputArea.getDocument().getLength());
                c.setSelectionVisible(true);
            }
        });
        popupMenu = new JPopupMenu();
        popupMenu.add(copyItem);
        popupMenu.add(selectAllItem);


        JMenuItem openPersonalWindow = new JMenuItem(CLIENT_POPUP_OPEN_PERSONAL, KeyEvent.VK_O);
        openPersonalWindow.setEnabled(false);

        JMenuItem whois = new JMenuItem(CLIENT_POPUP_WHOIS, KeyEvent.VK_W);
        whois.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                StringBuffer buffer = new StringBuffer();
                buffer.append(client.getProperties().getCommandPrefix().charAt(0));
                buffer.append("whois ").append(selectedNick);
                client.sendCommand(buffer.toString());
            }
        });

        nickContextMenu = new JPopupMenu();
        nickContextMenu.add(openPersonalWindow);
        nickContextMenu.add(whois);

        outputScrollPane = new JScrollPane(outputArea);
        outputScrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
        JScrollBar outputScrollBar = outputScrollPane.getVerticalScrollBar();

        outputScrollBar.addAdjustmentListener(new AdjustmentListener() {
            public void adjustmentValueChanged(AdjustmentEvent e) {
                JScrollBar scrollBar = (JScrollBar) e.getSource();
                int endLoc = document.getLength();
                try {
                    Rectangle end = outputArea.modelToView(endLoc);
                    if (end != null) {
                        int n = scrollBar.getValue() + scrollBar.getVisibleAmount();
                        boolean locked = (end.y >= n && n <= (end.y + end.height));

                        // Lock the client's scroll if the user has scrolled
                        // the slider back, and if the system is not currently
                        // auto-scrolling the output panel.
                        if (locked && !autoScrolling) {
                            client.pauseOutput(true);
                        } else if (!locked && !autoScrolling) {
                            client.pauseOutput(false);
                        }
                    }
                } catch (BadLocationException ex) {
                }
            }
        });

        /* create the input area */
        inputArea = new InputTextPane(client, this);
        JScrollPane inputScrollPane = new JScrollPane(inputArea);
        inputScrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);

        /* add the primary components to the root panel */
        setLayout(new BorderLayout());

        JSplitPane splitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT, outputScrollPane, inputScrollPane);
        splitPane.setOneTouchExpandable(true);
        splitPane.setResizeWeight(1.0);

        add(splitPane, BorderLayout.CENTER);

        updateStyles();
    }

    public void requestFocus() {
        inputArea.requestFocus();
    }

    public void clear() {
        try {
            // Note: this Swing method is thread-safe.
            document.remove(0, document.getLength());
        } catch (BadLocationException ex) {
            // XXX
        }
    }

    public String getSelectedText() {
        return outputArea.getSelectedText();
    }

    public void pasteText(String text) {
        inputArea.append(text);
    }

    public void pasteSpecial(String text) {
        inputArea.pasteSpecial(text);
    }

    public void updateStyles() {
        Style textOpenStyle = client.getProperties().getTextStyle(ClientPanel.TEXT_OPEN);

        Color background = StyleConstants.getBackground(textOpenStyle); // TODO: make a separate selection for background?
        Color foreground = StyleConstants.getForeground(textOpenStyle);
        Font f = FontUtility.getFont(textOpenStyle);

        outputArea.setBackground(background);
        outputArea.setForeground(foreground);

        inputArea.setFont(f);
        inputArea.setBackground(background);
        inputArea.setForeground(foreground);
        inputArea.setCaretColor(foreground);
    }

    /**
     * Adds a new URLListener object to the list of objects listening
     * for MessageEvent notifications.
     */
    public void addURLListener(URLListener l) {
        synchronized (this) {
            urlListeners.add(l);
        }
    }

    /**
     * Removes an existing MessageListener object from the list of objects
     * listening for MessageEvent notifications.
     */
    public void removeURLListener(URLListener l) {
        synchronized (this) {
            int n = urlListeners.indexOf(l);
            if (n >= 0) {
                urlListeners.remove(n);
            }
        }
    }

    /*
     * interface MessageListener
     */
    public void messageReceived(final MessageEvent e) {
        if (SwingUtilities.isEventDispatchThread()) {
            processPacket(e.getPacket());
        } else {
            try {
                SwingUtilities.invokeAndWait(new Runnable() {
                    public void run() {
                        processPacket(e.getPacket());
                    }
                });
            } catch (Exception ex) {
                System.err.println("Unable to activate processMessageEvent in Swing event thread, " + ex.getMessage());
            }
        }
    }

    private void fireURLReceived(URLEvent e) {
        synchronized (this) {
            ListIterator i = urlListeners.listIterator();
            while (i.hasNext()) {
                URLListener l = (URLListener) i.next();
                l.urlReceived(e);
            }
        }
    }

    private void processPacket(Packet p) {
        try {
            Caret currentCaret = outputArea.getCaret();
            // XXX remove any currect selection; if this is not done, then several
            // swing errors will occur when trying to redraw the highlighter 
            // as the Dot position is moved. unclear if this is a Swing bug or
            // documented behavior.
            currentCaret.setSelectionVisible(false);

            Style textOpen = client.getProperties().getTextStyle(ClientPanel.TEXT_OPEN);
            document.insertString(document.getLength(), "\n", textOpen);

            displayMessageTimestamp();

            if (p instanceof OpenPacket) {
                displayOpenPacket((OpenPacket) p);

            } else if (p instanceof PersonalPacket) {
                displayPersonalPacket((PersonalPacket) p);

            } else if (p instanceof StatusPacket) {
                displayStatusPacket((StatusPacket) p);

            } else if (p instanceof CommandOutputPacket) {
                displayCommandOutputPacket((CommandOutputPacket) p);

            } else if (p instanceof BeepPacket) {
                displayBeepPacket((BeepPacket) p);

            } else if (p instanceof ProtocolPacket) {
                displayProtocolPacket((ProtocolPacket) p);

            } else if (p instanceof ErrorPacket) {
                displayErrorMessage(((ErrorPacket) p).getErrorText());

            } else if (p instanceof ExitPacket) {
                document.insertString(document.getLength(), CLIENT_DISCONNECTED_MSG, textOpen);

            } else if (p instanceof PrintPacket) {
                PrintPacket pp = (PrintPacket) p;
                switch (pp.getMsgType()) {
                    case PrintPacket.MSG_TYPE_ERROR:
                        displayErrorMessage(p.toString());
                        break;

                    case PrintPacket.MSG_TYPE_PERSONAL:
                        displayPersonalMessage(pp);
                        break;

                    case PrintPacket.MSG_TYPE_OPEN:
                        displayOpenMessage(pp);
                        break;

                    case PrintPacket.MSG_TYPE_NORMAL:
                    default:
                        document.insertString(document.getLength(), p.toString(), textOpen);
                        break;
                }
            }

            // Only scroll the output viewport if the client scroll
            // lock is turned off.
            boolean scrollLocked = client.isOutputPaused();
            if (!scrollLocked) {
                int endLoc = document.getLength();

                currentCaret.setDot(endLoc);

                Rectangle end = outputArea.modelToView(endLoc);
                // A visible rectangle is not available if this component has
                // not yet been rendered. In this case, don't bother scrolling
                // the viewport.
                if (end != null) {
                    autoScrolling = true;
                    outputArea.scrollRectToVisible(end);
                    autoScrolling = false;
                }
            }

        } catch (BadLocationException ex) {
            // XXX
        }
    }

    protected void submitInput(String s) {
        client.sendCommand(s);
    }

/*
	private void insertIcon(Icon icon) {
		outputArea.setEditable(true);
		outputArea.setCaretPosition(document.getEndPosition().getOffset()-1);
		outputArea.insertIcon(icon);
		outputArea.setEditable(false);
	}
*/

    /**
     * Displays text, taking into account any URLs embedded in the text string.
     * @param text text buffer to add to the client panel
     * @param textStyle text style to use when displaying text
     * @throws BadLocationException
     */
    private void displayText(String text, Style textStyle) throws BadLocationException {
        Matcher matcher = URLMatcher.URL_PATTERN.matcher(text);

        int lastMatch = 0;
        while (matcher.find(lastMatch)) {
            String urlText = matcher.group(1);

            int matchBeginOffset = matcher.start();
            int matchEndOffset = matcher.end();
            String prefix = text.substring(lastMatch, matchBeginOffset);
            lastMatch = matchEndOffset;

            document.insertString(document.getLength(), prefix, textStyle);

            Style a = (Style) textStyle.copyAttributes();
            StyleConstants.setUnderline(a, true);
            a.addAttribute(ClientPanel.TEXT_ATTRIBUTE_URL, "URL");

            document.insertString(document.getLength(), urlText, a);

            URL url = URLMatcher.getURL(urlText);
            if (url != null) {
                fireURLReceived(new URLEvent(this, url));
            }
        }

        String remainingText = text.substring(lastMatch);
        document.insertString(document.getLength(), remainingText, textStyle);
    }

    /**
     * Displays text, taking into account emoticons which should be displayed
     * as graphical icons instead of text.
     */
    private void displayTextWithEmoticons(String text, Style textStyle) throws BadLocationException {
        boolean emoticonsEnabled = client.getProperties().areEmoticonsEnabled();
        //boolean animatedEmoticonsEnabled = client.getProperties().areAnimatedEmoticonsEnabled();

        if (!emoticonsEnabled) {
            displayText(text, textStyle);
        } else {
            Pattern pattern = IconManager.getInstance().getPattern();

            int lastMatch = 0;
            Matcher matcher = pattern.matcher(text);
            while (matcher.find(lastMatch)) {
                String emoticon = matcher.group(1);

                int matchBeginOffset = matcher.start();
                int matchEndOffset = matcher.end();
                String prefix = text.substring(lastMatch, matchBeginOffset);
                lastMatch = matchEndOffset;

                // Insert any text before the emoticon into the document.
                displayText(prefix, textStyle);

                // Insert the emoticon icon, or the emoticon text if the icon is
                // not available for any reason.
                ImageIcon icon = IconManager.getInstance().getIcon(emoticon);
                if (icon == null) {
                    displayText(emoticon, textStyle);
                } else {
                    Style a = (Style) textStyle.copyAttributes();
                    StyleConstants.setIcon(a, icon);
                    a.addAttribute(ClientPanel.TEXT_ATTRIBUTE_EMOTICON, "emoticon");
                    displayText(emoticon, a);
                }
            }

            // Insert any text remaining after the emoticon into the
            // document. The pattern matcher will automatically limit
            // this portion of the string so it doesn't run into the
            // next matched emoticon.
            String remainingText = text.substring(lastMatch);
            displayText(remainingText, textStyle);
        }
    }

    private void displayMessageTimestamp() throws BadLocationException {
        if (client.getProperties().areMessageTimestampsEnabled()) {
            Style textStyle = client.getProperties().getTextStyle(ClientPanel.TEXT_TIMESTAMP);

            SimpleDateFormat dateFormat = new SimpleDateFormat(client.getProperties().getMessageTimestampsFormat());
            dateFormat.setTimeZone(TimeZone.getDefault());
            String timestamp = dateFormat.format(new Date()) + " ";
            document.insertString(document.getLength(), timestamp, textStyle);
        }
    }

    private void displayOpenPacket(OpenPacket p) throws BadLocationException {
        Style textOpen = client.getProperties().getTextStyle(ClientPanel.TEXT_OPEN);
        Style textOpenNick = client.getProperties().getTextStyle(ClientPanel.TEXT_OPEN_NICK);

        document.insertString(document.getLength(), "<", textOpenNick);

        textOpenNick.addAttribute(ClientPanel.TEXT_ATTRIBUTE_NICK, "This is user's nick.");
        displayTextWithEmoticons(p.getNick(), textOpenNick);
        textOpenNick.removeAttribute(ClientPanel.TEXT_ATTRIBUTE_NICK);

        document.insertString(document.getLength(), "> ", textOpenNick);
        displayTextWithEmoticons(p.getText(), textOpen);
    }

    // TODO: this is one of those hacks I put in here to deal with c_print...
    private void displayOpenMessage(PrintPacket p) throws BadLocationException {
        Style textOpen = client.getProperties().getTextStyle(ClientPanel.TEXT_OPEN);
        Style textOpenNick = client.getProperties().getTextStyle(ClientPanel.TEXT_OPEN_NICK);

        document.insertString(document.getLength(), "-> ", textOpenNick);
        displayTextWithEmoticons(p.toString(), textOpen);
    }

    private void displayPersonalPacket(PersonalPacket p) throws BadLocationException {
        Style textPersonal = client.getProperties().getTextStyle(ClientPanel.TEXT_PERSONAL);
        Style textPersonalNick = client.getProperties().getTextStyle(ClientPanel.TEXT_PERSONAL_NICK);

        String nick = p.getNick();
        String text = p.getText();

        document.insertString(document.getLength(), "<*", textPersonalNick);

        textPersonalNick.addAttribute(ClientPanel.TEXT_ATTRIBUTE_NICK, "This is user's nick.");
        displayTextWithEmoticons(nick, textPersonalNick);
        textPersonalNick.removeAttribute(ClientPanel.TEXT_ATTRIBUTE_NICK);

        document.insertString(document.getLength(), "*> ", textPersonalNick);

        displayTextWithEmoticons(text, textPersonal);

        if (client.getProperties().isAddIncomingToHistoryEnabled()) {
            client.addUserHistory(nick);
        }
    }

    private void displayPersonalMessage(PrintPacket p) throws BadLocationException {
        String[] args = p.getArgs();
        if (args.length >= 1) {
            String nick = args[0];
            String text = p.toString();

            Style textPersonal = client.getProperties().getTextStyle(ClientPanel.TEXT_PERSONAL);
            Style textPersonalNick = client.getProperties().getTextStyle(ClientPanel.TEXT_PERSONAL_NICK);

            document.insertString(document.getLength(), "<*to: ", textPersonalNick);

            textPersonalNick.addAttribute(ClientPanel.TEXT_ATTRIBUTE_NICK, "This is user's nick.");
            displayTextWithEmoticons(nick, textPersonalNick);
            textPersonalNick.removeAttribute(ClientPanel.TEXT_ATTRIBUTE_NICK);

            document.insertString(document.getLength(), "*> ", textPersonalNick);
            displayTextWithEmoticons(text, textPersonal);
        }
    }

    private void displayStatusPacket(StatusPacket p) throws BadLocationException {
        Style textStatus = client.getProperties().getTextStyle(ClientPanel.TEXT_STATUS);
        Style textStatusHeader = client.getProperties().getTextStyle(ClientPanel.TEXT_STATUS_HEADER);

        document.insertString(document.getLength(), "[=", textStatusHeader);
        document.insertString(document.getLength(), p.getStatusHeader(), textStatusHeader);
        document.insertString(document.getLength(), "=] ", textStatusHeader);
        displayTextWithEmoticons(p.getStatusText(), textStatus);
    }

    private void displayCommandOutputPacket(CommandOutputPacket p) throws BadLocationException {
        Style textCommandOutput = client.getProperties().getTextStyle(ClientPanel.TEXT_COMMAND_OUTPUT);
        displayTextWithEmoticons(p.getCommandOutput(), textCommandOutput);
    }

    private void displayBeepPacket(BeepPacket p) throws BadLocationException {
        Style textStatus = client.getProperties().getTextStyle(ClientPanel.TEXT_STATUS);
        Style textStatusHeader = client.getProperties().getTextStyle(ClientPanel.TEXT_STATUS_HEADER);

        document.insertString(document.getLength(), "[=Beep!=] ", textStatusHeader);

        textStatus.addAttribute(ClientPanel.TEXT_ATTRIBUTE_NICK, "This is user's nick.");
        document.insertString(document.getLength(), p.getNick(), textStatus);
        textStatus.removeAttribute(ClientPanel.TEXT_ATTRIBUTE_NICK);

        document.insertString(document.getLength(), " has sent you a beep!", textStatus);

        // TODO: check to see if audible beeps are enabled
        Toolkit.getDefaultToolkit().beep();
    }

    private void displayProtocolPacket(ProtocolPacket p) throws BadLocationException {
        Style textCommandOutput = client.getProperties().getTextStyle(ClientPanel.TEXT_COMMAND_OUTPUT);

        document.insertString(document.getLength(), "Connected to the ", textCommandOutput);
        displayTextWithEmoticons(p.getServerName(), textCommandOutput);
        document.insertString(document.getLength(), " server (", textCommandOutput);
        displayTextWithEmoticons(p.getServerDesc(), textCommandOutput);
        document.insertString(document.getLength(), ")", textCommandOutput);
    }

    private void displayErrorMessage(String msg) throws BadLocationException {
        Style textError = client.getProperties().getTextStyle(ClientPanel.TEXT_ERROR);
        Style textErrorHeader = client.getProperties().getTextStyle(ClientPanel.TEXT_ERROR_HEADER);

        document.insertString(document.getLength(), "[=Error=] ", textErrorHeader);
        displayTextWithEmoticons(msg, textError);
    }

    /*
    private String getTextFromPoint(Point pt) {
        int pos = outputArea.viewToModel(pt);
        String text = null;

        try {
            StyledDocument doc = (StyledDocument) outputArea.getDocument();
            int start = doc.getStartPosition().getOffset();
            int end = doc.getEndPosition().getOffset();

            // XXX this algorithm needs to be internationalized
            int wordStart = pos;
            while (wordStart > start) {
                if (Character.isWhitespace(doc.getText(wordStart, 1).charAt(0))) {
                    break;
                }

                wordStart--;
            }

            int wordEnd = pos;
            while (wordEnd < end) {
                if (Character.isWhitespace(doc.getText(wordEnd, 1).charAt(0))) {
                    break;
                }

                wordEnd++;
            }

            text = doc.getText(wordStart, wordEnd-wordStart).trim(); // XXX hmm, trim...
        } catch (BadLocationException ex) {
            // TODO ?
        }

        return text;
    }
    */

    private String getTextFromPointWithAttribute(Point pt, Object textAttribute) {
        int pos = outputArea.viewToModel(pt);
        String text = null;

        try {
            StyledDocument doc = (StyledDocument) outputArea.getDocument();
            int start = doc.getStartPosition().getOffset();
            int end = doc.getEndPosition().getOffset();

            int wordStart = pos;
            while (wordStart > start) {
                Element e = doc.getCharacterElement(wordStart - 1);
                AttributeSet a = e.getAttributes();
                if (a.getAttribute(textAttribute) == null) {
                    break;
                }
                wordStart--;
            }

            int wordEnd = pos;
            while (wordEnd < end) {
                Element e = doc.getCharacterElement(wordEnd);
                AttributeSet a = e.getAttributes();
                if (a.getAttribute(textAttribute) == null) {
                    break;
                }
                wordEnd++;
            }

            text = doc.getText(wordStart, wordEnd - wordStart);
        } catch (BadLocationException ex) {
        }

        return text;
    }

    private boolean isClickableElement(Point pt) {
        boolean clickable = false;

        int pos = outputArea.viewToModel(pt);
        StyledDocument doc = (StyledDocument) outputArea.getDocument();
        Element element = doc.getCharacterElement(pos);
        AttributeSet a = element.getAttributes();
        if (a.getAttribute(ClientPanel.TEXT_ATTRIBUTE_URL) != null) {
            clickable = true;
        } else if (a.getAttribute(ClientPanel.TEXT_ATTRIBUTE_NICK) != null) {
            clickable = true;
        }

        return clickable;
    }


    private void processClickedURL(Point pt, boolean rightClicked) {
        String text = getTextFromPointWithAttribute(pt, ClientPanel.TEXT_ATTRIBUTE_URL);
        URL url = URLMatcher.getURL(text);
        if (url != null) {
            Cursor c = getCursor();
            setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));

            if (!BrowserControl.displayURL(url)) {
                JOptionPane.showMessageDialog(outputArea, url.toExternalForm(), "Unable to display URL", // TODO
                        JOptionPane.ERROR_MESSAGE);
            }

            setCursor(c);
        }
    }

    private void processClickedNick(Point pt, boolean rightClick) {
        String text = getTextFromPointWithAttribute(pt, ClientPanel.TEXT_ATTRIBUTE_NICK);

        if (rightClick) {
            selectedNick = text;
            nickContextMenu.show(outputArea, (int) pt.getX(), (int) pt.getY());

        } else {
            inputArea.setNick(text);
            inputArea.requestFocus();
        }
    }

    class MouseWatcher extends MouseAdapter {
        private MouseMotionWatcher motionWatcher = new MouseMotionWatcher();

        public void mouseClicked(MouseEvent e) {
            int mods = e.getModifiers();
            Point pt = e.getPoint();

            // See if the output area was "right clicked"
            if ((mods & InputEvent.BUTTON3_MASK) != 0) {

                int pos = outputArea.viewToModel(pt);
                StyledDocument doc = (StyledDocument) outputArea.getDocument();
                Element element = doc.getCharacterElement(pos);
                AttributeSet a = element.getAttributes();
                if (a.getAttribute(ClientPanel.TEXT_ATTRIBUTE_URL) != null) {
                    processClickedURL(pt, true);
                } else if (a.getAttribute(ClientPanel.TEXT_ATTRIBUTE_NICK) != null) {
                    processClickedNick(pt, true);
                } else {
                    Caret c = outputArea.getCaret();

                    // configure our values so direction of selection does not matter
                    int mark = Math.min(c.getMark(), c.getDot());
                    int dot = Math.max(c.getMark(), c.getDot());

                    if (c.isSelectionVisible() && mark != dot && mark <= pos && pos <= dot) {
                        int length = c.getDot() - c.getMark();
                        try {
                            clickedText = doc.getText(c.getMark(), length);
                        } catch (BadLocationException ex) {
                        }

                        copyItem.setEnabled(true);
                    } else {
                        copyItem.setEnabled(false);
                    }

                    popupMenu.show(outputArea, e.getX(), e.getY());
                }
            } else if ((mods & InputEvent.BUTTON1_MASK) != 0) {
                int pos = outputArea.viewToModel(pt);
                StyledDocument doc = (StyledDocument) outputArea.getDocument();
                Element element = doc.getCharacterElement(pos);
                AttributeSet a = element.getAttributes();
                if (a.getAttribute(ClientPanel.TEXT_ATTRIBUTE_URL) != null) {
                    processClickedURL(pt, false);
                } else if (a.getAttribute(ClientPanel.TEXT_ATTRIBUTE_NICK) != null) {
                    processClickedNick(pt, false);
                }
            }
        }

        public void mouseEntered(MouseEvent e) {
            outputArea.addMouseMotionListener(motionWatcher);
        }

        public void mouseExited(MouseEvent e) {
            outputArea.removeMouseMotionListener(motionWatcher);
        }
    }


    class MouseMotionWatcher extends MouseMotionAdapter {
        private Cursor savedCursor = null;

        public void mouseMoved(MouseEvent e) {
            Point pt = e.getPoint();

            if (isClickableElement(pt)) {
                if (savedCursor == null) {
                    savedCursor = outputArea.getCursor();
                }
                Cursor c = Cursor.getPredefinedCursor(Cursor.HAND_CURSOR);
                outputArea.setCursor(c);
            } else if (savedCursor != null) {
                outputArea.setCursor(savedCursor);
                savedCursor = null;
            }
        }
    }
}
