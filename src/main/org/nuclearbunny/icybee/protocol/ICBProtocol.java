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

import java.util.HashMap;
import java.util.Map;
import java.util.Collections;

public enum ICBProtocol {
    PKT_LOGIN('a'),
    PKT_OPEN('b'),
    PKT_PERSONAL('c'),
    PKT_STATUS('d'),
    PKT_ERROR('e'),
    PKT_IMPORTANT('f'),
    PKT_EXIT('g'),
    PKT_COMMAND('h'),
    PKT_COMMAND_OUT('i'),
    PKT_PROTOCOL('j'),
    PKT_BEEP('k'),
    PKT_PING('l'),
    PKT_PONG('m'),
    PKT_NOOP('n');

    public char getPacketType() {
        return pktType;
    }

    private ICBProtocol(final char pktType) {
        this.pktType = pktType;
    }

    private final char pktType;

    public static ICBProtocol getPacketType(final String rawPacket) {
        ICBProtocol packetType = PACKET_TYPES.get(rawPacket.charAt(0));
        if (packetType == null) {
            throw new IllegalArgumentException("invalid packet type");
        }
        return packetType;
    }

    private final static Map<Character, ICBProtocol> PACKET_TYPES;

    static {
        Map<Character, ICBProtocol> packetTypes = new HashMap<Character, ICBProtocol>();
        packetTypes.put(PKT_LOGIN.getPacketType(), PKT_LOGIN);
        packetTypes.put(PKT_OPEN.getPacketType(), PKT_OPEN);
        packetTypes.put(PKT_PERSONAL.getPacketType(), PKT_PERSONAL);
        packetTypes.put(PKT_STATUS.getPacketType(), PKT_STATUS);
        packetTypes.put(PKT_ERROR.getPacketType(), PKT_ERROR);
        packetTypes.put(PKT_IMPORTANT.getPacketType(), PKT_IMPORTANT);
        packetTypes.put(PKT_EXIT.getPacketType(), PKT_EXIT);
        packetTypes.put(PKT_COMMAND.getPacketType(), PKT_COMMAND);
        packetTypes.put(PKT_COMMAND_OUT.getPacketType(), PKT_COMMAND_OUT);
        packetTypes.put(PKT_PROTOCOL.getPacketType(), PKT_PROTOCOL);
        packetTypes.put(PKT_BEEP.getPacketType(), PKT_BEEP);
        packetTypes.put(PKT_PING.getPacketType(), PKT_PING);
        packetTypes.put(PKT_PONG.getPacketType(), PKT_PONG);
        packetTypes.put(PKT_NOOP.getPacketType(), PKT_NOOP);
        PACKET_TYPES = Collections.unmodifiableMap(packetTypes);
    }

    public static final int MAX_FIELDS = 20;
    public static final int MAX_PACKET_SIZE = 255;
    public static final int MAX_NICK_SIZE = 12;
    public static final int MAX_OPEN_MESSAGE_SIZE = (MAX_PACKET_SIZE - (MAX_NICK_SIZE) - 2);
    public static final int MAX_PERSONAL_MESSAGE_SIZE = (MAX_PACKET_SIZE - (MAX_NICK_SIZE * 2) - 2);
    public static final int MAX_WRITE_MESSAGE_SIZE = (MAX_PACKET_SIZE - (MAX_NICK_SIZE * 2) - 2 - 14);
}
