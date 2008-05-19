/*
 * $Id: ClientVersionCommand.java,v 1.3 2003/01/06 03:27:30 dcgibbons Exp $
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

package org.nuclearbunny.icybee.commands;

import org.nuclearbunny.icybee.*;
import org.nuclearbunny.icybee.protocol.*;

import java.text.*;
import java.util.*;
import tcl.lang.*;

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
           .append("Copyright © ")
           .append(verInfo.getString("ReleaseCopyrightYear"))
           .append(" David C. Gibbons. All rights reserved.");
        client.printMessage(PrintPacket.MSG_TYPE_NORMAL, buf.toString());
    }
}
