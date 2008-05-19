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

import org.nuclearbunny.icybee.TclUtil;
import org.nuclearbunny.icybee.Client;
import org.nuclearbunny.util.HexDump;
import tcl.lang.Command;
import tcl.lang.Interp;
import tcl.lang.TclException;
import tcl.lang.TclNumArgsException;
import tcl.lang.TclObject;

public class WriteCommand implements Command {
    private static final String USAGE = "usage: s_write nick message";
    private Client client;

    public WriteCommand(Client client) {
        this.client = client;
    }

    public void cmdProc(Interp interp, TclObject[] args) throws TclException {
        if (args.length < 3) {
            throw new TclNumArgsException(interp, 1, args, WriteCommand.USAGE);
        } else {
            String nick = args[1].toString();
            String msg = TclUtil.collapseArgs(2, args);
            client.sendWriteMessage(nick, msg);
        }
    }
}
