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

package org.nuclearbunny.icybee.commands;

import org.nuclearbunny.icybee.*;
import tcl.lang.*;

public class GroupCommand implements Command {
    private static final String usage = "usage: s_group cancel|change|create|invite|pass|remove|status|topic args";

    private Client client;

    public GroupCommand(Client client) {
        this.client = client;
    }

    public void cmdProc(Interp interp, TclObject[] args) throws TclException {
        if (args.length < 2) {
            throw new TclNumArgsException(interp, 1, args, usage);
        }

        String cmd = args[1].toString();

        if (cmd.equalsIgnoreCase("cancel")) {
            if (args.length != 3) {
                throw new TclNumArgsException(interp, 1, args, "usage: s_group cancel nick");
            } else {
                client.sendCommandMessage("cancel", args[2].toString());
            }
        } else if (cmd.equalsIgnoreCase("change")) {
            if (args.length != 3) {
                throw new TclNumArgsException(interp, 1, args, "usage: s_group change group");
            } else {
                client.sendCommandMessage("g", args[2].toString());
            }
        } else if (cmd.equalsIgnoreCase("create")) {
            if (args.length != 3) {
                throw new TclNumArgsException(interp, 1, args, "usage: s_group create group");
            } else {
                client.sendCommandMessage("g", args[2].toString());
            }
        } else if (cmd.equalsIgnoreCase("invite")) {
            if (args.length != 3) {
                throw new TclNumArgsException(interp, 1, args, "usage: s_group invite nick");
            } else {
                client.sendCommandMessage("invite", args[2].toString());
            }
        } else if (cmd.equalsIgnoreCase("pass")) {
            if (args.length > 3) {
                throw new TclNumArgsException(interp, 1, args, "usage: s_group pass nick");
            } else if (args.length == 3) {
                client.sendCommandMessage("pass", args[2].toString());
            } else {
                client.sendCommandMessage("pass", "");
            }
        } else if (cmd.equalsIgnoreCase("remove")) {
            if (args.length < 3) {
                throw new TclNumArgsException(interp, 1, args, "usage: s_group remove nick [message]");
            } else {
                client.sendCommandMessage("boot", TclUtil.collapseArgs(2, args));
            }
        } else if (cmd.equalsIgnoreCase("status"))  {
            client.sendCommandMessage("status", TclUtil.collapseArgs(2, args));
        } else if (cmd.equalsIgnoreCase("topic")) {
            if (args.length < 3) {
                throw new TclNumArgsException(interp, 1, args, "usage: s_group topic newtopic...");
            } else {
                client.sendCommandMessage("topic", TclUtil.collapseArgs(2, args));
            }
        } else {
            throw new TclException(interp, usage);
        }
    }
}
