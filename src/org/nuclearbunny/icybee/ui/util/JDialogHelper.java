/*
 * $Id: JDialogHelper.java,v 1.1 2004/09/19 16:08:15 dcgibbons Exp $
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

package org.nuclearbunny.icybee.ui.util;

import javax.swing.*;
import java.awt.event.WindowEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.awt.*;

public class JDialogHelper extends JDialog {
    protected static final String ESCAPE_ACTION_NAME = "escape";

    private Action dismissAction;

    public JDialogHelper(Frame owner, boolean modal) {
        super(owner, modal);
    }

    protected void dialogInit() {
        super.dialogInit();

        addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                getDismissAction().actionPerformed(new ActionEvent(this, -1, "windowClosing"));
            }
        });
    }

    protected JRootPane createRootPane() {
        JRootPane rootPane = super.createRootPane();
        setCancelKey(rootPane);
        return rootPane;
    }

    protected void setCancelKey(JComponent c) {
        KeyStroke keyStroke = KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0);
        c.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(keyStroke, ESCAPE_ACTION_NAME);
        c.getActionMap().put(ESCAPE_ACTION_NAME, getDismissAction());
    }

    protected Action getDismissAction() {
        if (dismissAction == null) {
            dismissAction = new AbstractAction() {
                public void actionPerformed(ActionEvent e) {
                    dispose();
                }
            };
        }
        return dismissAction;
    }
}
