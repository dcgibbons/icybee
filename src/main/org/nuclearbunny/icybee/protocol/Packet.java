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

import java.lang.reflect.Constructor;
import java.net.ProtocolException;
import java.util.*;

public class Packet {
    /**
     * Creates a basic Packet from a raw string of packet data.
     *
     * @param rawPacket the characters composing the raw packet
     * @throws ProtocolException if the raw packet does not contain valid
     *                           data
     */
    public Packet(final String rawPacket) throws ProtocolException {
        try {
            this.type = ICBProtocol.getPacketType(rawPacket);
        } catch (IllegalArgumentException ex) {
            throw new ProtocolException("invalid packet type");
        }

        StringTokenizer st = new StringTokenizer(rawPacket.substring(1), "\000\001");
        while (st.hasMoreTokens()) {
            fields.add(st.nextToken());
        }
    }

    /**
     * {@inheritDoc}
     */
    public String toString() {
        StringBuilder buffer = new StringBuilder(256);
        buffer.append(getPacketType().getPacketType());
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
     * Determines the type of packet this object represents.
     *
     * @return the protocol type of this packet
     */
    public ICBProtocol getPacketType() {
        return type;
    }

    /**
     * Sets the type of packet this object represents.
     *
     * @param type the protocol type of this packet
     */
    protected void setPacketType(ICBProtocol type) {
        this.type = type;
    }

    /**
     * Determines the number of data fields currently contained within this
     * packet object.
     *
     * @return the field count of the packet
     */
    protected int getFieldCount() {
        return fields.size();
    }

    /**
     * Returns the data for the specified field number or null if that
     * field does not exist.
     *
     * @param fieldNo the field number to extract
     * @return the raw texted for the specified field, or null
     */
    protected String getField(int fieldNo) {
        if (fieldNo >= 0 && fieldNo < fields.size()) {
            return fields.get(fieldNo);
        } else {
            return null;
        }
    }

    /**
     * Sets the text of the specified field.
     *
     * @param fieldNo the field number to set
     * @param text    the text to set in the specified field
     */
    protected void setField(int fieldNo, String text) {
        if (fieldNo < 0 || fieldNo > ICBProtocol.MAX_FIELDS) {
            throw new IllegalArgumentException("invalid field number");
        }
        fields.add(fieldNo, text);
    }

    /**
     * Creates an appropriate <i>Packet</i> object from the data contained
     * within.
     *
     * @param rawPacket the raw packet text, such as received from a network
     *                  source
     * @return a valid packet instance for this text
     * @throws ProtocolException if the raw packet text was malformed
     */
    public static Packet getInstance(final String rawPacket) throws ProtocolException {
        final char pktType = rawPacket.charAt(0);
        final Class c = PACKET_CLASSES.get(pktType);
        if (c == null) {
            throw new ProtocolException("invalid packet type");
        }

        // locate a constructor that takes a single String argument
        final Class[] argsClass = new Class[]{String.class};
        final Object[] args = new Object[]{rawPacket};

        try {
            final Constructor strArgConstructor = c.getConstructor(argsClass);
            return (Packet) strArgConstructor.newInstance(args);
        } catch (Exception ex) {
            throw new ProtocolException(ex.getMessage());
        }
    }

    protected Packet() {
    }

    private ICBProtocol type;
    private final ArrayList<String> fields = new ArrayList<String>(ICBProtocol.MAX_FIELDS);

    /**
     * A map of ICB packet types to the class objects that should be created
     * to handle those types of packets.
     */
    private static final Map<Character, Class> PACKET_CLASSES;

    static {
        Map<Character, Class> packets = new HashMap<Character, Class>();

        packets.put(ICBProtocol.PKT_LOGIN.getPacketType(), LoginPacket.class);
        packets.put(ICBProtocol.PKT_ERROR.getPacketType(), ErrorPacket.class);
        packets.put(ICBProtocol.PKT_OPEN.getPacketType(), OpenPacket.class);
        packets.put(ICBProtocol.PKT_PROTOCOL.getPacketType(), ProtocolPacket.class);
        packets.put(ICBProtocol.PKT_IMPORTANT.getPacketType(), StatusPacket.class);
        packets.put(ICBProtocol.PKT_STATUS.getPacketType(), StatusPacket.class);
        packets.put(ICBProtocol.PKT_COMMAND_OUT.getPacketType(), CommandOutputPacket.class);
        packets.put(ICBProtocol.PKT_PERSONAL.getPacketType(), PersonalPacket.class);
        packets.put(ICBProtocol.PKT_BEEP.getPacketType(), BeepPacket.class);
        packets.put(ICBProtocol.PKT_EXIT.getPacketType(), ExitPacket.class);
        packets.put(ICBProtocol.PKT_COMMAND.getPacketType(), CommandPacket.class);
        packets.put(ICBProtocol.PKT_PING.getPacketType(), PingPacket.class);

        PACKET_CLASSES = Collections.unmodifiableMap(packets);
    }
}
