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

import org.nuclearbunny.icybee.protocol.*;
import java.util.regex.Pattern;
import java.util.regex.Matcher;

import tcl.lang.*;

public class TclTriggerListener implements MessageListener {
    private ICBClient theClient;
    private Interp interp;

    private static final Pattern rsvpPattern = Pattern.compile("You are invited to group (\\w*) by (\\w*)",
                                                               Pattern.CASE_INSENSITIVE);
    private static final Pattern arrivePattern = Pattern.compile("(\\w*) .*");
    private static final Pattern bootPattern = Pattern.compile("(\\w*) booted you\\.");

    public TclTriggerListener(ICBClient client) {
        theClient = client;
        interp = theClient.getTclManager().getTclInterp();
    }

    public void messageReceived(MessageEvent e) {
        Packet p = e.getPacket();
        try {
            // set the global variable "theMessage" for all of the possible TCL
            // trigger procedures
            TclObject theMessage = TclString.newInstance(p.toString());
            interp.setVar("theMessage", theMessage, TCL.GLOBAL_ONLY);

            if (p instanceof OpenPacket) {
                processOpenPacket((OpenPacket)p);
            } else if (p instanceof PersonalPacket) {
                processPersonalPacket((PersonalPacket)p);
            } else if (p instanceof BeepPacket) {
                processBeepPacket((BeepPacket)p);
            } else if (p instanceof ErrorPacket) {
                processErrorPacket((ErrorPacket)p);
            } else if (p instanceof StatusPacket) {
                processStatusPacket((StatusPacket)p);
            }
        } catch (TclException ex) {
            // TODO System.err.println("Uncaught TCL exception, " + ex);
        }
    }

    private void processOpenPacket(OpenPacket p) throws TclException {
        String person = p.getNick();
        interp.eval("Trig_openmsg " + person);
    }

    private void processPersonalPacket(PersonalPacket p) throws TclException {
        String person = p.getNick();
        interp.eval("Trig_personalmsg " + person);
    }

    private void processBeepPacket(BeepPacket p) throws TclException {
        String person = p.getNick();
        interp.eval("Trig_beepmsg " + person);
    }

    private void processErrorPacket(ErrorPacket p) throws TclException {
        interp.eval("Trig_errormsg");
    }

    private void processStatusPacket(StatusPacket p) throws TclException {
        if (p.getPacketType() == ICBProtocol.PKT_IMPORTANT) {
            interp.eval("Trig_importantmsg");

        } else if (p.getStatusHeader().equalsIgnoreCase("Arrive") || p.getStatusHeader().equalsIgnoreCase("Sign-On")) {
            Matcher matcher = arrivePattern.matcher(p.getStatusText());
            if (matcher.matches()) {
                String person = matcher.group(1);
                interp.eval("Trig_arrivemsg " + person);
            }

        } else if (p.getStatusHeader().equalsIgnoreCase("Depart") || p.getStatusHeader().equalsIgnoreCase("Sign-Off")) {
            Matcher matcher = arrivePattern.matcher(p.getStatusText());
            if (matcher.matches()) {
                String person = matcher.group(1);
                interp.eval("Trig_leavemsg " + person);
            }

        } else if (p.getStatusHeader().equalsIgnoreCase("Boot")) {
            Matcher matcher = bootPattern.matcher(p.getStatusText());
            if (matcher.matches()) {
                String person = matcher.group(1);
                interp.eval("Trig_bootmsg " + person);
            }

        } else if (p.getStatusHeader().equalsIgnoreCase("Drop")) {
            interp.eval("Trig_dropmsg");

        } else if (p.getStatusHeader().equalsIgnoreCase("RSVP")) {
            Matcher matcher = rsvpPattern.matcher(p.getStatusText());
            if (matcher.matches()) {
                String group = matcher.group(1);
                String person = matcher.group(2);
                interp.eval("Trig_invitemsg " + person + " " + group);
            }
        }
    }
}

/*
<*Dr.Strange*> openmsg personalmsg URL beepmsg invitemsg arrivemsg dropmsg statusmsg bootmsg errormsg importantmsg
<*Dr.Strange*> open, personal, beep, arrive, take single parameter that is nickname. URL takes a single parameter that is the URL in question.
<*Dr.Strange*> The rest have no parameters (except for invite whch has 2).
<*Dr.Strange8> however thre are global variables available to the triggers with more information.

tcl_linkvar(interp, "varName", &address, TCL_LINK_STRING|TCL_LINK_READ_ONLY);


===========================================================================
proc Trig_personalmsg {person} {
	global theMessage
	...
}

	This function gets called when a personal message (/m) gets sent
to you.  Trig_personalmsg gets the sender's nickname passed in $person,
and a read-only copy of the message body text lives in $theMessage.


===========================================================================
proc Trig_beepmsg {person} { ... }

	This function gets the nickname of the person that beeped you sent
in via $person.



===========================================================================
proc Trig_dropmsg {} { ... }
proc Trig_errormsg {} { ... }
proc Trig_importantmsg {} { ... }

	No arguments are passed to these functions currently. The dropmsg
gets called when the server sends you [=Drop=] warning that you're about to
be knocked off the system.   errormsg/importantmsg don't seem terribly
useful, but it doesn't seem to hurt anything to have hooks available.


===========================================================================
proc Trig_invitemsg {person group} { ... }

	This gets the person that invited you in $person, and the group
that you've been invited to in $group.
*/

