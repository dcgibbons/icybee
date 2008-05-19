/*
 * $Id: ConnectionState.java,v 1.2 2002/09/22 02:14:08 dcgibbons Exp $
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

package org.nuclearbunny.icybee;

public class ConnectionState  {
    public final String name;

    private ConnectionState(String name) { 
        this.name = name;
    }

    public String toString() {
        return name;
    }

    public static final ConnectionState CONNECTING = new ConnectionState("connecting");
    public static final ConnectionState CONNECTED = new ConnectionState("connected");
    public static final ConnectionState DISCONNECTING = new ConnectionState("disconnecting");
    public static final ConnectionState DISCONNECTED = new ConnectionState("disconnected");
}
