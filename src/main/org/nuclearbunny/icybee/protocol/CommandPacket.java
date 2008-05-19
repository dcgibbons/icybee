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

import java.lang.reflect.*;
import java.net.*;
import java.util.*;


public class CommandPacket extends Packet {
    public CommandPacket() {
        setPacketType(ICBProtocol.PKT_COMMAND);
    }

    public CommandPacket(String rawPacket) throws ProtocolException {
        init(Packet.SERVER, rawPacket);
    }

    public CommandPacket(String command, String msg) throws ProtocolException {
        setPacketType(ICBProtocol.PKT_COMMAND);
        setField(0, command);
        if (msg != null) {
            setField(1, msg);
        }
    }
}
