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

public class ICBProtocol {
    public static final Character PKT_LOGIN = new Character('a');
    public static final Character PKT_OPEN = new Character('b');
    public static final Character PKT_PERSONAL = new Character('c');
    public static final Character PKT_STATUS = new Character('d');
    public static final Character PKT_ERROR = new Character('e');
    public static final Character PKT_IMPORTANT = new Character('f');
    public static final Character PKT_EXIT = new Character('g');
    public static final Character PKT_COMMAND = new Character('h');
    public static final Character PKT_COMMAND_OUT = new Character('i');
    public static final Character PKT_PROTOCOL = new Character('j');
    public static final Character PKT_BEEP = new Character('k');
    public static final Character PKT_PING = new Character('l');
    public static final Character PKT_PONG = new Character('m');
    public static final Character PKT_NOOP = new Character('n');

    public static final int MAX_FIELDS = 20;
    public static final int MAX_PACKET_SIZE = 255;
    public static final int MAX_NICK_SIZE = 12;
    public static final int MAX_OPEN_MESSAGE_SIZE = (MAX_PACKET_SIZE - (MAX_NICK_SIZE) - 2);
    public static final int MAX_PERSONAL_MESSAGE_SIZE = (MAX_PACKET_SIZE - (MAX_NICK_SIZE * 2) - 2);
    public static final int MAX_WRITE_MESSAGE_SIZE = (MAX_PACKET_SIZE - (MAX_NICK_SIZE * 2) - 2 - 14);
}
