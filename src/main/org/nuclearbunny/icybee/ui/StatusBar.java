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

import java.awt.*;
import java.awt.event.*;
import java.util.*;
import java.text.MessageFormat;

import javax.swing.*;
import javax.swing.border.*;

public class StatusBar extends JPanel implements StatusListener {
    private static final int TIMER_INTERVAL = 1000;
    private static final String STATUSBAR_WELCOME = UIMessages.messages.getString(UIMessages.STATUSBAR_WELCOME);
    private static final String STATUSBAR_CONNECTED = UIMessages.messages.getString(UIMessages.STATUSBAR_CONNECTED);
    private static final String STATUSBAR_CONNECTED_TIME = UIMessages.messages.getString(UIMessages.STATUSBAR_CONNECTED_TIME);
    private static final String STATUSBAR_CONNECTING = UIMessages.messages.getString(UIMessages.STATUSBAR_CONNECTING);
    private static final String STATUSBAR_DISCONNECTED = UIMessages.messages.getString(UIMessages.STATUSBAR_DISCONNECTED);
    private static final String STATUSBAR_DISCONNECTING = UIMessages.messages.getString(UIMessages.STATUSBAR_DISCONNECTING);
    private static final String STATUSBAR_LOGGING_ON = UIMessages.messages.getString(UIMessages.STATUSBAR_LOGGING_ON);
    private static final String STATUSBAR_LOGGING_OFF = UIMessages.messages.getString(UIMessages.STATUSBAR_LOGGING_OFF);
    private static final String STATUSBAR_SCROLL_ON = UIMessages.messages.getString(UIMessages.STATUSBAR_SCROLL_ON);
    private static final String STATUSBAR_SCROLL_OFF = UIMessages.messages.getString(UIMessages.STATUSBAR_SCROLL_OFF);
    private static final String STATUSBAR_IDLE_TIME = UIMessages.messages.getString(UIMessages.STATUSBAR_IDLE_TIME);

    private ICBClient client;

    private JLabel connectedMessage;
    private JLabel loggingMessage;
    private JLabel pausedMessage;
    private JLabel idleMessage;
    private JLabel elapsedMessage;

    private javax.swing.Timer timer;
    private long connectedTime;

    public StatusBar(ICBClient client) {
        this.client = client;
        client.addStatusListener(this);

        Border bevelBorder = BorderFactory.createLoweredBevelBorder();

        connectedMessage = new JLabel(STATUSBAR_WELCOME);
        connectedMessage.setBorder(bevelBorder);

        loggingMessage = new JLabel(STATUSBAR_LOGGING_OFF);
        loggingMessage.setBorder(bevelBorder);

        pausedMessage = new JLabel(STATUSBAR_SCROLL_ON);
        pausedMessage.setBorder(bevelBorder);

        idleMessage = new JLabel(MessageFormat.format(STATUSBAR_IDLE_TIME, new String[] { "0m" }));
        idleMessage.setBorder(bevelBorder);

        elapsedMessage = new JLabel(MessageFormat.format(STATUSBAR_CONNECTED_TIME, new String[] { "0m" }));
        elapsedMessage.setBorder(bevelBorder);

        GridBagLayout layout = new GridBagLayout();
        setLayout(layout);

        GridBagConstraints c = new GridBagConstraints();
        c.fill = GridBagConstraints.HORIZONTAL;
        c.gridheight = 1;

        /* give the ConnectedMessage most of the layout by setting the weight */
        c.weightx = 1.0;
        add(connectedMessage, c);

        /* add the other labels to the layout with a weight of 0, forcing them
           to the left */
        c.weightx = 0.0;
        add(loggingMessage, c);
        add(pausedMessage, c);
        add(idleMessage, c);
        add(elapsedMessage, c);
    }

    public void statusConnecting(EventObject e) {
        connectedMessage.setText(STATUSBAR_CONNECTING);
    }

    public void statusConnected(EventObject e) {
        connectedMessage.setText(STATUSBAR_CONNECTED);
        connectedTime = System.currentTimeMillis();

        timer = new javax.swing.Timer(StatusBar.TIMER_INTERVAL, new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                long elapsedTime = (System.currentTimeMillis() - connectedTime) / 1000;
                String[] args = { formatTime(elapsedTime) };
                elapsedMessage.setText(MessageFormat.format(STATUSBAR_CONNECTED_TIME, args));

                long idleTime = (System.currentTimeMillis() - client.getLastMessageSentAt()) / 1000;
                args = new String[] { formatTime(idleTime) };
                idleMessage.setText(MessageFormat.format(STATUSBAR_IDLE_TIME, args));
            }
        });
        timer.start();
    }

    public void statusDisconnecting(EventObject e) {
        connectedMessage.setText(STATUSBAR_DISCONNECTING);
    }

    public void statusDisconnected(EventObject e) {
        connectedMessage.setText(STATUSBAR_DISCONNECTED);
        if (timer != null) {
            timer.stop();
            timer = null;
        }
    }

    public void statusLoggingStarted(EventObject e) {
        loggingMessage.setText(STATUSBAR_LOGGING_ON);
    }

    public void statusLoggingStopped(EventObject e) {
        loggingMessage.setText(STATUSBAR_LOGGING_OFF);
    }

    public void statusOutputPaused(EventObject e) {
        pausedMessage.setText(STATUSBAR_SCROLL_OFF);
    }

    public void statusOutputUnpaused(EventObject e) {
        pausedMessage.setText(STATUSBAR_SCROLL_ON);
    }

    private String formatTime(long elapsedTime) {
        Long minutes = new Long(elapsedTime / 60 % 60);
        Long hours = new Long(elapsedTime / 3600 % 24);
        Long days = new Long(elapsedTime / 86400);

        // TODO: would be nice to figure out a way to use ChoiceFormat to
        // make this i18n compliant
        StringBuffer buffer = new StringBuffer();
        if (days.longValue() > 0) {
            buffer.append(days).append("d ");
        }
        if (hours.longValue() > 0 || days.longValue() > 0) {
            buffer.append(hours).append("h ");
        }
        buffer.append(minutes).append('m');

        return buffer.toString();
    }
}
