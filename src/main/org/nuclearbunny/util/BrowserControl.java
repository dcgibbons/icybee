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

package org.nuclearbunny.util;

import java.net.*;
import javax.jnlp.*;

public class BrowserControl { 
    public static boolean displayURL(String urlString) {
        try {
            URL url = new URL(urlString);
            return BrowserControl.displayURL(url);
        } catch (MalformedURLException ex) {
            System.out.println("Unable to display URL, " + ex.getMessage());
            ex.printStackTrace(System.err);
            return false;
        }
    }

    public static boolean displayURL(URL url) {
        boolean displayed;

        try {
            BasicService bs = (BasicService) ServiceManager.lookup("javax.jnlp.BasicService");

            if (bs.isWebBrowserSupported()) {
                displayed = bs.showDocument(url);
            } else {
                displayed = false;
            }
        } catch (UnavailableServiceException ex) {
            displayed = false;
        }

        return displayed;
    }
}
