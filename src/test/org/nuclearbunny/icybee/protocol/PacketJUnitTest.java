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

import junit.framework.TestCase;

import java.net.ProtocolException;

public class PacketJUnitTest extends TestCase {
    public void testConstruction() throws ProtocolException {
        String rawPacket = ICBProtocol.PKT_PERSONAL.getPacketType() + "just testing\001this is just a test!\000";
        Packet p = new Packet(rawPacket);
        assertEquals(2, p.getFieldCount());
        assertEquals("just testing", p.getField(0));
        assertEquals("this is just a test!", p.getField(1));
        assertNull(p.getField(2));

        PersonalPacket pp = (PersonalPacket) Packet.getInstance(rawPacket);
        assertEquals("just testing", pp.getNick());
        assertEquals("this is just a test!", pp.getText());
    }

    public void testInvalidPacket() {
        String rawPacket = "zblah\001blah\000";

        try {
            Packet.getInstance(rawPacket);
            assertFalse("invalid packet test failed", true);
        } catch (ProtocolException e) {
            // successful test!
        }
    }

    public void testToString() throws ProtocolException {
        String rawPacket = ICBProtocol.PKT_PERSONAL.getPacketType() + "just testing\001this is just a test!\000";
        Packet p = Packet.getInstance(rawPacket);
        assertEquals(rawPacket, p.toString());
    }

    public void testSetField() {
        class MyTestPacket extends Packet {
            public MyTestPacket() {
                setPacketType(ICBProtocol.PKT_PROTOCOL);
            }
        }

        Packet p = new MyTestPacket();

        // test setField underflow
        try {
            p.setField(-1, "test");
            assertFalse("field underflow test failed", true);
        } catch (IllegalArgumentException ex) {
            // successful test!
        }

        // test setField overflow
        try {
            p.setField(ICBProtocol.MAX_FIELDS + 1, "test");
            assertFalse("field overflow test failed", true);
        } catch (IllegalArgumentException ex) {
            // successful test!
        }

        p.setField(0, "test");
    }
}
