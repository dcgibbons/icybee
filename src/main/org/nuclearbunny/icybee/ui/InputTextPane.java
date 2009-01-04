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

package org.nuclearbunny.icybee.ui;

import org.nuclearbunny.icybee.ICBClient;
import org.nuclearbunny.icybee.net.URLShrinker;
import org.nuclearbunny.util.URLMatcher;

import javax.swing.*;
import javax.swing.text.BadLocationException;
import javax.swing.text.Document;
import javax.swing.text.Position;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.io.IOException;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

class InputTextPane extends JTextArea {
    private static final int INPUT_TEXT_ROWS = 2;
    private static final int INPUT_HISTORY_SIZE = 100; // TODO: make this configurable

    private static final KeyStroke UP_KEYSTROKE = KeyStroke.getKeyStroke(KeyEvent.VK_UP, 0);
    private static final KeyStroke DOWN_KEYSTROKE = KeyStroke.getKeyStroke(KeyEvent.VK_DOWN, 0);
    private static final KeyStroke TAB_KEYSTROKE = KeyStroke.getKeyStroke(KeyEvent.VK_TAB, 0);
    private static final KeyStroke ENTER_KEYSTROKE = KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0);
    private static final KeyStroke CTRL_P_KEYSTROKE = KeyStroke.getKeyStroke(KeyEvent.VK_P, Event.CTRL_MASK);

    private static final Pattern COMMAND_PATTERN = Pattern.compile("(\\S*)\\s*(\\S*)(.*)");
    private static final int COMMAND_CMD_GROUP = 1;
    private static final int COMMAND_NICK_GROUP = 2;
    private static final int COMMAND_ARGS_GROUP = 3;

    private final ICBClient theClient;
    private final ClientPanel thePanel;
    private URLShrinker urlShrinker;
    private String savedInputBuffer;
    private ArrayList inputHistory;
    private int inputHistoryIndex;

    public InputTextPane(ICBClient client, ClientPanel clientPanel) {
        super();

        theClient = client;
        thePanel = clientPanel;
        urlShrinker = new org.nuclearbunny.icybee.net.impl.TinyURLImpl(); // TODO: pick based on preferences
        inputHistory = new ArrayList(InputTextPane.INPUT_HISTORY_SIZE);
        inputHistoryIndex = 0;

        setRows(InputTextPane.INPUT_TEXT_ROWS);
        setLineWrap(true);
        setWrapStyleWord(true);

        InputMap parentInputMap = getInputMap(JComponent.WHEN_FOCUSED);
        InputMap inputMap = new InputMap();
        inputMap.setParent(parentInputMap);

        // TODO: these should be configurable
        inputMap.put(UP_KEYSTROKE, "custom-up");
        inputMap.put(DOWN_KEYSTROKE, "custom-down");
        inputMap.put(TAB_KEYSTROKE, "user-history");
        inputMap.put(ENTER_KEYSTROKE, "submit-input");
        inputMap.put(CTRL_P_KEYSTROKE, "pause-output");
        setInputMap(JComponent.WHEN_FOCUSED, inputMap);

        ActionMap actionMap = getActionMap();
        actionMap.put("custom-up", new CustomUpAction());
        actionMap.put("custom-down", new CustomDownAction());
        actionMap.put("user-history", new UserHistoryAction());
        actionMap.put("submit-input", new SubmitInputAction());
        actionMap.put("pause-output", new PauseOutputAction());
    }

    public void setNick(String newNick) {
        Cursor c = getCursor();
        setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));

        String commandPrefix = theClient.getProperties().getCommandPrefix();

        String currentText = getDocText();

        StringBuffer newText = null;

        if (currentText.startsWith(commandPrefix)) {
            String commandText = currentText.substring(commandPrefix.length());
            Matcher matcher = COMMAND_PATTERN.matcher(commandText);
            boolean patternMatched = matcher.matches();
            if (patternMatched) {
                String command = matcher.group(COMMAND_CMD_GROUP);
                String args = matcher.group(COMMAND_ARGS_GROUP);
                newText = new StringBuffer(commandPrefix);
                newText.append(command);
                newText.append(' ').append(newNick);
                if (args == null || args.length() == 0) {
                    newText.append(' ');
                } else {
                    newText.append(args);
                }
            } else {
                Toolkit.getDefaultToolkit().beep();
            }
        } else {
            newText = new StringBuffer(commandPrefix);
            newText.append("m ").append(newNick).append(' ');
            newText.append(currentText);
        }

        if (newText != null) {
            setDocText(newText.toString());
        }

        setCursor(c);
    }

    protected void pasteSpecial(final String pasteBuffer) {
        Cursor c = getCursor();
        setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));

        StringBuffer buffer = new StringBuffer(pasteBuffer.length());

        Matcher matcher = URLMatcher.URL_PATTERN.matcher(pasteBuffer);
        int lastMatch = 0;
        while (matcher.find(lastMatch)) {
            String urlText = matcher.group(1);

            int matchBeginOffset = matcher.start();
            int matchEndOffset = matcher.end();
            String prefix = pasteBuffer.substring(lastMatch, matchBeginOffset);
            lastMatch = matchEndOffset;

            buffer.append(prefix);
            buffer.append(shrinkURL(urlText));
        }
        buffer.append(pasteBuffer.substring(lastMatch));

        append(buffer.toString());

        setCursor(c);
    }

    private String getDocText() {
        String docText;
        try {
            Document doc = getDocument();
            docText = doc.getText(0, doc.getLength()).trim();
        } catch (BadLocationException ex) {
            docText = "";
        }
        return docText;
    }

    private void setDocText(String text) {
        try {
            Document doc = getDocument();
            doc.remove(0, doc.getLength());
            doc.insertString(0, text, null);
        } catch (BadLocationException ex) {
            // NO-OP
        }
    }

    private String getInputBuffer() {
        String buffer;
        try {
            Document doc = getDocument();
            int n = doc.getLength();
            buffer = doc.getText(0, n);
            doc.remove(0, n);
        } catch (BadLocationException ex) {
            // NO-OP
            buffer = "";
        }
        return buffer;
    }

    private void addToInputHistory(String inputBuffer) {
        int n = inputHistory.size();
        if (n == InputTextPane.INPUT_HISTORY_SIZE) {
            inputHistory.remove(0);
        }
        inputHistory.add(inputBuffer);
        inputHistoryIndex = inputHistory.size();
    }

    /**
     * We ask the UI component if a standard move backward/up would actually move the
     * caret position. If not, then we know we're at the logical end of the buffer's
     * allowed up movement and it's okay to attempt a history scroll.
     */
    private boolean canScrollBackward() {
        boolean scrollable = false;
        try {
            Position.Bias[] bias = new Position.Bias[1];
            int nextPos = getUI().getNextVisualPositionFrom(this, getCaretPosition(),
                    Position.Bias.Backward, SwingConstants.NORTH, bias);
            scrollable = (nextPos == getCaretPosition());
        } catch (BadLocationException ex) {
            // TODO: now what?
        }
        return scrollable;
    }

    /**
     * We ask the UI component if a standard move forward/down would actually move the
     * carent position. If not, then we know we're at the logical end of the buffer's
     * allowed down movement and it's okay to attempt a history scroll.
     */
    private boolean canScrollForward() {
        boolean scrollable = false;
        try {
            Position.Bias[] bias = new Position.Bias[1];
            int nextPos = getUI().getNextVisualPositionFrom(this, getCaretPosition(),
                    Position.Bias.Forward, SwingConstants.SOUTH, bias);
            scrollable = (nextPos == getCaretPosition());
        } catch (BadLocationException ex) {
            // TODO: now what?
        }
        return scrollable;
    }

    private boolean processUpEvent() {
        boolean processed = false;

        try {
            if (canScrollBackward()) {
                processed = true;
                if (inputHistoryIndex == 0) {
                    Toolkit.getDefaultToolkit().beep();
                } else {
                    // If we're at the end of the history and scrolling back, then we need to
                    // make sure we save any currently entered text so the user may return to
                    // it later. This is cached outside of the inputHistory list so that it
                    // won't be saved in case the user selects an older history item to use as
                    // input.
                    if (inputHistoryIndex == inputHistory.size()) {
                        savedInputBuffer = getInputBuffer();
                    }

                    inputHistoryIndex--;
                    String inputBuffer = inputHistory.get(inputHistoryIndex).toString();
                    Document doc = getDocument();
                    doc.remove(0, doc.getLength());
                    doc.insertString(0, inputBuffer, null);
                    setCaretPosition(0);
                }
            }
        } catch (BadLocationException ex) {
            // TODO: now what?
        }

        return processed;
    }

    private boolean processDownEvent() {
        boolean processed = false;

        try {
            if (canScrollForward()) {
                processed = true;
                if (inputHistoryIndex < inputHistory.size() - 1) {
                    inputHistoryIndex++;
                    String inputBuffer = inputHistory.get(inputHistoryIndex).toString();
                    Document doc = getDocument();
                    doc.remove(0, doc.getLength());
                    doc.insertString(0, inputBuffer, null);
                    setCaretPosition(0);
                } else if (inputHistoryIndex == inputHistory.size() - 1) {
                    inputHistoryIndex = inputHistory.size();
                    if (savedInputBuffer != null) {
                        Document doc = getDocument();
                        doc.remove(0, doc.getLength());
                        doc.insertString(0, savedInputBuffer, null);
                        setCaretPosition(0);
                        savedInputBuffer = null;
                    }
                } else {
                    Toolkit.getDefaultToolkit().beep();
                }
            }
        } catch (BadLocationException ex) {
        }

        return processed;
    }

    private void handleTabSelection() {
        String nextNick = theClient.getNextUserFromHistory(true);
        if (nextNick != null) {
            setNick(nextNick);
        } else {
            Toolkit.getDefaultToolkit().beep();
            return;
        }
    }

    private class CustomUpAction extends AbstractAction {
        public void actionPerformed(ActionEvent e) {
            if (!processUpEvent()) {
                InputMap im = getInputMap().getParent();
                Object actionKey = im.get(UP_KEYSTROKE);
                ActionMap am = getActionMap();
                Action a = am.get(actionKey);
                if (a != null) {
                    a.actionPerformed(e);
                }
            }
        }
    }

    private class CustomDownAction extends AbstractAction {
        public void actionPerformed(ActionEvent e) {
            if (!processDownEvent()) {
                InputMap im = getInputMap().getParent();
                Object actionKey = im.get(DOWN_KEYSTROKE);
                ActionMap am = getActionMap();
                Action a = am.get(actionKey);
                if (a != null) {
                    a.actionPerformed(e);
                }
            }
        }
    }

    private class UserHistoryAction extends AbstractAction {
        public void actionPerformed(ActionEvent e) {
            handleTabSelection();
        }
    }

    private class SubmitInputAction extends AbstractAction {
        public void actionPerformed(ActionEvent e) {
            String inputBuffer = getInputBuffer();
            savedInputBuffer = null;
            addToInputHistory(inputBuffer);
            thePanel.submitInput(inputBuffer);
        }
    }

    private class PauseOutputAction extends AbstractAction {
        public void actionPerformed(ActionEvent e) {
            theClient.toggleOutputPause();
        }
    }

    private String shrinkURL(String url) {
        String shrunkURL = url;
        if (url.length() > urlShrinker.getMinURLLen()) {
            // TODO: ideally, when shrinking a URL we should give the user
            // some feedback in the status bar, and perhaps offer a stop
            // button so that they may cancel the operation of the shrinker
            // is slow to respond
            try {
                shrunkURL = urlShrinker.shrinkURL(url);
            } catch (IOException ex) {
                shrunkURL = url;
            }
        }
        return shrunkURL;
    }
}
