/*
 * $Id: IcyBee.java,v 1.4 2003/01/06 03:21:00 dcgibbons Exp $
 *
 * IcyBee - http://www.nuclearbunny.org/icybee/
 * A client for the Internet CB Network - http://www.icb.net/
 *
 * Copyright © 2000-2004 David C. Gibbons, dcg@nuclearbunny.org
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

import org.nuclearbunny.icybee.ui.*;
import org.nuclearbunny.icybee.ui.macosx.MacOSXIntegrator;

import java.net.*;
import java.util.*;
import java.lang.reflect.InvocationTargetException;
import javax.swing.*;

public class IcyBee {
    private static final ResourceBundle verInfo = ResourceBundle.getBundle("version");
    
    public IcyBee() throws Exception {
        // If running on Mac OS X, then we need to set the appName in a
        // special system property before any of the UI system starts.
        if (MacOSXIntegrator.isMacOSX()) {
            String appName = UIMessages.messages.getString(UIMessages.ICYBEE_APP_NAME);
            System.setProperty("com.apple.mrj.application.apple.menu.about.name", appName);
            MacOSXIntegrator.getInstance();
        }

        // create the communication client
        final ICBClient client = new ICBClient();

        // start the Swing user interface
        startSwingUI(client);

        // perform any required start processing based on user options
        client.checkStartupOptions();

        // the Swing thread loop will keep the application active now
    }

    private void startSwingUI(final ICBClient client) {
        Runnable r = new Runnable() {
            public void run() {
                JFrame mainFrame = new MainFrame(client);
                mainFrame.setVisible(true);

                // See if this is a new version for the user. If so, display a new license agreement
                // and "What's New" information.
                String thisRelease = verInfo.getString("Release");
                String lastReleaseUsed = client.getProperties().getLastReleaseUsed();
                if (!lastReleaseUsed.equals(thisRelease)) {
                    // Show the What's New page
                    URL whatsnewURL = this.getClass().getClassLoader().getResource("help/whatsnew.html");
                    JDialog whatsnewDialog = new HtmlDialog(mainFrame, whatsnewURL);
                    whatsnewDialog.setSize(400, 400); // XXX ack!
                    whatsnewDialog.setLocationRelativeTo(mainFrame);
                    whatsnewDialog.setVisible(true);

                    client.getProperties().setLastReleaseUsed(thisRelease);
                }
            }
        };

        try {
            SwingUtilities.invokeAndWait(r);
        } catch (Exception e) {
            // NO-OP
        }
    }

    public static boolean isDebugEnabled() {
        return System.getProperty("icybee.debug", "false").equals("true");
    }

    public static void main(String[] args) throws Exception {
        new IcyBee();
    }
}
