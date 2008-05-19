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

package org.nuclearbunny.icybee.ui;

import org.nuclearbunny.util.BrowserControl;
import org.nuclearbunny.icybee.ui.util.JDialogHelper;
import org.nuclearbunny.icybee.ui.util.ActionHelper;

import java.awt.*;
import java.awt.event.*;
import java.net.*;
import java.io.*;
import java.text.*;
import javax.swing.*;
import javax.swing.border.*;
import javax.swing.event.*;

public class HtmlDialog extends JDialogHelper {
    private static final Border emptyBorder = BorderFactory.createEmptyBorder(5, 5, 5, 5);

    private JButton okayButton;
    private JEditorPane editorPane;

    public HtmlDialog(Frame owner) {
        super(owner, true);
        initDialog();
    }

    public HtmlDialog(Frame owner, URL documentURL) {
        super(owner, true);
        initDialog();

        try {
            editorPane.setPage(documentURL);
        } catch (IOException ex) {
            System.err.println(ex.getMessage());
            ex.printStackTrace(System.err);
        }
    }

    public HtmlDialog(Frame owner, String text) {
        super(owner, true);
        initDialog();
        setText(text);
    }

    public void setText(String text) {
        editorPane.setContentType("text/html");
        editorPane.setText(text);
    }

    private void initDialog() {
        okayButton = new JButton(new ActionHelper(UIMessages.messages, UIMessages.OK_ACTION) {
            public void actionPerformed(ActionEvent event) {
                dispose();
            }
        });
        okayButton.addActionListener(getDismissAction());
        getRootPane().setDefaultButton(okayButton);

        editorPane = new JEditorPane();
        editorPane.setEditable(false);
        setCancelKey(editorPane);
        editorPane.addFocusListener(new FocusAdapter() {
            public void focusGained(FocusEvent event) {
                okayButton.requestFocusInWindow();
            }
        });

        editorPane.addHyperlinkListener(new HyperlinkListener() {
            public void hyperlinkUpdate(HyperlinkEvent e) {
                if (e.getEventType() == HyperlinkEvent.EventType.ACTIVATED) {
                    JEditorPane pane = (JEditorPane) e.getSource();
                    URL url = e.getURL();
                    if (url == null) {
                        return;
                    } else {
                        if (!BrowserControl.displayURL(url)) {
                            try {
                                pane.setPage(url);
                            } catch (Throwable t) {
                                String errorMsgTitle = UIMessages.messages.getString(UIMessages.ERROR_LAUNCH_URL_TITLE);
                                String errorMsgDesc = UIMessages.messages.getString(UIMessages.ERROR_LAUNCH_URL);
                                Object[] args = {url.toExternalForm()};
                                String errorMsg = MessageFormat.format(errorMsgDesc, args);
                                JOptionPane.showMessageDialog(HtmlDialog.this.getOwner(), errorMsg,
                                                              errorMsgTitle, JOptionPane.ERROR_MESSAGE);
                            }
                        }
                    }
                }
            }
        });

        JScrollPane scrollPane = new JScrollPane(editorPane);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);

        JPanel p = new JPanel(new GridLayout(1, 0, 5, 5));
        p.add(okayButton);
        JPanel buttonPanel = new JPanel(new BorderLayout());
        buttonPanel.add(p, BorderLayout.EAST);
        buttonPanel.setBorder(emptyBorder);

        Container contentPane = getContentPane();
        contentPane.setLayout(new BorderLayout());
        contentPane.add(scrollPane, BorderLayout.CENTER);
        contentPane.add(buttonPanel, BorderLayout.SOUTH);
    }
}
