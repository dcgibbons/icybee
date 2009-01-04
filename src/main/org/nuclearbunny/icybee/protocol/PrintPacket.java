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

public class PrintPacket extends Packet {
    public static final int MSG_TYPE_NORMAL = 0;
    public static final int MSG_TYPE_ERROR = 1;
    public static final int MSG_TYPE_PERSONAL = 2;
    public static final int MSG_TYPE_OPEN = 3;

    private int msgType;
    private String msg;
    private String[] args;

    public PrintPacket(int msgType, String msg) {
        this.msgType = msgType;
        this.msg = msg;
    }

    public PrintPacket(int msgType, String msg, String[] args) {
        this.msgType = msgType;
        this.msg = msg;
        this.args = args;
    }

    public int getMsgType() {
        return msgType;
    }

    public String toString() {
        return msg;
    }

    public String[] getArgs() {
        return args;
    }
}
