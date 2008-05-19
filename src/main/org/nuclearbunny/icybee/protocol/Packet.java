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

import org.nuclearbunny.icybee.protocol.*;

public class Packet {
    public static final int SERVER = 0;
    public static final int CLIENT = 1;

    /**
     * Creates a basic Packet from a raw string of packet data.
     *
     * @exception ProtocolException if the raw packet does not contain valid
     *                              data
     */
    public Packet(int from, String rawPacket) throws ProtocolException {
        init(from, rawPacket);
    }

    /**
     * Determines the type of packet this object represents.
     */
    public ICBProtocol getPacketType() {
        return type;
    }

    /**
     * Sets the type of packet this object represents.
     */
    public void setPacketType(ICBProtocol type) {
        this.type = type;
    }

    /**
     * Determines the number of data fields currently contained within this
     * packet object.
     */
    public int getFieldCount() {
        return fields.size();
    }

    /**
     * Returns the data for the specified field number or <i>null</i> if that
     * field does not exist.
     */
    public String getField(int n) {
        if (n >= 0 && n < fields.size()) {
            return (String) fields.get(n);
        } else {
            return null;
        }
    }

    public void setField(int n, String s) {
        fields.add(n, s); // XXX this should be smarter
    }

    public String toString() {
        StringBuffer buffer = new StringBuffer(256);
        buffer.append(getPacketType());
        for (int i = 0, n = fields.size(); i < n; i++) {
            buffer.append(getField(i));
            if (i + 1 < n) {
                buffer.append("\001");
            }
        }
        buffer.append("\000");
        return buffer.toString();
    }

    /**
     * Creates an appropriate <i>Packet</i> object from the data contained
     * within.
     */
    public static Packet getInstance(String rawPacket) throws ProtocolException {
        Character pktType = new Character(rawPacket.charAt(0));

        Object o = packets.get(pktType);
        if (o == null) {
            throw new ProtocolException("invalid packet type");
        }
        Class c = (Class) o;

        /* locate a constructor that takes a single String argument */
        Class[] argsClass = new Class[] { String.class };
        Object[] args = new Object[] { rawPacket };

        Packet p = null;
        try {
            Constructor strArgConstructor = c.getConstructor(argsClass);

            /* construct the concrete object and return it to the user */
            p = (Packet) strArgConstructor.newInstance(args);
        } catch (Exception ex) {
            throw new ProtocolException(ex.getMessage());
        }
        return p;
    }

    protected Packet() {
    }

    /**
     * Performs private object initialization. Subclasses should use this
     * to perform raw packet initialization when required.
     *
     * @exception ProtocolException if the raw packet does not contain valid
     *                              data
     */
    protected void init(int from, String rawPacket) throws ProtocolException {
        this.rawPacket = rawPacket;
        this.type = ICBProtocol.getPacketType(rawPacket);
        StringTokenizer st = new StringTokenizer(rawPacket.substring(1), "\000\001");
        while (st.hasMoreTokens()) {
            fields.add(st.nextToken());
        }
    }

    private ICBProtocol type;
    private ArrayList fields = new ArrayList(ICBProtocol.MAX_FIELDS);
    private String rawPacket;

    /**
     * A map of ICB packet types to the class objects that should be created
     * to handle those types of packets.
     */
    private static Hashtable packets = new Hashtable();
    static {
        packets.put(ICBProtocol.PKT_LOGIN, LoginPacket.class);
        packets.put(ICBProtocol.PKT_ERROR, ErrorPacket.class);
        packets.put(ICBProtocol.PKT_OPEN, OpenPacket.class);
        packets.put(ICBProtocol.PKT_PROTOCOL, ProtocolPacket.class);
        packets.put(ICBProtocol.PKT_IMPORTANT, StatusPacket.class);
        packets.put(ICBProtocol.PKT_STATUS, StatusPacket.class);
        packets.put(ICBProtocol.PKT_COMMAND_OUT, CommandOutputPacket.class);
        packets.put(ICBProtocol.PKT_PERSONAL, PersonalPacket.class);
        packets.put(ICBProtocol.PKT_BEEP, BeepPacket.class);
        packets.put(ICBProtocol.PKT_EXIT, ExitPacket.class);
        packets.put(ICBProtocol.PKT_COMMAND, CommandPacket.class);
        packets.put(ICBProtocol.PKT_PING, PingPacket.class);
    }
}
