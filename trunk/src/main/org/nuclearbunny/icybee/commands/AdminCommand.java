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

package org.nuclearbunny.icybee.commands;

import org.nuclearbunny.icybee.Client;
import tcl.lang.*;

public class AdminCommand implements Command {
    private static final String USAGE = "usage: s_admin drop|shutdown|wall args";
    private Client client;

    public AdminCommand(Client client) {
        this.client = client;
    }

    public void cmdProc(Interp interp, TclObject[] args) throws TclException {
        throw new TclNumArgsException(interp, 1, args, "s_admin is not yet implemented");
        // TODO: implement and test
/*
        if (args.length != 2) {
            throw new TclNumArgsException(interp, 1, args, usage);
        }

        String cmd = args[1].toString();

        if (cmd.equalsIgnoreCase("drop")) {
            String clientArgs = TclUtil.collapseArgs(2, args);
            client.sendCommandMessage("drop", clientArgs);
        } else if (cmd.equalsIgnoreCase("shutdown")) {
            String clientArgs = TclUtil.collapseArgs(2, args);
            client.sendCommandMessage("shutdown", clientArgs);
        } else if (cmd.equalsIgnoreCase("wall")) {
            String clientArgs = TclUtil.collapseArgs(2, args);
            client.sendCommandMessage("wall", clientArgs);
        } else {
            throw new TclException(interp, usage);
        }
*/
    }
}
