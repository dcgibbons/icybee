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

package org.nuclearbunny.icybee.protocol;

import org.nuclearbunny.util.StringUtils;
import org.nuclearbunny.util.DateTimeUtils;

import java.net.ProtocolException;
import java.text.FieldPosition;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

public class CommandOutputPacket extends Packet {
    public static final String WHO_GROUP_HEADER = "gh";
    public static final String WHO_GROUP_INFO = "wg";
    public static final String WHO_HEADER = "wh";
    public static final String WHO_LISTING = "wl";
    public static final String COMMAND_OUTPUT = "co";

    public CommandOutputPacket(final String rawPacket) throws ProtocolException {
        super(rawPacket);

        cmdOutputType = rawPacket.substring(1, 3);

        if (cmdOutputType.compareTo("gh") == 0) {
            // group header
            cmdOutput = "Group     ## S  Moderator    ";

        } else if (cmdOutputType.compareTo("wg") == 0) {
            // group name from a who command
            StringBuffer buffer = new StringBuffer();
            buffer.append("Group: ");
            buffer.append(getField(1));
            if (getFieldCount() > 2) {
                buffer.append(" ").append(getField(2));
            }

        } else if (cmdOutputType.compareTo("wh") == 0) {
            // who header
            cmdOutput = "   Nickname      Idle      Sign-on  Account";

        } else if (cmdOutputType.compareTo("wl") == 0) {
            parseWhoListing();

        } else if (cmdOutputType.compareTo("ch") == 0) {
            //System.err.println("unsupported 'ch' command output received");

        } else if (cmdOutputType.compareTo("c") == 0) {
            cmdOutput = "/" + getField(1);

        } else if (cmdOutputType.compareTo("co") == 0) {
            cmdOutput = rawPacket.substring(4, rawPacket.length() - 1); // XXX this is broken

        } else {
            cmdOutput = rawPacket.substring(1, rawPacket.length() - 1);
        }
    }

    public String getCommandOutputType() {
        return cmdOutputType;
    }

    public String getCommandOutput() {
        return cmdOutput;
    }

    public boolean isModerator() {
        return (getField(1).charAt(0) == 'm');
    }

    public String getNickname() {
        return getField(2);
    }

    public long getIdleTime() {
        return Long.parseLong(getField(3));
    }

    public Date getSignOnTime() {
        return new Date(Long.parseLong(getField(5)) * 1000L);
    }

    public String getUsername() {
        return getField(6);
    }

    public String getHostname() {
        return getField(7);
    }

    public String getStatus() {
        return getField(8);
    }
    
    private String cmdOutputType;
    private String cmdOutput;

    private void parseWhoListing() {
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
            buffer.append(nick)
                  .append(StringUtils.repeatString(" ", fieldWidth - nickLen));
        }
        buffer.append(" ");

        /* append idle time */
        buffer.append(DateTimeUtils.formatElapsedTime(getIdleTime()))
              .append(" ");

        /* append sign-on time */
        buffer.append(DateTimeUtils.formatEventTime(getSignOnTime()))
              .append(" ");

        /* append address */
        buffer.append(getField(6)).append("@").append(getField(7));

        /* append status */
        if (getFieldCount() > 8) {
            buffer.append("  ").append(getField(8));
        }

        cmdOutput = buffer.toString();
    }
}
