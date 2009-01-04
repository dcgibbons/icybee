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

public class ClientUsageCommand implements Command {
    public ClientUsageCommand(Client client) {
        this.client = client;
    }

    public void cmdProc(Interp interp, TclObject[] args)
            throws TclException {
        if (args.length == 1) {
            throw new TclNumArgsException(interp, 1, args, usage);
        }

        String cmd = args[1].toString();
        if (cmd.equalsIgnoreCase("add")) {
            if (args.length != 6) {
                throw new TclNumArgsException(interp, 1, args,
                        "usage: c_usage add name type args usage...");
            }
            client.addUsage(args[2].toString(), args[3].toString(),
                    args[4].toString(), args[5].toString());

        } else if (cmd.equalsIgnoreCase("delete")) {
            if (args.length != 3) {
                throw new TclNumArgsException(interp, 1, args,
                        "usage: c_usage delete name");
            }
            client.removeUsage(args[2].toString());

        } else if (cmd.equalsIgnoreCase("list")) {
            if (args.length != 2) {
                throw new TclNumArgsException(interp, 1, args,
                        "usage: c_usage list failed");
            }
            client.listUsage(args[1].toString());
        }
    }

    private static final String usage =
            "usage: c_usage add|delete|list [name] [type args usage]";
    private Client client;
}
