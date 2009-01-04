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

package org.nuclearbunny.icybee.ui.macosx;

import javax.swing.*;

public class MacOSXIntegrator {
    private static MacOSXIntegrator thisInstance;
    private MacOSXIntegratorImpl impl;

    private MacOSXIntegrator() {
        if (isMacOSX()) {
            impl = new MacOSXIntegratorImpl();
        }
    }

    public static boolean isMacOSX() {
        return (System.getProperty("os.name").indexOf("Mac OS X") != -1);
    }

    public static MacOSXIntegrator getInstance() {
        if (isMacOSX() && thisInstance == null) {
            thisInstance = new MacOSXIntegrator();
        }

        return thisInstance;
    }

    public void setAboutAction(Action aboutAction) {
        if (impl != null) {
            impl.setAboutAction(aboutAction);
        }
    }

    public void setPreferencesAction(Action preferencesAction) {
        if (impl != null) {
            impl.setPreferencesAction(preferencesAction);
        }
    }

    public void setQuitAction(Action quitAction) {
        if (impl != null) {
            impl.setQuitAction(quitAction);
        }
    }
}
