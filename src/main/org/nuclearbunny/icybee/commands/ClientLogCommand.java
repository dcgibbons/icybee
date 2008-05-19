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
import org.nuclearbunny.icybee.protocol.*;
import tcl.lang.*;

import java.io.IOException;

public class ClientLogCommand implements Command {
    private static final String USAGE = "usage: c_log [filename]"; // TODO
    private Client client;

    public ClientLogCommand(Client client) {
        this.client = client;
    }

    public void cmdProc(Interp interp, TclObject[] args) throws TclException {
        try {
            if (args.length == 1) {
                if (client.isLoggingEnabled()) {
                    client.stopLogging();
                } else {
                    client.startLogging();
                }
            } else {
                client.startLogging(args[1].toString());
            }
        } catch (IllegalStateException e) {
            client.printMessage(PrintPacket.MSG_TYPE_ERROR, "unable to toggle logging"); // TODO
            e.printStackTrace();
        } catch (IOException e) {
            client.printMessage(PrintPacket.MSG_TYPE_ERROR, "unable to open specified log file"); // TODO
            e.printStackTrace();
        }
    }
}
