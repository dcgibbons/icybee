/*
 * $Id: HexDump.java,v 1.2 2002/05/07 02:01:33 dcgibbons Exp $
 *
 * IcyBee - http://www.nuclearbunny.org/icybee/
 * A client for the Internet CB Network - http://www.icb.net/
 *
 * Copyright © 2000-2003 David C. Gibbons, dcg@nuclearbunny.org
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

package org.nuclearbunny.util;

import java.io.*;

/**
 * HexDump provides a dump method thyat displays a byte array
 * using a traditional hex dump format.
 *
 * @author David C. Gibbons
 * @version 1.0
 */
public class HexDump {
    /**
     * The dump method displays the provided byte buffer to the
     * specified PrintStream.
     */
    public static void dump(PrintStream out, byte[] buf) {
        int i;
        int n;
        StringBuffer outbuf;
        String ofs;

        for (n = 0; n < buf.length; n+= HexDump.BYTE_COUNT) {
            outbuf = new StringBuffer();

            ofs = Integer.toHexString(n).toUpperCase();
            for (int j = 0, k = 4-ofs.length(); j < k; j++) {
                outbuf.append("0");
            }
            outbuf.append(ofs).append(" ");

            for (i = n; i < n + HexDump.BYTE_COUNT && i < buf.length; i++) {
                int v = (buf[i] < 0) ? buf[i] + 256 : buf[i];
                String s = Integer.toHexString(v).toUpperCase();
                if (s.length() == 1) {
                    outbuf.append("0");
                }
                outbuf.append(s).append(" ");
            }

            while (i < n + HexDump.BYTE_COUNT) {
                outbuf.append("   ");
                i++;
            }

            for (i = n; i < n + HexDump.BYTE_COUNT && i < buf.length; i++) {
                int v = (buf[i] < 0) ? buf[i] + 256 : buf[i];
                boolean visible = true;
                if (v > 127 || Character.isISOControl((char) buf[i])) {
                    visible = false;
                }
                if (visible) {
                    outbuf.append((char) v);
                } else {
                    outbuf.append(".");
                }
            }

            while (i < n + HexDump.BYTE_COUNT) {
                outbuf.append(" ");
                i++;
            }

           out.println(outbuf.toString());
        }
    }

    private static final int BYTE_COUNT = 16;
}
