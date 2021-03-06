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
import org.nuclearbunny.icybee.protocol.PrintPacket;
import tcl.lang.Command;
import tcl.lang.Interp;
import tcl.lang.TclException;
import tcl.lang.TclObject;

import java.text.NumberFormat;
import java.util.ResourceBundle;

public class ClientVersionCommand implements Command {
    private Client client;
    private ResourceBundle verInfo = ResourceBundle.getBundle("version");

    public ClientVersionCommand(Client client) {
        this.client = client;
    }

    public void cmdProc(Interp interp, TclObject[] args) throws TclException {
        double release = Double.parseDouble(verInfo.getString("Release"));
        String releaseType = verInfo.getString("ReleaseType");

        NumberFormat nf = NumberFormat.getInstance();
        nf.setMaximumFractionDigits(2);
        nf.setMinimumFractionDigits(2);

        StringBuffer buf = new StringBuffer();
        buf.append("IcyBee Version ")
                .append(nf.format(release))
                .append(' ')
                .append(releaseType)
                .append(", ")
                .append("Copyright (C) ")
                .append(verInfo.getString("ReleaseCopyrightYear"))
                .append(" David C. Gibbons. All rights reserved.");
        client.printMessage(PrintPacket.MSG_TYPE_NORMAL, buf.toString());
    }
}
