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
import java.io.*;
import java.text.*;
import java.util.*;

public class OutputLogger implements MessageListener {
    private static final SimpleDateFormat sdf = new SimpleDateFormat(UIMessages.messages.getString(UIMessages.LOGGING_TIME_FORMAT));
    private static final String LOGGING_START = UIMessages.messages.getString(UIMessages.LOGGING_START);
    private static final String LOGGING_STOP = UIMessages.messages.getString(UIMessages.LOGGING_STOP);
    private static final String LOGGING_OPEN_MSG = UIMessages.messages.getString(UIMessages.LOGGING_OPEN_MSG);
    private static final String LOGGING_PERSONAL_MSG = UIMessages.messages.getString(UIMessages.LOGGING_PERSONAL_MSG);
    private static final String LOGGING_STATUS_MSG = UIMessages.messages.getString(UIMessages.LOGGING_STATUS_MSG);
    private static final String LOGGING_BEEP_MSG = UIMessages.messages.getString(UIMessages.LOGGING_BEEP_MSG);
    private static final String LOGGING_CONNECTED_MSG = UIMessages.messages.getString(UIMessages.LOGGING_CONNECTED_MSG);
    private static final String LOGGING_ERROR_MSG = UIMessages.messages.getString(UIMessages.LOGGING_ERROR_MSG);
    private static final String LOGGING_DISCONNECTED_MSG = UIMessages.messages.getString(UIMessages.LOGGING_DISCONNECTED_MSG);
    private static final String LOGGING_PRINT_MESSAGE = UIMessages.messages.getString(UIMessages.LOGGING_PRINT_MESSAGE);

    private ICBClient client;
    private PrintStream output;

    public OutputLogger(ICBClient client, String logFileName, boolean append) throws IOException {
        this.client = client;
        output = new PrintStream(new FileOutputStream(logFileName, append), true);
        output.println();
        output.println(MessageFormat.format(LOGGING_START, sdf.format(new Date())));
    }

    public void close() {
        output.println();
        output.println(MessageFormat.format(LOGGING_STOP, sdf.format(new Date())));
        output.flush();
        output = null;
    }

    /*
     * interface MessageListener
     */
    public void messageReceived(final MessageEvent e) {
        Packet p = e.getPacket();

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
            displayExitPacket((ExitPacket) p);

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
                    displayPrintMessage(pp);
                    break;

                case PrintPacket.MSG_TYPE_NORMAL:
                default:
                    output.println(p.toString());
                    break;
            }
        }
    }

    public void displayOpenPacket(OpenPacket p) {
        output.println(MessageFormat.format(LOGGING_OPEN_MSG, p.getNick(), p.getText()));
    }

    public void displayPersonalPacket(PersonalPacket p) {
        output.println(MessageFormat.format(LOGGING_PERSONAL_MSG, p.getNick(), p.getText()));
    }

    public void displayStatusPacket(StatusPacket p) {
        output.println(MessageFormat.format(LOGGING_STATUS_MSG, p.getStatusHeader(), p.getStatusText()));
    }

    public void displayCommandOutputPacket(CommandOutputPacket p) {
        output.println(p.getCommandOutput());
    }

    public void displayBeepPacket(BeepPacket p) {
        output.println(MessageFormat.format(LOGGING_BEEP_MSG, p.getNick()));
    }

    public void displayProtocolPacket(ProtocolPacket p) {
        output.println(MessageFormat.format(LOGGING_CONNECTED_MSG, p.getServerName(), p.getServerDesc()));
    }

    public void displayErrorMessage(String msg) {
        output.println(MessageFormat.format(LOGGING_ERROR_MSG, msg));
    }

    public void displayExitPacket(ExitPacket p) {
        output.println(LOGGING_DISCONNECTED_MSG);
    }

    private void displayMessageTimestamp() {
        if (client.getProperties().areMessageTimestampsEnabled()) {
            SimpleDateFormat dateFormat = new SimpleDateFormat(client.getProperties().getMessageTimestampsFormat());
            dateFormat.setTimeZone(TimeZone.getDefault());
            output.print(dateFormat.format(new Date()));
            output.print(' ');
        }
    }

    private void displayPersonalMessage(PrintPacket p) {
        String[] args = p.getArgs();
        if (args.length >= 1) {
            String nick = args[0];
            String text = p.toString();

            output.println(MessageFormat.format(LOGGING_PERSONAL_MSG, nick, text));
        }
    }

    // TODO: this is one of those hacks I put in here to deal with c_print...
    private void displayPrintMessage(PrintPacket p) {
        output.println(MessageFormat.format(LOGGING_PRINT_MESSAGE, p.toString()));
    }
}
