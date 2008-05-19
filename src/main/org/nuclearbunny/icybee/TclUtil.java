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

package org.nuclearbunny.icybee;

import org.nuclearbunny.icybee.commands.*;
import tcl.lang.*;
import java.io.*;

public class TclUtil {
    private Interp interp = new Interp();
    private Client client;

    public TclUtil(ICBClient client) {
        this.client = client;

        /* tcl commands handled by the client */
        interp.createCommand("c_hclear", new ClientHistoryClearCommand(client));
        interp.createCommand("c_hdel", new ClientHistoryRemoveCommand(client));
        interp.createCommand("c_log", new ClientLogCommand(client));
        interp.createCommand("c_print", new ClientPrintCommand(client));
        interp.createCommand("c_time", new ClientTimeCommand(client));
        interp.createCommand("c_usage", new ClientUsageCommand(client));
        interp.createCommand("c_version", new ClientVersionCommand(client));

        /* tcl commands sent to the server */
        interp.createCommand("s_admin", new AdminCommand(client));
        interp.createCommand("s_beep", new BeepCommand(client));
        interp.createCommand("s_chpw", new ChangePasswordCommand(client));
        interp.createCommand("s_drop", new DropCommand(client));
        interp.createCommand("s_echoback", new EchobackCommand(client));
        interp.createCommand("s_motd", new MotdCommand(client));
        interp.createCommand("s_group", new GroupCommand(client));
        interp.createCommand("s_nick", new NickCommand(client));
        interp.createCommand("s_open", new OpenCommand(client));
        interp.createCommand("s_personal", new PersonalCommand(client));
        interp.createCommand("s_read", new ReadCommand(client));
        interp.createCommand("s_register", new RegisterCommand(client));
        interp.createCommand("s_send", new SendCommand(client));
        interp.createCommand("s_version", new VersionCommand(client));
        interp.createCommand("s_who", new WhoCommand(client));
        interp.createCommand("s_write", new WriteCommand(client));

        try {
            evalResource("/icb_init.tcl");

        } catch (TclException ex) {
            // XXX handle this better
            System.err.println("Unable to evaluate Tcl startup script.");
            System.err.println(interp.getResult());
            ex.printStackTrace();
            System.exit(1);
        }
    }

    public Interp getTclInterp() {
        return interp;
    }

    public boolean executeCommand(String tclCmd) {
        String massaged = massageCommand(tclCmd);

        boolean success = true;
        String result = null;
        try {
            interp.eval(massaged);
            result = interp.getResult().toString();
        } catch (TclException ex) {
            success = false;
            System.err.println("Unable to execute TCl command: \"" + tclCmd + "\"");
            System.err.println("TCL result: " + result);
            ex.printStackTrace(System.err);
        }

        return success;
    }

    /* Extracted from the jacl code -- it would be nice if they made this
       public! */
    public void evalResource(String resName) throws TclException {
        InputStream stream = null;

        try {
            Class c = getClass();
            stream = c.getResourceAsStream(resName);
        } catch (SecurityException e2) {
            return;
        }

        if (stream == null) {
            throw new TclException(interp, "cannot read resource \"" + resName + "\"");
        }

        try {
            int num = stream.available();
            byte[] byteArray = new byte[num];
            int offset = 0;
            int readLen;
            while (num > 0) {
                readLen = stream.read( byteArray, offset, num );
                offset += readLen;
                num -= readLen;
            }

            interp.eval(new String(byteArray), 0);
        } catch (IOException e) {
            return;
        } finally {
            try {
                stream.close();
            } catch (IOException ex) {
            }
        }
    }

    public void evalFile(String fileName) throws TclException  {
        InputStream stream = null;

        try {
            stream = new FileInputStream(fileName);
        } catch (IOException ex) {
            throw new TclException(interp, "cannot read file \"" + fileName + "\"");
        }

        try {
            int num = stream.available();
            byte[] byteArray = new byte[num];
            int offset = 0;
            int readLen;
            while (num > 0) {
                readLen = stream.read( byteArray, offset, num );
                offset += readLen;
                num -= readLen;
            }

            interp.eval(new String(byteArray), 0);
        } catch (IOException e) {
            return;
        } finally {
            try {
                stream.close();
            } catch (IOException ex) {
            }
        }
    }

    protected String massageCommand(String cmd) {
        // allocate a string buffer with a little extra space
        StringBuffer buf = new StringBuffer(cmd.length() + 16);

        if (cmd.startsWith("tcl ")) {
            buf.append(cmd.substring(4));
        } else {
            char c;
            for (int i = 0, n = cmd.length(); i < n; i++) {
                c = cmd.charAt(i);
                switch (c) {
                    case '{': case '}':
                    case '[': case ']':
                    case '"': case ';':
                    case '$': case '\\':
                        buf.append('\\').append(c);
                        break;
                    default:
                        if (Character.isISOControl(c)) {
                            buf.append('\\');
                        }
                        buf.append(c);
                        break;
                }
            }
        }

        return buf.toString();
    }

    public static String collapseArgs(String[] args) {
        StringBuffer b = new StringBuffer();
        for (int i = 0; i < args.length; i++) {
            b.append(args[i]);
            if (i + 1 < args.length) {
                b.append(' ');
            }
        }
        return b.toString();
    }

    public static String collapseArgs(int start, TclObject[] args) {
        StringBuffer b = new StringBuffer();
        for (int i = start; i < args.length; i++) {
            b.append(args[i].toString());
            if (i + 1 < args.length) {
                b.append(' ');
            }
        }
        return b.toString();
    }
}
