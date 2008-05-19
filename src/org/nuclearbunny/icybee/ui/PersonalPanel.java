/*
 * $Id: PersonalPanel.java,v 1.4 2003/01/06 03:36:07 dcgibbons Exp $
 *
 * IcyBee - http://www.nuclearbunny.org/icybee/
 * A client for the Internet CB Network - http://www.icb.net/
 *
 * Copyright � 2000-2003 David C. Gibbons, dcg@nuclearbunny.org
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

package org.nuclearbunny.icybee.ui;

import org.nuclearbunny.icybee.*;

public class PersonalPanel extends ClientPanel {
    private String otherUser;

    public PersonalPanel(ICBClient client, String otherUser) {
        super(client);
        this.otherUser = otherUser;
    }

    protected void submitInput(String s) {
        // if this output doesn't start with the command prefix, then
        // send the message directly to the specified user, otherwise
        // let the client process it normally
        char commandPrefix = client.getProperties().getCommandPrefix().charAt(0);
        if (s.charAt(0) != commandPrefix) {
            client.sendPersonalMessage(otherUser, s);
        } else {
            client.sendCommand(s);
        }
    }
}
