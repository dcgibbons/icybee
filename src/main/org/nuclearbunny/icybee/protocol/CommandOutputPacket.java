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

package org.nuclearbunny.icybee.protocol;

import org.nuclearbunny.util.*;
import java.lang.reflect.*;
import java.net.*;
import java.text.*;
import java.util.*;

public class CommandOutputPacket extends Packet {
	public static final String WHO_GROUP_HEADER = "gh";
	public static final String WHO_GROUP_INFO = "wg";
	public static final String WHO_HEADER = "wh";
	public static final String WHO_LISTING = "wl";
	public static final String COMMAND_OUTPUT = "co";

    public CommandOutputPacket(String rawPacket) throws ProtocolException {
        init(Packet.SERVER, rawPacket);
    }

    public String getCommandOutput() {
        return cmdOutput;
    }

    protected void init(int from, String rawPacket) throws ProtocolException {
        super.init(from, rawPacket);

        String cmd = rawPacket.substring(1, 3);

        if (cmd.compareTo("gh") == 0) {
            // group header
            cmdOutput = "Group     ## S  Moderator    ";

        } else if (cmd.compareTo("wg") == 0) {
            // group name from a who command
            StringBuffer buffer = new StringBuffer();
            buffer.append("Group: ");
            buffer.append(getField(1));
            if (getFieldCount() > 2) {
                buffer.append(" ").append(getField(2));
            }

        } else if (cmd.compareTo("wh") == 0) {
            // who header
            cmdOutput = "   Nickname      Idle      Sign-on  Account";

        } else if (cmd.compareTo("wl") == 0) {
            parseWhoListing(rawPacket);

        } else if (cmd.compareTo("ch") == 0) {
            //System.err.println("unsupported 'ch' command output received");

        } else if (cmd.compareTo("c") == 0) {
            cmdOutput = "/" + getField(1);

        } else if (cmd.compareTo("co") == 0) {
            cmdOutput = rawPacket.substring(4, rawPacket.length()-1); // XXX this is broken

        } else {
            cmdOutput = rawPacket.substring(1, rawPacket.length()-1);
        }
    }

    private String cmdOutput;

    private void parseWhoListing(String rawPacket) {
        StringBuffer buffer = new StringBuffer(80);

        /* indicate group moderator */
        buffer.append((getField(1).charAt(0) == 'm') ? '*' : ' ');

        /* append nickname, pad to 12 characters total */
        int fieldWidth = 12;
        String nick = getField(2);
        int nickLen = nick.length();
        if (nickLen > fieldWidth) {
            buffer.append(nick.substring(1, fieldWidth));
        } else {
            buffer.append(nick);
            buffer.append(getSpaces(fieldWidth-nickLen));
        }
        buffer.append(" ");

        /* append idle time */
        long idle = Long.parseLong(getField(3));
        if (idle < 60) {
            buffer.append("       - ");
        } else {
            long hours = idle/3600;
            if (hours > 0) {
                NumberFormat nf = NumberFormat.getNumberInstance();
                FieldPosition fp = new FieldPosition(NumberFormat.INTEGER_FIELD);
                nf.setMaximumIntegerDigits(3);
                String h = nf.format(hours, new StringBuffer(), fp).toString();
                buffer.append(getSpaces(3-fp.getEndIndex())).append(h)
                      .append("h ");
                idle -= 3600*hours;
            } else {
                buffer.append("     ");
            }

            long minutes = idle/60;
            if (minutes < 10) {
                buffer.append(' ');
            }
            buffer.append(minutes).append("m ");
        }

        /* append sign-on time */
        long now = System.currentTimeMillis();
        Date d = new Date(Long.parseLong(getField(5))*1000L);
        long days = (now-d.getTime())/86400000L; // milliseconds in a day
        if (days > 0) {
            NumberFormat nf = NumberFormat.getNumberInstance();
            FieldPosition fp = new FieldPosition(NumberFormat.INTEGER_FIELD);
            nf.setMaximumIntegerDigits(3);
            String ds = nf.format(days, new StringBuffer(), fp).toString();
            buffer.append(getSpaces(3-fp.getEndIndex())).append(ds).append('+');
        } else {
            buffer.append("    ");
        }

        SimpleDateFormat df = new SimpleDateFormat("hh:mm aa");
        df.setTimeZone(TimeZone.getDefault());
        buffer.append(df.format(d)).append(' ');

        /* append address */
        buffer.append(getField(6)).append("@").append(getField(7));

        /* append status */
        if (getFieldCount() > 8) {
            buffer.append("  ").append(getField(8));
        }

        cmdOutput = buffer.toString();
    }

    private static String getSpaces(int n) {
        StringBuffer buffer = new StringBuffer(n);
        for (int i = 0; i < n; i++) {
            buffer.append(' ');
        }
        return buffer.toString();
    }
}
