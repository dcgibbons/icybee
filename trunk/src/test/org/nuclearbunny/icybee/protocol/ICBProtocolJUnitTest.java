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

public class ICBProtocolJUnitTest extends TestCase {
    public void testInvalidPacket() {
        try {
            ICBProtocol.getPacketType("z");
            assertFalse("ICBProtocol.getPacketType did not throw expected exception", true);
        } catch (IllegalArgumentException ex) {
            // NO-OP - test successful
            assertNotNull(ex);
        }
    }

    public void testValidPacketTypes() {
        for (char pt = 'a'; pt < 'o'; pt++) {
            assertNotNull(ICBProtocol.getPacketType(new String(new char[]{pt})));
        }
    }

    public void testBasics() {
        ICBProtocol pktType = ICBProtocol.PKT_LOGIN;
        assertEquals("PKT_LOGIN should be equal to 'a'", pktType.getPacketType(), 'a');
    }
}
